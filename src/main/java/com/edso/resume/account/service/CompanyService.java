package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.CreateCompanyRequest;
import com.edso.resume.account.domain.request.DeleteCompanyRequest;
import com.edso.resume.account.domain.request.UpdateCompanyRequest;
import com.edso.resume.account.entities.CompanyEntity;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;

public interface CompanyService {
    GetArrayResponse<CompanyEntity> findAllCompany(HeaderInfo info, String name, Integer page, Integer size);

    BaseResponse createCompany(CreateCompanyRequest request);

    BaseResponse updateCompany(UpdateCompanyRequest request);

    BaseResponse deleteCompany(DeleteCompanyRequest request);
}
