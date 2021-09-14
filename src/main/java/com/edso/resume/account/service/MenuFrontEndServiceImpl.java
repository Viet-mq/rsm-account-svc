package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.AssignMenuToUserRequest;
import com.edso.resume.account.domain.request.CreateMenuFrontendRequest;
import com.edso.resume.account.domain.request.RevokeMenuFromUserRequest;
import com.edso.resume.account.domain.request.UpdateMenuFrontendRequest;
import com.edso.resume.account.entities.ActionMenuEntity;
import com.edso.resume.account.entities.MenuEntity;
import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.common.CollectionNameDefs;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.entities.PagingInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.response.GetSingleItemResponse;
import com.google.common.base.Strings;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class MenuFrontEndServiceImpl extends BaseService implements MenuFrontEndService {

    private final MongoDbOnlineSyncActions db;

    public MenuFrontEndServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public GetArrayResponse<Document> list(HeaderInfo info, String id, String name, Integer page, Integer size) {
        GetArrayResponse<Document> response = new GetArrayResponse<>();
        List<Bson> c = new ArrayList<>();
        if (!Strings.isNullOrEmpty(id)) {
            c.add(Filters.eq("id", id));
        }
        if (!Strings.isNullOrEmpty(name)) {
            c.add(Filters.regex("name", Pattern.compile(name)));
        }
        Bson cond = buildCondition(c);
        long total = db.countAll(CollectionNameDefs.COLL_FRONTEND_MENU, cond);
        PagingInfo pagingInfo = PagingInfo.parse(page, size);
        List<Document> rows = db.findAll(CollectionNameDefs.COLL_FRONTEND_MENU, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());
        response.setTotal(total);
        response.setRows(rows);
        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse createMenu(CreateMenuFrontendRequest request) {

        try {

            String id = UUID.randomUUID().toString();
            Document menu = new Document();
            menu.append("id", id);
            menu.append("name", request.getName());
            menu.append("create_at", System.currentTimeMillis());
            menu.append("update_at", System.currentTimeMillis());
            menu.append("create_by", request.getInfo().getUsername());
            menu.append("update_by", request.getInfo().getUsername());

            List<MenuEntity> menus = request.getMenu();
            List<Document> lst = new ArrayList<>();
            Set<String> sets = new HashSet<>();

            for (MenuEntity mn : menus) {
                sets.addAll(getIds(mn));
                lst.add(buildMenuItems(mn));
            }

            menu.append("children", lst);
            db.insertOne(CollectionNameDefs.COLL_FRONTEND_MENU, menu);

            if (!sets.isEmpty()) {
                List<Document> refs = new ArrayList<>();
                for (String mn : sets) {
                    Document doc = new Document();
                    doc.append("menu_id", id);
                    doc.append("view_id", mn);
                    refs.add(doc);
                }
                db.insertMany(CollectionNameDefs.COLL_FRONTEND_MENU_ITEMS, refs);
            }

            return new BaseResponse(0, "OK");

        } catch (Throwable ex) {
            logger.error("Ex: ", ex);
            return new BaseResponse(-1, "Hệ thống đang bận");
        }

    }

    private Document buildMenuItems(MenuEntity entity) {

        Document doc = new Document();
        doc.append("id", entity.getId());
        doc.append("name", entity.getName());
        doc.append("show", entity.getShow());
        doc.append("icon", entity.getIcon());

        List<Document> at = new ArrayList<>();
        List<ActionMenuEntity> actions = entity.getActions();

        if (actions != null && !actions.isEmpty()) {
            for (ActionMenuEntity e : actions) {
                Document d = new Document();
                d.append("action_id", e.getActionId());
                d.append("action_name", e.getActionName());
                d.append("show", e.getShow() != null ? e.getShow() : true);
                at.add(d);
            }
        }

        List<Document> children = new ArrayList<>();
        List<MenuEntity> cr = entity.getChildren();
        if (cr != null && !cr.isEmpty()) {
            for (MenuEntity e : cr) {
                children.add(buildMenuItems(e));
            }
        }

        doc.append("children", children);
        doc.append("action", at);

        return doc;
    }

    private Set<String> getIds(MenuEntity entity) {
        Set<String> rs = new HashSet<>();
        rs.add(entity.getId());
        List<MenuEntity> entities = entity.getChildren();
        if (entities != null && !entities.isEmpty()) {
            for (MenuEntity e : entities) {
                Set<String> s = getIds(e);
                if (!s.isEmpty()) {
                    rs.addAll(s);
                }
            }
        }
        return rs;
    }

    @Override
    public BaseResponse updateMenu(UpdateMenuFrontendRequest request) {

        try {

            String id = UUID.randomUUID().toString();

            Bson c = Filters.eq("id", id);
            long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_MENU, c);
            if (count == 0) {
                return new BaseResponse(-1, "Không tồn tại menu này");
            }

            List<MenuEntity> menus = request.getMenu();
            List<Document> lst = new ArrayList<>();
            Set<String> sets = new HashSet<>();

            for (MenuEntity mn : menus) {
                sets.addAll(getIds(mn));
                lst.add(buildMenuItems(mn));
            }

            Bson updates = Updates.combine(
                    Updates.set("name", request.getName()),
                    Updates.set("children", lst),
                    Updates.set("update_at", System.currentTimeMillis()),
                    Updates.set("update_by", request.getInfo().getUsername())
            );

            db.update(CollectionNameDefs.COLL_FRONTEND_MENU, c, updates, true);

            if (!sets.isEmpty()) {
                List<Document> refs = new ArrayList<>();
                for (String mn : sets) {
                    Document doc = new Document();
                    doc.append("menu_id", id);
                    doc.append("view_id", mn);
                    refs.add(doc);
                }
                db.delete(CollectionNameDefs.COLL_FRONTEND_MENU_ITEMS, Filters.eq("menu_id", id));
                db.insertMany(CollectionNameDefs.COLL_FRONTEND_MENU_ITEMS, refs);
            }

            return new BaseResponse(0, "OK");

        } catch (Throwable ex) {
            logger.error("Ex: ", ex);
            return new BaseResponse(-1, "Hệ thống đang bận");
        }
    }

    @Override
    public BaseResponse deleteMenu(UpdateMenuFrontendRequest request) {
        try {
            String id = request.getId();
            Bson cond = Filters.eq("id", id);
            db.delete(CollectionNameDefs.COLL_FRONTEND_MENU, cond);
            Bson cCond = Filters.eq("menu_id", id);
            db.delete(CollectionNameDefs.COLL_FRONTEND_MENU_ITEMS, cCond);
            return new BaseResponse(0, "OK");
        } catch (Throwable ex) {
            logger.error("Ex: ", ex);
            return new BaseResponse(-1, "Hệ thống đang bận");
        }
    }

    @Override
    public BaseResponse assignToUser(AssignMenuToUserRequest request) {
        try {

            String menuId = request.getMenuId();
            String username = request.getUsername();
            Bson fMenu = Filters.eq("id", menuId);

            Document menu = db.findOne(CollectionNameDefs.COLL_FRONTEND_MENU, fMenu);
            if (menu == null) {
                return new BaseResponse(-1, "Không tồn tại menu này");
            }

            Bson fUser = Filters.eq("username", username);
            Document user = db.findOne(CollectionNameDefs.COLL_USER, fUser);

            if (user == null) {
                return new BaseResponse(-1, "Không tồn tại tài khoản này");
            }

            Bson cond = Filters.and(
                    Filters.eq("username", username),
                    Filters.eq("menu_id", menuId)
            );

            long count = db.countAll(CollectionNameDefs.COLL_FRONTEND_MENU_USER, cond);
            if (count >= 0) {
                return new BaseResponse(-1, "Tài khoản đã được gán menu này");
            }

            // 1 user chỉ có 1 menu, xóa hết các menu cũ
            Bson cDel = Filters.eq("username", username);
            db.delete(CollectionNameDefs.COLL_FRONTEND_MENU_USER, cDel);

            Document doc = new Document();
            doc.append("username", username);
            doc.append("menu_id", menuId);
            doc.append("create_at", System.currentTimeMillis());
            doc.append("update_at", System.currentTimeMillis());
            doc.append("create_by", request.getInfo().getUsername());
            doc.append("update_by", request.getInfo().getUsername());
            db.insertOne(CollectionNameDefs.COLL_FRONTEND_MENU_USER, doc);
            return new BaseResponse(0, "OK");

        } catch (Throwable ex) {
            logger.error("Ex: ", ex);
            return new BaseResponse(-1, "Hệ thống đang bận");
        }

    }

    @Override
    public BaseResponse revokeToUser(RevokeMenuFromUserRequest request) {

        try {

            String menuId = request.getMenuId();
            String username = request.getUsername();

            Bson cond = Filters.and(
                    Filters.eq("username", username),
                    Filters.eq("menu_id", menuId)
            );

            db.delete(CollectionNameDefs.COLL_FRONTEND_MENU_USER, cond);

            return new BaseResponse(0, "OK");

        } catch (Throwable ex) {
            logger.error("Ex: ", ex);
            return new BaseResponse(-1, "Hệ thống đang bận");
        }

    }

    @Override
    public GetSingleItemResponse<Document> getMenuOfUser(String username) {
        GetSingleItemResponse<Document> response = new GetSingleItemResponse<>();
        Bson cond = Filters.eq("username", username);
        Document doc = db.findOne(CollectionNameDefs.COLL_FRONTEND_MENU_USER, cond);
        if (doc == null) {
            response.setResult(-1, "Item not found");
            return response;
        }

        String menuId = AppUtils.parseString(doc.get("menu_id"));
        cond = Filters.eq("id", menuId);
        Document menu = db.findOne(CollectionNameDefs.COLL_FRONTEND_MENU, cond);
        if (menu == null) {
            response.setResult(-1, "Item not found");
            return response;
        }

        response.setItem(menu);
        response.setSuccess();
        return response;
    }

}
