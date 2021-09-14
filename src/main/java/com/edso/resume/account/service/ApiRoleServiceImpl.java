package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.CreateApiRoleRequest;
import com.edso.resume.account.domain.request.DeleteApiRoleRequest;
import com.edso.resume.account.domain.request.UpdateApiRoleRequest;
import com.edso.resume.account.entities.ApiRoleEntity;
import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.common.CollectionNameDefs;
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
import java.util.regex.Pattern;

@Service
public class ApiRoleServiceImpl extends BaseService implements ApiRoleService {

    private final MongoDbOnlineSyncActions db;

    public ApiRoleServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public GetArrayResponse<ApiRoleEntity> list(HeaderInfo headerInfo, String name, String id, Integer page, Integer size) {

        GetArrayResponse<ApiRoleEntity> response = new GetArrayResponse<>();

        List<ApiRoleEntity> rows = new ArrayList<>();

        List<Bson> c = new ArrayList<>();

        if (!Strings.isNullOrEmpty(id)) {
            c.add(Filters.eq("id", id));
        }

        if (!Strings.isNullOrEmpty(name)) {
            c.add(Filters.regex("name", Pattern.compile(name)));
        }

        Bson cond = buildCondition(c);

        long total = db.countAll(CollectionNameDefs.COLL_API_ROLE, cond);

        PagingInfo pagingInfo = PagingInfo.parse(page, size);

        FindIterable<Document> lst = db.findAll2(CollectionNameDefs.COLL_API_ROLE, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());

        for (Document doc : lst) {
            ApiRoleEntity entity = ApiRoleEntity.builder()
                    .id(AppUtils.parseString(doc.get("id")))
                    .path(AppUtils.parseString(doc.get("path")))
                    .method(AppUtils.parseString(doc.get("method")))
                    .name(AppUtils.parseString(doc.get("name")))
                    .build();
            rows.add(entity);

        }
        response.setTotal(total);
        response.setRows(rows);
        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse createApiRole(CreateApiRoleRequest request) {

        String path = request.getPath();
        Bson cond = Filters.and(
                Filters.eq("path", path),
                Filters.eq("method", request.getMethod())
        );
        Document role = db.findOne(CollectionNameDefs.COLL_API_ROLE, cond);

        if (role != null) {
            return new BaseResponse(-1, "Role này đã tồn tại");
        }

        role = new Document();
        role.append("id", UUID.randomUUID().toString());
        role.append("path", request.getPath());
        role.append("method", request.getMethod());
        role.append("name", request.getName());
        role.append("create_at", System.currentTimeMillis());
        role.append("update_at", System.currentTimeMillis());
        role.append("create_by", request.getInfo().getUsername());
        role.append("update_by", request.getInfo().getUsername());
        db.insertOne(CollectionNameDefs.COLL_API_ROLE, role);
        return new BaseResponse(0, "OK");

    }

    @Override
    public BaseResponse updateApiRole(UpdateApiRoleRequest request) {

        String id = request.getId();
        Bson cond = Filters.eq("id", id);
        Document role = db.findOne(CollectionNameDefs.COLL_API_ROLE, cond);
        if (role == null) {
            return new BaseResponse(-1, "Role này không tồn tại");
        }

        // update roles
        Bson updates = Updates.combine(
                Updates.set("name", request.getName()),
                Updates.set("update_at", System.currentTimeMillis()),
                Updates.set("update_by", request.getInfo().getUsername())
        );
        db.update(CollectionNameDefs.COLL_API_ROLE, cond, updates, true);

        // update refs
        Bson fApiGroup = Filters.eq("api_id", id);
        Bson uApiGroup = Updates.combine(
                Updates.set("name", request.getName()),
                Updates.set("update_by", request.getInfo().getUsername()),
                Updates.set("update_at", System.currentTimeMillis())
        );
        db.update(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, fApiGroup, uApiGroup, false);

        return new BaseResponse(0, "OK");

    }

    @Override
    public BaseResponse deleteApiRole(DeleteApiRoleRequest request) {

        String id = request.getId();

        Bson fApiGroup = Filters.eq("api_id", id);

        long count = db.countAll(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, fApiGroup);

        if (count > 0) {
            return new BaseResponse(-1, "Api này tồn tại trên một group api, không thể xóa");
        }

        Bson cond = Filters.eq("id", id);

        Document role = db.findOne(CollectionNameDefs.COLL_API_ROLE, cond);
        if (role == null) {
            return new BaseResponse(-1, "Role này không tồn tại");
        }

        db.delete(CollectionNameDefs.COLL_API_ROLE, cond);
        return new BaseResponse(0, "OK");

    }

}
