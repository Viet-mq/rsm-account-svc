package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.*;
import com.edso.resume.account.entities.ApiRoleGroupEntity;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;

public interface ApiRoleGroupService {
    GetArrayResponse<ApiRoleGroupEntity> list(HeaderInfo headerInfo, String name, String id, Integer page, Integer size);

    BaseResponse createRoleApiGroup(CreateRoleGroupRequest request);

    BaseResponse updateRoleApiGroup(UpdateRoleGroupRequest request);

    BaseResponse deleteRoleApiGroup(DeleteRoleGroupRequest request);

    BaseResponse assign(AssignApiRoleRequest request);

    BaseResponse revoke(RevokeApiRoleRequest request);

    GetArrayResponse<String> getApiRoleOfUser(String username);

}
