package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.*;
import com.edso.resume.account.entities.ApiRoleEntity;
import com.edso.resume.account.entities.ApiRoleGroupEntity;
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

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ApiRoleGroupServiceImpl extends BaseService implements ApiRoleGroupService {

    private final MongoDbOnlineSyncActions db;

    public ApiRoleGroupServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public GetArrayResponse<ApiRoleGroupEntity> list(HeaderInfo headerInfo, String name, String id, Integer page, Integer size) {
        GetArrayResponse<ApiRoleGroupEntity> response = new GetArrayResponse<>();
        List<ApiRoleGroupEntity> rows = new ArrayList<>();
        List<Bson> c = new ArrayList<>();
        if (!Strings.isNullOrEmpty(id)) {
            c.add(Filters.eq("id", id));
        }
        if (!Strings.isNullOrEmpty(name)) {
            c.add(Filters.regex("name", Pattern.compile(name)));
        }
        Bson cond = buildCondition(c);
        long total = db.countAll(CollectionNameDefs.COLL_API_ROLE_GROUP, cond);
        PagingInfo pagingInfo = PagingInfo.parse(page, size);
        FindIterable<Document> lst = db.findAll2(CollectionNameDefs.COLL_API_ROLE_GROUP, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());
        for (Document doc : lst) {
            String groupId = AppUtils.parseString(doc.get("id"));
            Bson cc = Filters.eq("group_id", groupId);
            // temp, need join
            FindIterable<Document> dds = db.findAll2(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, cc, null, 0, 10000);
            List<ApiRoleEntity> roles = new ArrayList<>();
            if (dds != null) {
                for (Document r : dds) {
                    ApiRoleEntity e = ApiRoleEntity.builder()
                            .id(AppUtils.parseString(r.get("id")))
                            .name(AppUtils.parseString(r.get("name")))
                            .build();
                    roles.add(e);
                }
            }
            ApiRoleGroupEntity entity = ApiRoleGroupEntity.builder()
                    .id(id)
                    .name(AppUtils.parseString(doc.get("name")))
                    .roles(roles)
                    .createAt(AppUtils.parseLong(doc.get("create_at")))
                    .updateAt(AppUtils.parseLong(doc.get("update_at")))
                    .build();
            rows.add(entity);
        }
        response.setTotal(total);
        response.setRows(rows);
        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse createRoleApiGroup(CreateRoleGroupRequest request) {
        String id = UUID.randomUUID().toString();
        String name = request.getName();
        Document doc = new Document();
        doc.append("id", id);
        doc.append("name", name);
        doc.append("create_at", System.currentTimeMillis());
        doc.append("update_at", System.currentTimeMillis());
        doc.append("create_by", request.getInfo().getUsername());
        doc.append("update_by", request.getInfo().getUsername());
        db.insertOne(CollectionNameDefs.COLL_API_ROLE_GROUP, doc);
        List<ApiRoleEntity> roles = request.getRoles();
        insertRoleItems(id, roles, request.getInfo().getUsername());
        return new BaseResponse(0, "OK");
    }

    private void insertRoleItems(String id, List<ApiRoleEntity> roles, String author) {
        if (roles != null && !roles.isEmpty()) {
            List<Document> rs = new ArrayList<>();
            for (ApiRoleEntity role : roles) {
                Document r = new Document();
                r.append("group_id", id);
                r.append("api_id", role.getId());
                r.append("api_name", role.getName());
                r.append("create_at", System.currentTimeMillis());
                r.append("update_at", System.currentTimeMillis());
                r.append("create_by", author);
                r.append("update_by", author);
                rs.add(r);
            }
            db.insertMany(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, rs);
        }
    }

    @Override
    public BaseResponse updateRoleApiGroup(UpdateRoleGroupRequest request) {

        String id = request.getId();
        Bson cond = Filters.eq("id", id);
        long count = db.countAll(CollectionNameDefs.COLL_API_ROLE_GROUP, cond);
        if (count == 0) {
            return new BaseResponse(-1, "Không tồn tại group này !");
        }

        Bson updates = Updates.combine(
                Updates.set("name", request.getName()),
                Updates.set("update_at", System.currentTimeMillis()),
                Updates.set("update_by", request.getInfo().getUsername())
        );

        db.update(CollectionNameDefs.COLL_API_ROLE_GROUP, cond, updates, true);

        // clear and re-insert (temp)
        cond = Filters.eq("group_id", id);
        db.delete(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, cond);
        List<ApiRoleEntity> roles = request.getRoles();
        insertRoleItems(id, roles, request.getInfo().getUsername());

        return new BaseResponse(0, "OK");

    }

    @Override
    public BaseResponse deleteRoleApiGroup(DeleteRoleGroupRequest request) {
        String id = request.getId();
        Bson cond = Filters.eq("id", id);
        db.delete(CollectionNameDefs.COLL_API_ROLE_GROUP, cond);
        cond = Filters.eq("group_id", id);
        db.delete(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, cond);
        return new BaseResponse(0, "OK");
    }

    @Override
    public BaseResponse assign(AssignApiRoleRequest request) {

        String groupId = request.getGroupApiId();
        String username = request.getUsername();
        Bson fGroup = Filters.eq("id", groupId);

        Document apiRoleGroup = db.findOne(CollectionNameDefs.COLL_API_ROLE_GROUP, fGroup);
        if (apiRoleGroup == null) {
            return new BaseResponse(-1, "Không tồn tại group api này");
        }

        Bson fUser = Filters.eq("username", username);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, fUser);
        if (user == null) {
            return new BaseResponse(-1, "Không tồn tại tài khoản này");
        }

        Bson cond = Filters.and(
                Filters.eq("username", username),
                Filters.eq("group_id", groupId)
        );

        Document doc = db.findOne(CollectionNameDefs.COLL_API_ROLE_GROUP_USER, cond);
        if (doc != null) {
            return new BaseResponse(-1, "Tài khoản đã được gán group api");
        }

        doc = new Document();
        doc.append("username", username);
        doc.append("group_id", groupId);
        doc.append("create_at", System.currentTimeMillis());
        doc.append("update_at", System.currentTimeMillis());
        doc.append("create_by", request.getInfo().getUsername());
        doc.append("update_by", request.getInfo().getUsername());
        db.insertOne(CollectionNameDefs.COLL_API_ROLE_GROUP_USER, doc);
        return new BaseResponse(0, "OK");

    }

    @Override
    public BaseResponse revoke(RevokeApiRoleRequest request) {
        String groupId = request.getGroupApiId();
        String username = request.getUsername();
        Bson cond = Filters.and(
                Filters.eq("username", username),
                Filters.eq("group_id", groupId)
        );
        db.delete(CollectionNameDefs.COLL_API_ROLE_GROUP_USER, cond);
        return new BaseResponse(0, "OK");
    }

    @Override
    public GetArrayResponse<String> getApiRoleOfUser(String username) {
        Bson cond = Filters.eq("username", username);
        GetArrayResponse<String> response = new GetArrayResponse<>();
        FindIterable<Document> list = db.findAll2(CollectionNameDefs.COLL_API_ROLE_GROUP_USER, cond, null, 0, 10000);
        if (list == null) {
            response.setResult(0, "OK");
            response.setTotal(0);
            response.setRows(new ArrayList<>());
            return response;
        }
        Set<String> groups = new HashSet<>();
        for (Document doc : list) {
            String groupId = AppUtils.parseString(doc.get("group_id"));
            groups.add(groupId);
        }
        Bson cond2 = Filters.in("group_id", new ArrayList<>(groups));
        FindIterable<Document> roles = db.findAll2(CollectionNameDefs.COLL_API_ROLE_GROUP_ITEMS, cond2, null, 0, 100000);
        Set<String> apis = new HashSet<>();
        if (roles != null) {
            for (Document role : roles) {
                String api = AppUtils.parseString(role.get("api_id"));
                apis.add(api);
            }
        }
        response.setRows(new ArrayList<>(apis));
        response.setTotal(apis.size());
        response.setSuccess();
        return response;
    }

}
