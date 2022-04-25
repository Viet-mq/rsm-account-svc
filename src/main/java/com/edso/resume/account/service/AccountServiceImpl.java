package com.edso.resume.account.service;

import com.edso.resume.account.common.Common;
import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.*;
import com.edso.resume.account.entities.UserEntity;
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
import java.util.regex.Pattern;

import static com.mongodb.client.model.Updates.set;

@Service
public class AccountServiceImpl extends BaseService implements AccountService {

    private final MongoDbOnlineSyncActions db;

    public AccountServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public GetArrayResponse<UserEntity> getAccounts(HeaderInfo headerInfo, String username, String name, Integer role, Integer status, String dateOfBirth, Integer page, Integer size) {
        GetArrayResponse<UserEntity> response = new GetArrayResponse<>();
        List<UserEntity> rows = new ArrayList<>();
        List<Bson> c = new ArrayList<>();
        if (!Strings.isNullOrEmpty(name)) {
            c.add(Filters.or(Filters.regex("username", Pattern.compile(parseVietnameseToEnglish(name))),
                    Filters.regex("name_search", Pattern.compile(parseVietnameseToEnglish(name))))
            );
        }
        if (role != null) {
            c.add(Filters.eq("role", role));
        }
        if (Strings.isNullOrEmpty(username)) {
            c.add(Filters.eq("username", username));
        }
        if (status != null) {
            c.add(Filters.eq("status", status));
        }
        if (dateOfBirth != null) {
            c.add(Filters.eq("dateOfBirth", dateOfBirth));
        }
        c.add(Filters.in(DbKeyConfig.ORGANIZATIONS, headerInfo.getOrganizations()));
        Bson cond = buildCondition(c);
        long total = db.countAll(CollectionNameDefs.COLL_USER, cond);
        PagingInfo pagingInfo = PagingInfo.parse(page, size);
        FindIterable<Document> lst = db.findAll2(CollectionNameDefs.COLL_USER, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());
        for (Document doc : lst) {
            UserEntity entity = new UserEntity();
            entity.setUsername(AppUtils.parseString(doc.get("username")));
            entity.setFullName(AppUtils.parseString(doc.get("full_name")));
            entity.setEmail(AppUtils.parseString(doc.get("email")));
            entity.setDateOfBirth(AppUtils.parseString(doc.get("dateOfBirth")));
            entity.setRoles((List<Document>) doc.get("roles"));
            entity.setOrganizations((List<String>) doc.get("organizations"));
            entity.setStatus(AppUtils.parseInt(doc.get("status")));
            entity.setCreateAt(AppUtils.parseLong(doc.get("create_at")));
            entity.setUpdateAt(AppUtils.parseLong(doc.get("update_at")));
            entity.setLastChangePasswordAt(AppUtils.parseLong(doc.get("last_change_password_at")));
            entity.setCreateBy(AppUtils.parseString(doc.get("create_by")));
            entity.setUpdateBy(AppUtils.parseString(doc.get("update_by")));
            // fill thêm các trường khác vào đây
            rows.add(entity);
        }
        response.setTotal(total);
        response.setRows(rows);
        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse createAccount(CreateAccountRequest request) {

        BaseResponse response = new BaseResponse();
        String username = request.getUsername();
        Bson cond = Filters.eq("username", username);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);

        if (user != null) {
            response.setResult(-1, "Tài khoản đã tồn tại");
            return response;
        }

        Document email = db.findOne(CollectionNameDefs.COLL_USER, Filters.eq("email", request.getEmail().replaceAll(" ", "")));
        if (email != null) {
            response.setResult(-1, "Email này đã tồn tại");
            return response;
        }

        List<Document> orgs = db.findAll(CollectionNameDefs.COLL_ORGANIZATION, Filters.in("id", request.getOrganizations()), null, 0, 0);
        if (orgs.size() != request.getOrganizations().size()) {
            response.setResult(-1, "Không tồn tại tổ chức này");
            return response;
        }

        FindIterable<Document> roles = db.findAll2(CollectionNameDefs.COLL_ROLE, Filters.in(DbKeyConfig.ID, request.getRoles()), null, 0, 0);
        List<Document> roleResult = new ArrayList<>();
        if (roles == null) {
            response.setFailed("Không tồn tại role này");
            return response;
        }
        for (Document document : roles) {
            document.remove("_id");
            document.remove(DbKeyConfig.NAME_SEARCH);
            document.remove(DbKeyConfig.NAME_EQUAL);
            document.remove(DbKeyConfig.CREATE_AT);
            document.remove(DbKeyConfig.CREATE_BY);
            document.remove(DbKeyConfig.UPDATE_AT);
            document.remove(DbKeyConfig.UPDATE_BY);
            document.remove(DbKeyConfig.API_ROLES);
            document.remove(DbKeyConfig.VIEW_ROLES);
            roleResult.add(document);
        }
        if (roleResult.size() != request.getRoles().size()) {
            response.setFailed("Không tồn tại role này");
            return response;
        }

        String password = request.getPassword();
        String dateofBirth = request.getDateOfBirth();
        password = AppUtils.MD5(password);
        password = AppUtils.MD5(password);

        user = new Document();

        user.append("username", username);
        user.append("email", request.getEmail().replaceAll(" ", ""));
        user.append("full_name", request.getFullName());
        user.append("name_search", parseVietnameseToEnglish(request.getFullName()));
        user.append("password", password);
        user.append("status", Common.ACC_STATUS_ACTIVE);
        user.append("dateOfBirth", dateofBirth);

        user.append("roles", roleResult);
        user.append(DbKeyConfig.ORGANIZATIONS, request.getOrganizations());

        user.append("create_at", System.currentTimeMillis());
        user.append("update_at", System.currentTimeMillis());
//        user.append("count_login", 0);
        user.append("create_by", request.getInfo().getUsername());
        user.append("update_by", request.getInfo().getUsername());

        // insert to database
        db.insertOne(CollectionNameDefs.COLL_USER, user);

        response.setSuccess();
        return response;

    }

    @Override
    public BaseResponse updateAccount(UpdateAccountRequest request) {

        BaseResponse response = new BaseResponse();

        String username = request.getUsername();
        String userUpdate = request.getInfo().getUsername();
        List<Bson> c = new ArrayList<>();
        c.add(Filters.eq("username", username));
        if (request.getInfo().getRole() != 1) {
            c.add(Filters.in(DbKeyConfig.ORGANIZATIONS, request.getInfo().getOrganizations()));
        }
        Bson cond = buildCondition(c);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);
        if (user == null) {
            response.setResult(-1, "Không tồn tại người dùng này");
            return response;
        }

        Document obj = db.findOne(CollectionNameDefs.COLL_USER, Filters.eq("email", request.getEmail().replaceAll(" ", "")));
        if (obj != null) {
            String objId = AppUtils.parseString(obj.get(DbKeyConfig.USERNAME));
            if (!objId.equals(request.getUsername())) {
                response.setFailed("Email này đã tồn tại");
                return response;
            }
        }

        List<Document> orgs = db.findAll(CollectionNameDefs.COLL_ORGANIZATION, Filters.in("id", request.getOrganizations()), null, 0, 0);
        if (orgs.size() != request.getOrganizations().size()) {
            response.setResult(-1, "Không tồn tại tổ chức này");
            return response;
        }

        FindIterable<Document> roles = db.findAll2(CollectionNameDefs.COLL_ROLE, Filters.in(DbKeyConfig.ID, request.getRoles()), null, 0, 0);
        List<Document> roleResult = new ArrayList<>();
        if (roles == null) {
            response.setFailed("Không tồn tại role này");
            return response;
        }
        for (Document document : roles) {
            document.remove("_id");
            document.remove(DbKeyConfig.NAME_SEARCH);
            document.remove(DbKeyConfig.NAME_EQUAL);
            document.remove(DbKeyConfig.CREATE_AT);
            document.remove(DbKeyConfig.CREATE_BY);
            document.remove(DbKeyConfig.UPDATE_AT);
            document.remove(DbKeyConfig.UPDATE_BY);
            document.remove(DbKeyConfig.API_ROLES);
            document.remove(DbKeyConfig.VIEW_ROLES);
            roleResult.add(document);
        }
        if (roleResult.size() != request.getRoles().size()) {
            response.setFailed("Không tồn tại role này");
            return response;
        }

        String fullName = request.getFullName();
        String dateOfBirth = request.getDateOfBirth();
        Long update_at = System.currentTimeMillis();


        Bson valueRole = set("roles", roleResult);
        Bson valueOrg = set(DbKeyConfig.ORGANIZATIONS, request.getOrganizations());
        Bson valueFullname = set("full_name", fullName);
        Bson valueNameSearch = set("name_search", parseVietnameseToEnglish(fullName));
        Bson valueDateOfBirth = set("dateOfBirth", dateOfBirth);
        Bson valueUpdate_at = set("update_at", update_at);
        Bson valueUserUpdate = set("update_by", userUpdate);
        Bson valueEmailUpdate = set("email", request.getEmail().replaceAll(" ", ""));

        Bson value = Updates.combine(valueFullname, valueDateOfBirth, valueUpdate_at, valueUserUpdate, valueEmailUpdate, valueNameSearch, valueRole, valueOrg);

        db.update(CollectionNameDefs.COLL_USER, cond, value);

        Bson updateProfile = Updates.combine(
                set(DbKeyConfig.FULL_NAME_CREATOR, request.getFullName())
        );
        db.update(CollectionNameDefs.COLL_PROFILE, Filters.eq(DbKeyConfig.CREATE_RECRUITMENT_BY, request.getUsername()), updateProfile);

        Bson updateCalendar = Updates.combine(
                set(DbKeyConfig.INTERVIEWERS_FULL_NAME, request.getFullName()),
                set(DbKeyConfig.INTERVIEWERS_EMAIL, request.getEmail().replaceAll(" ", ""))
        );
        db.update(CollectionNameDefs.COLL_CALENDAR_PROFILE, Filters.eq(DbKeyConfig.JOIN_USERNAME, request.getUsername()), updateCalendar);

        Bson updateRecruitment = Updates.combine(
                set(DbKeyConfig.INTERVIEWERS_FULL_NAME, request.getFullName()),
                set(DbKeyConfig.INTERVIEWERS_EMAIL, request.getEmail().replaceAll(" ", ""))
        );
        db.update(CollectionNameDefs.COLL_RECRUITMENT, Filters.eq(DbKeyConfig.JOIN_USERNAME, request.getUsername()), updateRecruitment);

        Bson updateRecruitment2 = Updates.combine(
                set(DbKeyConfig.FULL_NAME, request.getFullName())
        );
        db.update(CollectionNameDefs.COLL_RECRUITMENT, Filters.eq(DbKeyConfig.CREATE_BY, request.getUsername()), updateRecruitment2);

        Bson updateComment = Updates.combine(
                set(DbKeyConfig.FULL_NAME, request.getFullName())
        );
        db.update(CollectionNameDefs.COLL_COMMENT, Filters.eq(DbKeyConfig.CREATE_AT, request.getUsername()), updateComment);

        Bson con = Filters.eq(DbKeyConfig.USERNAME, request.getUsername());
        Bson updateHistory = Updates.combine(
                set(DbKeyConfig.FULL_NAME, request.getFullName())
        );
        db.update(CollectionNameDefs.COLL_HISTORY_PROFILE, con, updateHistory);

        Bson updateHistoryEmail = Updates.combine(
                set(DbKeyConfig.FULL_NAME, request.getFullName())
        );
        db.update(CollectionNameDefs.COLL_HISTORY_EMAIL, con, updateHistoryEmail);

        Bson updateNote = Updates.combine(
                set(DbKeyConfig.FULL_NAME, request.getFullName())
        );
        db.update(CollectionNameDefs.COLL_NOTE_PROFILE, con, updateNote);

        Bson updateProfile1 = Updates.combine(
                set(DbKeyConfig.HR_REF, request.getFullName()),
                set(DbKeyConfig.MAIL_REF, request.getEmail().replaceAll(" ", ""))
        );
        db.update(CollectionNameDefs.COLL_PROFILE, con, updateProfile1);

        Bson updateProfile2 = Updates.combine(
                set(DbKeyConfig.PIC_NAME, request.getFullName()),
                set(DbKeyConfig.PIC_MAIL, request.getEmail().replaceAll(" ", ""))
        );
        db.update(CollectionNameDefs.COLL_PROFILE, Filters.eq(DbKeyConfig.PIC_ID, request.getUsername()), updateProfile2);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse deleteAccount(DeleteAccountRequest request) {
        BaseResponse response = new BaseResponse();
        long count = db.countAll(CollectionNameDefs.COLL_PROFILE, Filters.eq(DbKeyConfig.CREATE_RECRUITMENT_BY, request.getUsername()));
        if (count > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count1 = db.countAll(CollectionNameDefs.COLL_CALENDAR_PROFILE, Filters.eq(DbKeyConfig.JOIN_USERNAME, request.getUsername()));
        if (count1 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count2 = db.countAll(CollectionNameDefs.COLL_RECRUITMENT, Filters.eq(DbKeyConfig.JOIN_USERNAME, request.getUsername()));
        if (count2 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count3 = db.countAll(CollectionNameDefs.COLL_COMMENT, Filters.eq(DbKeyConfig.CREATE_AT, request.getUsername()));
        if (count3 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        Bson con = Filters.eq(DbKeyConfig.USERNAME, request.getUsername());
        long count4 = db.countAll(CollectionNameDefs.COLL_HISTORY_EMAIL, con);
        if (count4 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count5 = db.countAll(CollectionNameDefs.COLL_HISTORY_PROFILE, con);
        if (count5 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count6 = db.countAll(CollectionNameDefs.COLL_NOTE_PROFILE, con);
        if (count6 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count7 = db.countAll(CollectionNameDefs.COLL_PROFILE, con);
        if (count7 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        long count8 = db.countAll(CollectionNameDefs.COLL_PROFILE, Filters.eq(DbKeyConfig.PIC_ID, request.getUsername()));
        if (count8 > 0) {
            response.setFailed("Không thể xóa user này!");
            return response;
        }
        String username = request.getUsername();
        List<Bson> c = new ArrayList<>();
        c.add(Filters.eq("username", username));
        if (request.getInfo().getRole() != 1) {
            c.add(Filters.in(DbKeyConfig.ORGANIZATIONS, request.getInfo().getOrganizations()));
        }
        Bson cond = buildCondition(c);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);
        if (user == null) {
            response.setResult(-1, "Không tồn tại người dùng này");
            return response;
        }

        db.delete(CollectionNameDefs.COLL_USER, user);
        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse changePasswordAcc(ChangePasswordAccRequest request) {

        BaseResponse response = new BaseResponse();

        String username = request.getUsername();

        List<Bson> c = new ArrayList<>();
        c.add(Filters.eq("username", username));
        if (request.getInfo().getRole() != 1) {
            c.add(Filters.in(DbKeyConfig.ORGANIZATIONS, request.getInfo().getOrganizations()));
        }
        Bson cond = buildCondition(c);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);
        if (user == null) {
            response.setResult(-1, "Không tồn tại người dùng này");
            return response;
        }

        String newPassword = AppUtils.MD5(request.getNewPassword());
        newPassword = AppUtils.MD5(newPassword);

        Long last_change_password_at = System.currentTimeMillis();

        Bson valueNewPassword = set("password", newPassword);
        Bson valueLast_change_password_at = set("last_change_password_at", last_change_password_at);

        Bson value = Updates.combine(valueNewPassword, valueLast_change_password_at);

        db.update(CollectionNameDefs.COLL_USER, cond, value);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse changePasswordSelf(ChangePasswordSelfRequest request) {

        BaseResponse response = new BaseResponse();

        String username = request.getInfo().getUsername();

        Bson cond = Filters.eq("username", username);

        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);

        String userPassword = AppUtils.parseString(user.get("password"));

        String oldPassword = AppUtils.MD5(request.getOldPassword());
        oldPassword = AppUtils.MD5(oldPassword);

        if (!userPassword.equals(oldPassword)) {
            response.setResult(-1, "Mật khẩu cũ không chính xác");
            return response;
        }


        String newPassword = AppUtils.MD5(request.getNewPassword());
        newPassword = AppUtils.MD5(newPassword);

        Long last_change_password_at = System.currentTimeMillis();

        Bson valueNewPassword = set("password", newPassword);
        Bson valueLast_change_password_at = set("last_change_password_at", last_change_password_at);

        Bson value = Updates.combine(valueNewPassword, valueLast_change_password_at);

        db.update(CollectionNameDefs.COLL_USER, cond, value);

        response.setSuccess();
        return response;

    }

}
