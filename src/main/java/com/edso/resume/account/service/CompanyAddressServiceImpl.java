package com.edso.resume.account.service;

import com.edso.resume.account.domain.db.MongoDbOnlineSyncActions;
import com.edso.resume.account.domain.request.CreateCompanyAddressRequest;
import com.edso.resume.account.domain.request.DeleteCompanyAddressRequest;
import com.edso.resume.account.domain.request.UpdateCompanyAddressRequest;
import com.edso.resume.lib.common.CollectionNameDefs;
import com.edso.resume.lib.common.DbKeyConfig;
import com.edso.resume.lib.response.BaseResponse;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompanyAddressServiceImpl extends BaseService implements CompanyAddressService {

    private final MongoDbOnlineSyncActions db;

    public CompanyAddressServiceImpl(MongoDbOnlineSyncActions db) {
        this.db = db;
    }

    @Override
    public BaseResponse createCompanyAddress(CreateCompanyAddressRequest request) {
        BaseResponse response = new BaseResponse();

        Bson cond = Filters.eq(DbKeyConfig.ID, request.getIdCompany());
        Document company = db.findOne(CollectionNameDefs.COLL_COMPANY, cond);

        if (company == null) {
            response.setFailed("Không tồn tại id company này");
            return response;
        }

        Document add = new Document();
        add.append(DbKeyConfig.ID, UUID.randomUUID().toString());
        add.append(DbKeyConfig.NAME, request.getAddress());

        Bson update = Updates.combine(
                Updates.push(DbKeyConfig.COMPANY_SUB_ADDRESS, add)
        );

        // insert to database
        db.update(CollectionNameDefs.COLL_COMPANY, cond, update, true);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse updateCompanyAddress(UpdateCompanyAddressRequest request) {
        BaseResponse response = new BaseResponse();

        Bson cond = Filters.eq(DbKeyConfig.ID, request.getIdCompany());
        Document company = db.findOne(CollectionNameDefs.COLL_COMPANY, cond);

        if (company == null) {
            response.setFailed("Không tồn tại id company này");
            return response;
        }

        Bson cond1 = Filters.eq("company_sub_address.id", request.getIdAddress());

        Bson update = Updates.combine(
                Updates.set("company_sub_address.$.name", request.getAddress())
        );

        // insert to database
        db.update(CollectionNameDefs.COLL_COMPANY, cond1, update, true);

        response.setSuccess();
        return response;
    }

    @Override
    public BaseResponse deleteCompanyAddress(DeleteCompanyAddressRequest request) {
        BaseResponse response = new BaseResponse();

        Bson cond = Filters.eq(DbKeyConfig.ID, request.getIdCompany());
        Document company = db.findOne(CollectionNameDefs.COLL_COMPANY, cond);

        if (company == null) {
            response.setFailed("Không tồn tại id company này");
            return response;
        }

        Bson id = Filters.eq(DbKeyConfig.ID, request.getIdAddress());

        Bson update = Updates.combine(
                Updates.pull(DbKeyConfig.COMPANY_SUB_ADDRESS, id)
        );

        // insert to database
        db.update(CollectionNameDefs.COLL_COMPANY, cond, update, true);

        response.setSuccess();
        return response;
    }
}
