package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.AssignMenuToUserRequest;
import com.edso.resume.account.domain.request.CreateMenuFrontendRequest;
import com.edso.resume.account.domain.request.RevokeMenuFromUserRequest;
import com.edso.resume.account.domain.request.UpdateMenuFrontendRequest;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.response.GetSingleItemResponse;
import org.bson.Document;

public interface MenuFrontEndService {
    GetArrayResponse<Document> list(HeaderInfo info, String id, String name, Integer page, Integer size);

    BaseResponse createMenu(CreateMenuFrontendRequest request);

    BaseResponse updateMenu(UpdateMenuFrontendRequest request);

    BaseResponse deleteMenu(UpdateMenuFrontendRequest request);

    BaseResponse assignToUser(AssignMenuToUserRequest request);

    BaseResponse revokeToUser(RevokeMenuFromUserRequest request);

    GetSingleItemResponse<Document> getMenuOfUser(String username);

}
