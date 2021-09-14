package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.AddActionToViewRequest;
import com.edso.resume.account.domain.request.CreateFrontendViewRequest;
import com.edso.resume.account.entities.FrontendViewEntity;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;

public interface FrontendViewService {
    GetArrayResponse<FrontendViewEntity> list(HeaderInfo info, String id, String name, Integer page, Integer size);

    BaseResponse createView(CreateFrontendViewRequest request);

    BaseResponse updateView(CreateFrontendViewRequest request);

    BaseResponse deleteView(CreateFrontendViewRequest request);

    BaseResponse addActionOnView(AddActionToViewRequest request);

    BaseResponse removeActionOnView(AddActionToViewRequest request);
}
