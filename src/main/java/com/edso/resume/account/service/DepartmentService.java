package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.CreateDepartmentRequest;
import com.edso.resume.account.domain.request.DeleteDepartmentRequest;
import com.edso.resume.account.domain.request.UpdateDepartmentRequest;
import com.edso.resume.account.entities.DepartmentEntity;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;

public interface DepartmentService {
    GetArrayResponse<DepartmentEntity> findAll(HeaderInfo info, String idCompany, Integer page, Integer size);

    BaseResponse createDepartment(CreateDepartmentRequest request, String idParent);

    BaseResponse updateDepartment(UpdateDepartmentRequest request, String idParent);

    BaseResponse deleteDepartment(DeleteDepartmentRequest request);

    void deleteDepartmentByIdCompany(String idCompany);
}
