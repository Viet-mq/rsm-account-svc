package com.edso.resume.account.service;

import com.edso.resume.account.common.Common;
import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.LoginRequest;
import com.edso.resume.account.domain.request.LogoutRequest;
import com.edso.resume.account.domain.response.LoginResponse;
import com.edso.resume.account.entities.SessionEntity;
import com.edso.resume.account.entities.TalentPool;
import com.edso.resume.account.repo.SessionRepository;
import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.common.CollectionNameDefs;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.response.GetSingleItemResponse;
import com.mongodb.client.model.Filters;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuthenticationServiceImpl extends BaseService implements AuthenticationService {

    private final MongoDbOnlineSyncActions db;
    private final SessionRepository sessionRepository;
    private final ApiRoleGroupService apiRoleGroupService;
    private final MenuFrontEndService menuFrontEndService;

    public AuthenticationServiceImpl(
            MongoDbOnlineSyncActions db,
            SessionRepository sessionRepository,
            ApiRoleGroupService apiRoleGroupService,
            MenuFrontEndService menuFrontEndService) {
        this.db = db;
        this.sessionRepository = sessionRepository;
        this.apiRoleGroupService = apiRoleGroupService;
        this.menuFrontEndService = menuFrontEndService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        LoginResponse response = new LoginResponse();
        try {

            String username = request.getUsername();
            String token = generateToken(username);

            Bson cond = Filters.eq("username", username);
            Document user = db.findOne(CollectionNameDefs.COLL_USER, cond);

            if (user == null) {
                response.setResult(-1, "Không tồn tại tài khoản này");
                return response;
            }

            String password = AppUtils.parseString(user.get("password"));
            String hash = AppUtils.MD5(request.getPassword());
            hash = AppUtils.MD5(hash);

            if (!password.equals(hash)) {
                response.setResult(-1, "Mật khẩu không đúng");
                return response;
            }

            // delete all session before
            sessionRepository.deleteByUserId(username);

            // get api access permission
            GetArrayResponse<String> r1 = apiRoleGroupService.getApiRoleOfUser(username);
            List<String> ps = new ArrayList<>();
            if (r1 != null && r1.getRows() != null) {
                ps = r1.getRows();
            }

            SessionEntity sessionEntity = SessionEntity.builder()
                    .token(token)
                    .username(username)
                    .role(1)
                    .permissions(ps)
                    .lastRequest(System.currentTimeMillis())
                    .build();
            sessionRepository.addSession(sessionEntity);

            // get menu tree for client
            GetSingleItemResponse<Document> r2 = menuFrontEndService.getMenuOfUser(username);
            List<Document> tree = new ArrayList<>();
            if (r2 != null && r2.getItem() != null) {
                tree = (List<Document>) r2.getItem().get("children");
            }

            if (tree == null) {
                tree = new ArrayList<>();
            }

            // [dautv] : tạm fix để test, sau này sẽ phân quyền lại cho đúng
            List<Document> talentPools = db.findAll("coll_talent_pool", new Document(), null, 0, 100);
            List<TalentPool> pools = new ArrayList<>();
            if (talentPools != null && !talentPools.isEmpty()) {
                for (Document d : talentPools) {
                    TalentPool pool = TalentPool.builder()
                            .id(AppUtils.parseString(d.get("id")))
                            .name(AppUtils.parseString(d.get("name")))
                            .build();
                    pools.add(pool);
                }
            }

            // setup response
            String fullName = AppUtils.parseString(user.get("full_name"));
            response.setPermission(tree);
            response.setUsername(username);
            response.setFullName(fullName);
            response.setAccessToken(sessionEntity.getToken());
            response.setPools(pools);
            response.setSuccess();
            return response;

        } catch (Throwable ex) {
            logger.error("Ex: ", ex);
            response.setResult(-1, "Đăng nhập thất bại");
            return response;
        }

    }

    private String generateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, Common.SECRET.getBytes(StandardCharsets.UTF_8))
                .setIssuedAt(date)
                .compact();
    }

    @Override
    public BaseResponse logout(LogoutRequest request) {
        sessionRepository.deleteByUserId(request.getUsername());
        return new BaseResponse(0, "Logout success");
    }

}
