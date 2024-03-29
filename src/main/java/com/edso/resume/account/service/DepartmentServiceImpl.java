package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.CreateDepartmentRequest;
import com.edso.resume.account.domain.request.DeleteDepartmentRequest;
import com.edso.resume.account.domain.request.UpdateDepartmentRequest;
import com.edso.resume.account.entities.DepartmentEntity;
import com.edso.resume.account.entities.SubDepartmentEntity;
import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.common.CollectionNameDefs;
import com.edso.resume.lib.common.DbKeyConfig;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.entities.PagingInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.google.common.base.Strings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DepartmentServiceImpl extends BaseService implements DepartmentService {

    private final MongoDbOnlineSyncActions db;

    public DepartmentServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public GetArrayResponse<DepartmentEntity> findAll(HeaderInfo info, String idCompany, Integer page, Integer size) {
        GetArrayResponse<DepartmentEntity> resp = new GetArrayResponse<>();
        Bson cond = Filters.eq(DbKeyConfig.COMPANY_ID, idCompany);
        Document company = db.findOne(CollectionNameDefs.COLL_COMPANY, Filters.eq(DbKeyConfig.ID, idCompany));
        if (company == null) {
            resp.setFailed("Không tồn tại công ty này");
            return resp;
        }
        PagingInfo pagingInfo = PagingInfo.parse(page, size);
        FindIterable<Document> lst = db.findAll2(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());
        List<DepartmentEntity> rows = new ArrayList<>();
        if (lst != null) {
            for (Document doc : lst) {
                String idParent = AppUtils.parseString(doc.get(DbKeyConfig.PARENT_ID));
                if (Strings.isNullOrEmpty(idParent)) {
                    DepartmentEntity department = DepartmentEntity.builder()
                            .id(AppUtils.parseString(doc.get(DbKeyConfig.ID)))
                            .idCompany(AppUtils.parseString(doc.get(DbKeyConfig.COMPANY_ID)))
                            .name(AppUtils.parseString(doc.get(DbKeyConfig.NAME)))
                            .child(new ArrayList<>())
                            .build();
                    rows.add(department);
                } else {
                    for (DepartmentEntity departmentEntity : rows) {
                        try {
                            if (departmentEntity.getId().equals(idParent)) {
                                List<SubDepartmentEntity> list = departmentEntity.getChild();
                                SubDepartmentEntity subDepartmentEntity = SubDepartmentEntity.builder()
                                        .id(AppUtils.parseString(doc.get(DbKeyConfig.ID)))
                                        .name(AppUtils.parseString(doc.get(DbKeyConfig.NAME)))
                                        .build();
                                list.add(subDepartmentEntity);
                                departmentEntity.setChild(list);
                            }
                        } catch (Throwable e) {
                            logger.error("Ex: ", e);
                        }
                    }
                }
            }
        }
        resp.setSuccess();
        resp.setTotal(rows.size());
        resp.setRows(rows);
        return resp;
    }

    @Override
    public BaseResponse createDepartment(CreateDepartmentRequest request, String idParent) {

        BaseResponse response = new BaseResponse();
        Document company = db.findOne(CollectionNameDefs.COLL_COMPANY, Filters.eq(DbKeyConfig.ID, request.getIdCompany()));
        if (company == null) {
            response.setFailed("Không tồn tại id company này");
            return response;
        }

        String parentName = null;
        if (!Strings.isNullOrEmpty(idParent)) {
            Document doc = db.findOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, Filters.eq(DbKeyConfig.ID, idParent));
            if (doc != null && Strings.isNullOrEmpty(AppUtils.parseString(doc.get(DbKeyConfig.PARENT_ID)))) {
                parentName = AppUtils.parseString(doc.get(DbKeyConfig.NAME));
            } else {
                response.setFailed("Không tồn tại id parent này");
                return response;
            }
        }

//        String name = request.getName();
//        Bson c = Filters.eq(DbKeyConfig.NAME_SEARCH, name.toLowerCase());
//        long count = db.countAll(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, c);
//
//        if (count > 0) {
//            response.setFailed("Tên này đã tồn tại");
//            return response;
//        }

        Document department = new Document();
        department.append(DbKeyConfig.ID, UUID.randomUUID().toString());
        department.append(DbKeyConfig.NAME, request.getName());
        department.append(DbKeyConfig.COMPANY_ID, request.getIdCompany());
        department.append(DbKeyConfig.PARENT_ID, idParent);
        department.append(DbKeyConfig.PARENT_NAME, parentName);
//        department.append(DbKeyConfig.NAME_SEARCH, name.toLowerCase());
        department.append(DbKeyConfig.CREATE_AT, System.currentTimeMillis());
        department.append(DbKeyConfig.CREATE_BY, request.getInfo().getUsername());

        // insert to database
        db.insertOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, department);

        response.setSuccess();
        return response;

    }


    @Override
    public BaseResponse updateDepartment(UpdateDepartmentRequest request, String idParent) {

        BaseResponse response = new BaseResponse();
        String id = request.getId();
        Bson cond = Filters.eq(DbKeyConfig.ID, id);
        Document idDocument = db.findOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond);

        if (idDocument == null) {
            response.setFailed("Id này không tồn tại");
            return response;
        }

//        String name = request.getName();
//        Document obj = db.findOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, Filters.eq(DbKeyConfig.NAME_SEARCH, name.toLowerCase()));
//        if (obj != null) {
//            String objId = AppUtils.parseString(obj.get(DbKeyConfig.ID));
//            if (!objId.equals(id)) {
//                response.setFailed("Tên này đã tồn tại");
//                return response;
//            }
//        }

        Bson updates;
        if (!Strings.isNullOrEmpty(idParent)) {
            Document doc = db.findOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, Filters.eq(DbKeyConfig.PARENT_ID, idParent));
            if (doc != null) {
                String parentName = AppUtils.parseString(doc.get(DbKeyConfig.NAME));
                // update roles
                updates = Updates.combine(
                        Updates.set(DbKeyConfig.NAME, request.getName()),
                        Updates.set(DbKeyConfig.PARENT_NAME, parentName),
//                Updates.set(DbKeyConfig.NAME_SEARCH, name.toLowerCase()),
                        Updates.set(DbKeyConfig.UPDATE_AT, System.currentTimeMillis()),
                        Updates.set(DbKeyConfig.UPDATE_BY, request.getInfo().getUsername())
                );
            } else {
                response.setFailed("Không tồn tại id parent này");
                return response;
            }
        } else {
            if (Strings.isNullOrEmpty(AppUtils.parseString(idDocument.get(DbKeyConfig.PARENT_ID)))) {
                // update roles
                updates = Updates.combine(
                        Updates.set(DbKeyConfig.NAME, request.getName()),
//                Updates.set(DbKeyConfig.NAME_SEARCH, name.toLowerCase()),
                        Updates.set(DbKeyConfig.UPDATE_AT, System.currentTimeMillis()),
                        Updates.set(DbKeyConfig.UPDATE_BY, request.getInfo().getUsername())
                );

                Bson con = Filters.eq(DbKeyConfig.PARENT_ID, request.getId());
                Bson update = Updates.combine(
                        Updates.set(DbKeyConfig.PARENT_NAME, request.getName())
                );
                db.update(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, con, update, true);
            } else {
                response.setFailed("Id này không tồn tại");
                return response;
            }
        }

        db.update(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond, updates, true);

        response.setSuccess();
        return response;

    }

    @Override
    public BaseResponse deleteDepartment(DeleteDepartmentRequest request) {
        BaseResponse response = new BaseResponse();
        String id = request.getId();
        Bson cond = Filters.eq(DbKeyConfig.ID, id);
        Document idDocument = db.findOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond);

        if (idDocument == null) {
            response.setFailed("Id này không tồn tại");
            return response;
        }

        db.delete(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond);

        Bson con = Filters.eq(DbKeyConfig.PARENT_ID, id);
        db.delete(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, con);

        return new BaseResponse(0, "OK");
    }

    @Override
    public void deleteDepartmentByIdCompany(String idCompany) {
        Bson cond = Filters.eq(DbKeyConfig.COMPANY_ID, idCompany);
        Document idDocument = db.findOne(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond);
        db.delete(CollectionNameDefs.COLL_DEPARTMENT_COMPANY, cond);
        logger.info("deleteDepartmentByIdCompany idCompany: {}", idCompany);
    }
}
