package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.CreateApiRoleRequest;
import com.edso.resume.account.domain.request.DeleteApiRoleRequest;
import com.edso.resume.account.domain.request.UpdateApiRoleRequest;
import com.edso.resume.account.entities.ApiRoleEntity;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;

public interface ApiRoleService {

    GetArrayResponse<ApiRoleEntity> list(HeaderInfo headerInfo, String name, String id, Integer page, Integer size);

    BaseResponse createApiRole(CreateApiRoleRequest request);

    BaseResponse updateApiRole(UpdateApiRoleRequest request);

    BaseResponse deleteApiRole(DeleteApiRoleRequest request);

}
