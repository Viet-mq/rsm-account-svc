package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.CreateCompanyAddressRequest;
import com.edso.resume.account.domain.request.DeleteCompanyAddressRequest;
import com.edso.resume.account.domain.request.UpdateCompanyAddressRequest;
import com.edso.resume.lib.response.BaseResponse;

public interface CompanyAddressService {
    BaseResponse createCompanyAddress(CreateCompanyAddressRequest request);

    BaseResponse updateCompanyAddress(UpdateCompanyAddressRequest request);

    BaseResponse deleteCompanyAddress(DeleteCompanyAddressRequest request);
}
