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
    public GetArrayResponse<UserEntity> getAccounts(HeaderInfo headerInfo, String name, Integer role, Integer status, String dateOfBirth, Integer page, Integer size) {
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
        if (status != null) {
            c.add(Filters.eq("status", status));
        }
        if (dateOfBirth != null) {
            c.add(Filters.eq("dateOfBirth", dateOfBirth));
        }
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
            entity.setRole(AppUtils.parseInt(doc.get("role")));
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

        Document email = db.findOne(CollectionNameDefs.COLL_USER, Filters.eq("email", request.getEmail()));

        if (email != null) {
            response.setResult(-1, "Email này đã tồn tại");
            return response;
        }

        String password = request.getPassword();
        String dateofBirth = request.getDateOfBirth();
        password = AppUtils.MD5(password);
        password = AppUtils.MD5(password);

        user = new Document();

        user.append("username", username);
        user.append("email", request.getEmail());
        user.append("full_name", request.getFullName());
        user.append("name_search", parseVietnameseToEnglish(request.getFullName()));
        user.append("password", password);
        user.append("status", Common.ACC_STATUS_ACTIVE);
        user.append("dateOfBirth", dateofBirth);

        // check role
        Integer role = request.getRole();

        user.append("role", role);
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

        Bson cond = Filters.eq("username", username);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);
        if (user == null) {
            response.setResult(-1, "Không tồn tại người dùng này");
            return response;
        }

        Document obj = db.findOne(CollectionNameDefs.COLL_USER, Filters.eq("email", request.getEmail()));
        if (obj != null) {
            String objId = AppUtils.parseString(obj.get(DbKeyConfig.USERNAME));
            if (!objId.equals(request.getUsername())) {
                response.setFailed("Email này đã tồn tại");
                return response;
            }
        }

        String fullName = request.getFullName();
        String dateOfBirth = request.getDateOfBirth();
        Long update_at = System.currentTimeMillis();


        Bson valueFullname = set("full_name", fullName);
        Bson valueNameSearch = set("name_search", parseVietnameseToEnglish(fullName));
        Bson valueDateOfBirth = set("dateOfBirth", dateOfBirth);
        Bson valueUpdate_at = set("update_at", update_at);
        Bson valueUserUpdate = set("update_by", userUpdate);
        Bson valueEmailUpdate = set("email", request.getEmail());

        Bson value = Updates.combine(valueFullname, valueDateOfBirth, valueUpdate_at, valueUserUpdate, valueEmailUpdate, valueNameSearch);

        db.update(CollectionNameDefs.COLL_USER, cond, value);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse deleteAccount(DeleteAccountRequest request) {
        BaseResponse response = new BaseResponse();

        String username = request.getUsername();
        Bson cond = Filters.eq("username", username);
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


        Bson cond = Filters.eq("username", username);
        Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);

        if (user == null) {
            response.setResult(-1, "Không tồn tại tài khoản này");
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

        Long last_change_password_at = System.currentTimeMillis();

        Bson valueNewPassword = set("password", newPassword);
        Bson valueLast_change_password_at = set("last_change_password_at", last_change_password_at);

        Bson value = Updates.combine(valueNewPassword, valueLast_change_password_at);

        db.update(CollectionNameDefs.COLL_USER, cond, value);

        response.setSuccess();
        return response;

    }

}
