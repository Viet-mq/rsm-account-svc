package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.AddActionToViewRequest;
import com.edso.resume.account.domain.request.CreateFrontendViewRequest;
import com.edso.resume.account.entities.FrontendViewEntity;
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
import java.util.regex.Pattern;

@Service
public class FrontendViewServiceImpl extends BaseService implements FrontendViewService {

    private final MongoDbOnlineSyncActions db;

    public FrontendViewServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public GetArrayResponse<FrontendViewEntity> list(HeaderInfo info, String id, String name, Integer page, Integer size) {
        GetArrayResponse<FrontendViewEntity> response = new GetArrayResponse<>();
        List<FrontendViewEntity> rows = new ArrayList<>();
        List<Bson> c = new ArrayList<>();
        if (!Strings.isNullOrEmpty(id)) {
            c.add(Filters.eq("id", id));
        }
        if (!Strings.isNullOrEmpty(name)) {
            c.add(Filters.regex("name", Pattern.compile(name)));
        }
        Bson cond = buildCondition(c);
        long total = db.countAll(CollectionNameDefs.COLL_FRONTEND_VIEW, cond);
        PagingInfo pagingInfo = PagingInfo.parse(page, size);
        FindIterable<Document> lst = db.findAll2(CollectionNameDefs.COLL_FRONTEND_VIEW, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());
        for (Document doc : lst) {
            String viewId = AppUtils.parseString(doc.get("id"));
            Bson cCond = Filters.eq("viewId", viewId);
            FindIterable<Document> l2 = db.findAll2(CollectionNameDefs.COLL_FRONTEND_VIEW_ACTION, cCond, null, 0, 100);
            List<AddActionToViewRequest> actions = new ArrayList<>();
            if (l2 != null) {
                for (Document d : l2) {
                    AddActionToViewRequest req = new AddActionToViewRequest();
                    req.setViewId(viewId);
                    req.setActionId(AppUtils.parseString(d.get("actionId")));
                    req.setActionName(AppUtils.parseString(d.get("actionName")));
                    req.setDesc(AppUtils.parseString(d.get("desc")));
                    actions.add(req);
                }
            }
            FrontendViewEntity entity = FrontendViewEntity.builder()
                    .id(viewId)
                    .name(AppUtils.parseString(doc.get("name")))
                    .icon(AppUtils.parseString(doc.get("icon")))
                    .show(doc.getBoolean("show", false))
                    .actions(actions)
                    .build();
            rows.add(entity);
        }
        response.setTotal(total);
        response.setRows(rows);
        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse createView(CreateFrontendViewRequest request) {
        String id = request.getId();
        Bson cond = Filters.eq("id", id);
        long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_VIEW, cond);
        if (count > 0) {
            return new BaseResponse(-1, "Đã tồn tại view này");
        }
        Document view = new Document();
        view.append("id", id);
        view.append("name", request.getName());
        view.append("icon", request.getIcon());
        view.append("show", request.getShow());
        view.append("create_at", System.currentTimeMillis());
        view.append("update_at", System.currentTimeMillis());
        view.append("create_by", request.getInfo().getUsername());
        view.append("update_by", request.getInfo().getUsername());
        view.append("actions", new ArrayList<>());
        db.insertOne(CollectionNameDefs.COLL_FRONTEND_VIEW, view);
        return new BaseResponse(0, "OK");
    }

    @Override
    public BaseResponse updateView(CreateFrontendViewRequest request) {
        String id = request.getId();
        Bson cond = Filters.eq("id", id);
        long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_VIEW, cond);
        if (count == 0) {
            return new BaseResponse(-1, "Không tồn tại view này");
        }
        Bson updates = Updates.combine(
                Updates.set("name", request.getName()),
                Updates.set("icon", request.getIcon()),
                Updates.set("show", request.getShow()),
                Updates.set("update_at", System.currentTimeMillis()),
                Updates.set("update_by", request.getInfo().getUsername())
        );
        db.update(CollectionNameDefs.COLL_FRONTEND_VIEW, cond, updates, true);
        return new BaseResponse(0, "OK");
    }

    @Override
    public BaseResponse deleteView(CreateFrontendViewRequest request) {
        String id = request.getId();
        Bson cond = Filters.eq("id", id);
        long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_VIEW, cond);
        if (count == 0) {
            return new BaseResponse(-1, "Không tồn tại view này");
        }

        // COLL_FRONTEND_MENU

        db.delete(CollectionNameDefs.COLL_FRONTEND_VIEW, cond);
        cond = Filters.eq("viewId", id);
        db.delete(CollectionNameDefs.COLL_FRONTEND_VIEW_ACTION, cond);
        return new BaseResponse(0, "OK");

    }

    @Override
    public BaseResponse addActionOnView(AddActionToViewRequest request) {
        String actionId = request.getActionId();
        String viewId = request.getViewId();
        Bson cond = Filters.and(
                Filters.eq("viewId", viewId),
                Filters.eq("actionId", actionId)
        );
        long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_VIEW_ACTION, cond);
        if (count > 0) {
            return new BaseResponse(-1, "View đã tồn tại action này");
        }
        Document action = new Document();
        action.append("viewId", viewId);
        action.append("actionId", actionId);
        action.append("actionName", request.getActionName());
        action.append("create_at", System.currentTimeMillis());
        action.append("update_at", System.currentTimeMillis());
        action.append("create_by", request.getInfo().getUsername());
        action.append("update_by", request.getInfo().getUsername());
        db.insertOne(CollectionNameDefs.COLL_FRONTEND_VIEW_ACTION, action);
        return new BaseResponse(0, "OK");
    }

    @Override
    public BaseResponse removeActionOnView(AddActionToViewRequest request) {
        String actionId = request.getActionId();
        String viewId = request.getViewId();
        Bson cond = Filters.and(
                Filters.eq("viewId", viewId),
                Filters.eq("actionId", actionId)
        );
        long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_VIEW_ACTION, cond);
        if (count == 0) {
            return new BaseResponse(-1, "View không tồn tại action này");
        }
        db.delete(CollectionNameDefs.COLL_FRONTEND_VIEW_ACTION, cond);
        return new BaseResponse(0, "OK");
    }

}
