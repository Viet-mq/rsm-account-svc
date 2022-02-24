package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.CreateCompanyRequest;
import com.edso.resume.account.domain.request.DeleteCompanyRequest;
import com.edso.resume.account.domain.request.UpdateCompanyRequest;
import com.edso.resume.account.entities.CompanyEntity;
import com.edso.resume.account.entities.SubAddressEntity;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CompanyServiceImpl extends BaseService implements CompanyService {

    private final MongoDbOnlineSyncActions db;
    private final DepartmentService departmentService;

//    @Value("${company.favicon.domain}")
//    private String domainFavIcon;
//    @Value("${company.favicon.path}")
//    private String pathFavIcon;
//    @Value("${company.logo.domain}")
//    private String domainLogo;
//    @Value("${company.logo.path}")
//    private String pathLogo;

    public CompanyServiceImpl(MongoDbOnlineSyncActions db, DepartmentService departmentService) {
        this.db = db;
        this.departmentService = departmentService;
    }

    @Override
    public GetArrayResponse<CompanyEntity> findAllCompany(HeaderInfo info, String name, Integer page, Integer size) {
        List<Bson> c = new ArrayList<>();
        if (!Strings.isNullOrEmpty(name)) {
            c.add(Filters.regex(DbKeyConfig.NAME_SEARCH, Pattern.compile(AppUtils.parseVietnameseToEnglish(name))));
        }
        Bson cond = buildCondition(c);
        long total = db.countAll(CollectionNameDefs.COLL_COMPANY, cond);
        PagingInfo pagingInfo = PagingInfo.parse(page, size);
        FindIterable<Document> lst = db.findAll2(CollectionNameDefs.COLL_COMPANY, cond, null, pagingInfo.getStart(), pagingInfo.getLimit());
        List<CompanyEntity> rows = new ArrayList<>();
        if (lst != null) {
            for (Document doc : lst) {

                CompanyEntity department = CompanyEntity.builder()
                        .id(AppUtils.parseString(doc.get(DbKeyConfig.ID)))
                        .companyName(AppUtils.parseString(doc.get(DbKeyConfig.COMPANY_NAME)))
                        .companyWebsiteUrl(AppUtils.parseString(doc.get(DbKeyConfig.COMPANY_WEBSITE_URL)))
                        .companyContactNumber(AppUtils.parseString(doc.get(DbKeyConfig.COMPANY_CONTACT_NUMBER)))
                        .companyAddress(AppUtils.parseString(doc.get(DbKeyConfig.COMPANY_ADDRESS)))
                        .companySubAddress((List<SubAddressEntity>) doc.get(DbKeyConfig.COMPANY_SUB_ADDRESS))
                        .websiteTitle(AppUtils.parseString(doc.get(DbKeyConfig.WEBSITE_TITLE)))
                        .websiteLink(AppUtils.parseString(doc.get(DbKeyConfig.WEBSITE_LINK)))
                        .keywords(AppUtils.parseString(doc.get(DbKeyConfig.KEYWORDS)))
                        .favIcon(AppUtils.parseString(doc.get(DbKeyConfig.FAV_ICON_URL)))
                        .companyLogo(AppUtils.parseString(doc.get(DbKeyConfig.COMPANY_LOGO_URL)))
                        .build();
                rows.add(department);
            }
        }
        GetArrayResponse<CompanyEntity> resp = new GetArrayResponse<>();
        resp.setSuccess();
        resp.setTotal(total);
        resp.setRows(rows);
        return resp;
    }

    @Override
    public BaseResponse createCompany(CreateCompanyRequest request) {
        BaseResponse response = new BaseResponse();

//        MultipartFile favicon = request.getFavIcon();
//        MultipartFile logo = request.getCompanyLogo();

        String name = request.getCompanyName();
        Bson c = Filters.eq(DbKeyConfig.NAME_EQUAL, AppUtils.mergeWhitespace(name.toLowerCase()));
        long count = db.countAll(CollectionNameDefs.COLL_COMPANY, c);

        if (count > 0) {
            response.setFailed("Tên này đã tồn tại");
            return response;
        }

//        //Save file
//        String faviconFileName = null;
//        try {
//            faviconFileName = saveFile(favicon, pathFavIcon);
//        } catch (Throwable ex) {
//            logger.error("Exception: ", ex);
//        }
//
//        String logoFileName = null;
//        try {
//            logoFileName = saveFile(logo, pathLogo);
//        } catch (Throwable ex) {
//            logger.error("Exception: ", ex);
//        }


        Document department = new Document();
        department.append(DbKeyConfig.ID, UUID.randomUUID().toString());
        department.append(DbKeyConfig.COMPANY_NAME, AppUtils.mergeWhitespace(name));
//        department.append(DbKeyConfig.COMPANY_WEBSITE_URL, request.getCompanyWebsiteUrl());
//        department.append(DbKeyConfig.COMPANY_CONTACT_NUMBER, request.getCompanyContactNumber());
//        department.append(DbKeyConfig.COMPANY_ADDRESS, request.getCompanyAddress());
//        department.append(DbKeyConfig.WEBSITE_TITLE, request.getWebsiteTitle());
//        department.append(DbKeyConfig.WEBSITE_LINK, request.getWebsiteLink());
//        department.append(DbKeyConfig.KEYWORDS, request.getKeywords());
//        department.append(DbKeyConfig.FAV_ICON_URL, domainFavIcon + faviconFileName);
//        department.append(DbKeyConfig.FAV_ICON_PATH, pathFavIcon + faviconFileName);
//        department.append(DbKeyConfig.COMPANY_LOGO_URL, domainLogo + logoFileName);
//        department.append(DbKeyConfig.COMPANY_LOGO_PATH, pathLogo + logoFileName);
        department.append(DbKeyConfig.NAME_SEARCH, AppUtils.parseVietnameseToEnglish(name));
        department.append(DbKeyConfig.NAME_EQUAL, AppUtils.mergeWhitespace(name.toLowerCase()));
        department.append(DbKeyConfig.CREATE_AT, System.currentTimeMillis());
        department.append(DbKeyConfig.CREATE_BY, request.getInfo().getUsername());

        // insert to database
        db.insertOne(CollectionNameDefs.COLL_COMPANY, department);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse updateCompany(UpdateCompanyRequest request) {
        BaseResponse response = new BaseResponse();

//        MultipartFile favicon = request.getFavIcon();
//        MultipartFile logo = request.getCompanyLogo();

        String id = request.getId();
        Bson cond = Filters.eq(DbKeyConfig.ID, id);
        Document idDocument = db.findOne(CollectionNameDefs.COLL_COMPANY, cond);

        if (idDocument == null) {
            response.setFailed("Id này không tồn tại");
            return response;
        }

        String name = request.getCompanyName();
        Document obj = db.findOne(CollectionNameDefs.COLL_COMPANY, Filters.eq(DbKeyConfig.NAME_EQUAL, AppUtils.mergeWhitespace(name.toLowerCase())));
        if (obj != null) {
            String objId = AppUtils.parseString(obj.get(DbKeyConfig.ID));
            if (!objId.equals(id)) {
                response.setFailed("Tên này đã tồn tại");
                return response;
            }
        }

//        deleteFile(AppUtils.parseString(idDocument.get(DbKeyConfig.COMPANY_LOGO_PATH)));
//        deleteFile(AppUtils.parseString(idDocument.get(DbKeyConfig.FAV_ICON_PATH)));

//        String faviconFileName = null;
//        try {
//            faviconFileName = saveFile(favicon, pathFavIcon);
//        } catch (Throwable ex) {
//            logger.error("Exception: ", ex);
//        }
//
//        String logoFileName = null;
//        try {
//            logoFileName = saveFile(logo, pathLogo);
//        } catch (Throwable ex) {
//            logger.error("Exception: ", ex);
//        }

        Bson updateProfile = Updates.combine(
                Updates.set(DbKeyConfig.COMPANY_NAME, AppUtils.mergeWhitespace(name))
        );
        db.update(CollectionNameDefs.COLL_PROFILE, Filters.eq(DbKeyConfig.COMPANY_ID, id), updateProfile);

        // update roles
        Bson updates = Updates.combine(
                Updates.set(DbKeyConfig.COMPANY_NAME, AppUtils.mergeWhitespace(name)),
//                Updates.set(DbKeyConfig.COMPANY_WEBSITE_URL, request.getCompanyWebsiteUrl()),
//                Updates.set(DbKeyConfig.COMPANY_CONTACT_NUMBER, request.getCompanyContactNumber()),
//                Updates.set(DbKeyConfig.COMPANY_ADDRESS, request.getCompanyAddress()),
//                Updates.set(DbKeyConfig.WEBSITE_TITLE, request.getWebsiteTitle()),
//                Updates.set(DbKeyConfig.WEBSITE_LINK, request.getWebsiteLink()),
//                Updates.set(DbKeyConfig.KEYWORDS, request.getKeywords()),
//                Updates.set(DbKeyConfig.FAV_ICON_URL, domainFavIcon + faviconFileName),
//                Updates.set(DbKeyConfig.FAV_ICON_PATH, pathFavIcon + faviconFileName),
//                Updates.set(DbKeyConfig.COMPANY_LOGO_URL, domainLogo + logoFileName),
//                Updates.set(DbKeyConfig.COMPANY_LOGO_PATH, pathLogo + logoFileName),
                Updates.set(DbKeyConfig.NAME_SEARCH, AppUtils.parseVietnameseToEnglish(name)),
                Updates.set(DbKeyConfig.NAME_EQUAL, AppUtils.mergeWhitespace(name.toLowerCase())),
                Updates.set(DbKeyConfig.UPDATE_AT, System.currentTimeMillis()),
                Updates.set(DbKeyConfig.UPDATE_BY, request.getInfo().getUsername())
        );
        db.update(CollectionNameDefs.COLL_COMPANY, cond, updates, true);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse deleteCompany(DeleteCompanyRequest request) {
        BaseResponse response = new BaseResponse();
        long count = db.countAll(CollectionNameDefs.COLL_PROFILE, Filters.eq(DbKeyConfig.COMPANY_ID, request.getId()));
        if (count > 0) {
            response.setFailed("Không thể xóa công ty này!");
            return response;
        }

        String id = request.getId();
        Bson cond = Filters.eq(DbKeyConfig.ID, id);
        Document idDocument = db.findOne(CollectionNameDefs.COLL_COMPANY, cond);

        if (idDocument == null) {
            response.setFailed("Id này không tồn tại");
            return response;
        }

        //Xóa file
        deleteFile(AppUtils.parseString(idDocument.get(DbKeyConfig.FAV_ICON_PATH)));
        deleteFile(AppUtils.parseString(idDocument.get(DbKeyConfig.COMPANY_LOGO_PATH)));

        departmentService.deleteDepartmentByIdCompany(id);

        db.delete(CollectionNameDefs.COLL_COMPANY, cond);

        return new BaseResponse(0, "OK");
    }

    public String saveFile(MultipartFile file, String serverPath) {
        FileOutputStream fos = null;
        try {
            String fileName = file.getOriginalFilename();
            File file1 = new File(serverPath + fileName);
            int i = 0;
            while (file1.exists()) {
                i++;
                String[] arr = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
                fileName = arr[0] + " (" + i + ")." + arr[1];
                file1 = new File(serverPath + fileName);
            }
            fos = new FileOutputStream(file1);
            fos.write(file.getBytes());
            return fileName;
        } catch (Throwable ex) {
            logger.error("Exception: ", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Throwable ex) {
                    logger.error("Exception: ", ex);
                }
            }
        }
        return null;
    }

    public void deleteFile(String path) {
        File file = new File(path);
        if (file.delete()) {
            logger.info("deleteFile filePath:{}", path);
        }
    }
}
