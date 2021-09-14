package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.*;
import com.edso.resume.account.entities.ApiRoleGroupEntity;
import com.edso.resume.account.service.ApiRoleGroupService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api-role-group")
public class ApiRoleGroupController extends BaseController {

    private final ApiRoleGroupService apiRoleGroupService;

    public ApiRoleGroupController(ApiRoleGroupService apiRoleGroupService) {
        this.apiRoleGroupService = apiRoleGroupService;
    }

    @GetMapping("/list")
    public GetArrayResponse<ApiRoleGroupEntity> list(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        return apiRoleGroupService.list(headerInfo, id, name, page, size);
    }

    @PostMapping("/create")
    public BaseResponse createRoleApiGroup(@RequestHeader Map<String, String> headers, @RequestBody CreateRoleGroupRequest request) {
        logger.info("=>createRoleApiGroup req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleGroupService.createRoleApiGroup(request);
            }
        }
        logger.info("<=createRoleApiGroup req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateRoleApiGroup(@RequestHeader Map<String, String> headers, @RequestBody UpdateRoleGroupRequest request) {
        logger.info("=>updateRoleApiGroup req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleGroupService.updateRoleApiGroup(request);
            }
        }
        logger.info("<=updateRoleApiGroup req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteRoleApiGroup(@RequestHeader Map<String, String> headers, @RequestBody DeleteRoleGroupRequest request) {
        logger.info("=>deleteRoleApiGroup req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleGroupService.deleteRoleApiGroup(request);
            }
        }
        logger.info("<=deleteRoleApiGroup req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/assign")
    public BaseResponse assign(@RequestHeader Map<String, String> headers, @RequestBody AssignApiRoleRequest request) {
        logger.info("=>assign req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleGroupService.assign(request);
            }
        }
        logger.info("<=assign req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/revoke")
    public BaseResponse revoke(@RequestHeader Map<String, String> headers, @RequestBody RevokeApiRoleRequest request) {
        logger.info("=>revoke req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleGroupService.revoke(request);
            }
        }
        logger.info("<=revoke req: {}, resp: {}", request, response);
        return response;
    }

}
