package com.edso.resume.account.service;

import com.edso.resume.account.common.Common;
import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.LoginRequest;
import com.edso.resume.account.domain.request.LogoutRequest;
import com.edso.resume.account.domain.response.LoginResponse;
import com.edso.resume.account.entities.Permission;
import com.edso.resume.account.entities.SessionEntity;
import com.edso.resume.account.entities.TalentPool;
import com.edso.resume.account.repo.SessionRepository;
import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.common.CollectionNameDefs;
import com.edso.resume.lib.common.DbKeyConfig;
import com.edso.resume.lib.response.BaseResponse;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

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

            int role = AppUtils.parseInt(user.get("role"));
            List<String> orgs = new ArrayList<>();
            List<String> myOrgs = new ArrayList<>();
            Set<String> paths = new HashSet<>();
            List<Permission> permissionList;
            if (role != 1) {
                Set<String> viewRoleIds = new HashSet<>();
                List<Document> listRole = (List<Document>) user.get(DbKeyConfig.ROLES);
                List<String> roleIds = new ArrayList<>();
                for (Document document : listRole) {
                    roleIds.add(AppUtils.parseString(document.get(DbKeyConfig.ID)));
                }
                FindIterable<Document> roles = db.findAll2(CollectionNameDefs.COLL_ROLE, Filters.in(DbKeyConfig.ID, roleIds), null, 0, 0);
                if (roles != null) {
                    Set<String> apiRoleIds = new HashSet<>();
                    for (Document document : roles) {
                        for (Document document1 : (List<Document>) document.get(DbKeyConfig.API_ROLES)) {
                            apiRoleIds.add(AppUtils.parseString(document1.get(DbKeyConfig.ID)));
                        }
                        for (Document document1 : (List<Document>) document.get(DbKeyConfig.VIEW_ROLES)) {
                            viewRoleIds.add(AppUtils.parseString(document1.get(DbKeyConfig.ID)));
                        }
                    }
                    FindIterable<Document> apiRole = db.findAll2(CollectionNameDefs.COLL_API_ROLE, Filters.in(DbKeyConfig.ID, apiRoleIds), null, 0, 0);
                    if (apiRole != null) {
                        Set<String> apiIds = new HashSet<>();
                        for (Document document : apiRole) {
                            apiIds.addAll((List<String>) document.get(DbKeyConfig.APIS));
                        }
                        FindIterable<Document> api = db.findAll2(CollectionNameDefs.COLL_API, Filters.in(DbKeyConfig.ID, apiIds), null, 0, 0);
                        if (api != null) {
                            for (Document document : api) {
                                paths.add(AppUtils.parseString(document.get(DbKeyConfig.PATH)));
                            }
                        }
                    }
                }

                Set<Permission> permissions = new HashSet<>();
                FindIterable<Document> viewRoles = db.findAll2(CollectionNameDefs.COLL_VIEW_ROLE, Filters.in(DbKeyConfig.ID, viewRoleIds), null, 0, 0);
                if (viewRoles != null) {
                    List<Document> list = new ArrayList<>();
                    for (Document document : viewRoles) {
                        List<Document> permissionList1 = (List<Document>) document.get(DbKeyConfig.PERMISSIONS);
                        list.addAll(permissionList1);
                        for (Document document1 : permissionList1) {
                            Permission permission = Permission.builder()
                                    .id(AppUtils.parseString(document1.get(DbKeyConfig.ID)))
                                    .title(AppUtils.parseString(document1.get(DbKeyConfig.TITLE)))
                                    .icon(AppUtils.parseString(document1.get(DbKeyConfig.ICON)))
                                    .path(AppUtils.parseString(document1.get(DbKeyConfig.PATH)))
                                    .index(AppUtils.parseLong(document1.get(DbKeyConfig.INDEX)))
                                    .actions(new HashSet<>())
                                    .build();
                            permissions.add(permission);
                        }
                    }
                    for (Document document : list) {
                        for (Permission p : permissions) {
                            if (p.getId().equals(AppUtils.parseString(document.get(DbKeyConfig.ID)))) {
                                Set<Document> actions = p.getActions();
                                actions.addAll((List<Document>) document.get(DbKeyConfig.ACTIONS));
                                p.setActions(actions);
                            }
                        }
                    }
                }
                permissionList = new LinkedList<>(permissions);
                Collections.sort(permissionList);

                myOrgs = (List<String>) user.get(DbKeyConfig.ORGANIZATIONS);
                getRecursiveFunction(myOrgs, orgs);
            } else {
                FindIterable<Document> list2 = db.findAll2(CollectionNameDefs.COLL_PERMISSION, null, null, 0, 0);
                Set<Permission> permissions = new HashSet<>();
                if (list2 != null) {
                    for (Document document : list2) {
                        Set<Document> actions = new HashSet<>();
                        List<Document> list1 = (List<Document>) document.get(DbKeyConfig.ACTIONS);
                        for (Document document1 : list1) {
                            actions.add(document1);
                        }

                        Permission permission = Permission.builder()
                                .id(AppUtils.parseString(document.get(DbKeyConfig.ID)))
                                .title(AppUtils.parseString(document.get(DbKeyConfig.TITLE)))
                                .icon(AppUtils.parseString(document.get(DbKeyConfig.ICON)))
                                .path(AppUtils.parseString(document.get(DbKeyConfig.PATH)))
                                .index(AppUtils.parseLong(document.get(DbKeyConfig.INDEX)))
                                .actions(actions)
                                .build();
                        permissions.add(permission);
                    }
                }
                permissionList = new LinkedList<>(permissions);
                Collections.sort(permissionList);
                FindIterable<Document> listOrg = db.findAll2(CollectionNameDefs.COLL_ORGANIZATION, null, null, 0, 0);
                for (Document document : listOrg) {
                    myOrgs.add(AppUtils.parseString(document.get(DbKeyConfig.ID)));
                    orgs.add(AppUtils.parseString(document.get(DbKeyConfig.ID)));
                }
            }

            SessionEntity sessionEntity = SessionEntity.builder()
                    .token(token)
                    .username(username)
                    .myOrganizations(myOrgs)
                    .organizations(orgs)
                    .role(role)
                    .apiPaths(paths)
                    .lastRequest(System.currentTimeMillis())
                    .build();
            sessionRepository.addSession(sessionEntity);

            // [dautv] : tạm fix để test, sau này sẽ phân quyền lại cho đúng
            List<Document> talentPools = db.findAll("coll_talent_pool", new Document(), null, 0, 100);
            List<TalentPool> pools = new ArrayList<>();
            if (talentPools != null && !talentPools.isEmpty()) {
                Random random = new Random();
                for (Document d : talentPools) {
                    TalentPool pool = TalentPool.builder()
                            .id(AppUtils.parseString(d.get("id")))
                            .name(AppUtils.parseString(d.get("name")))
                            .count(random.nextInt(500))
                            .build();
                    pools.add(pool);
                }
            }

            // setup response
            response.setPermissions(permissionList);
            response.setUsername(username);
            response.setFullName(AppUtils.parseString(user.get(DbKeyConfig.FULL_NAME)));
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

    private void getRecursiveFunction(List<String> parentIds, List<String> orgs) {
        orgs.addAll(parentIds);
        List<Document> parents = db.findAll(CollectionNameDefs.COLL_ORGANIZATION, Filters.in(DbKeyConfig.PARENT_ID, parentIds), null, 0, 0);
        if (!parents.isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (Document document : parents) {
                ids.add(AppUtils.parseString(document.get(DbKeyConfig.ID)));
            }
            getRecursiveFunction(ids, orgs);
        }

    }

    @Override
    public BaseResponse logout(LogoutRequest request) {
        sessionRepository.deleteByUserId(request.getUsername());
        return new BaseResponse(0, "Logout success");
    }

}
