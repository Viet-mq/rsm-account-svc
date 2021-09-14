package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.CreateApiRoleRequest;
import com.edso.resume.account.domain.request.DeleteApiRoleRequest;
import com.edso.resume.account.domain.request.UpdateApiRoleRequest;
import com.edso.resume.account.entities.ApiRoleEntity;
import com.edso.resume.account.service.ApiRoleService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api-role")
public class ApiRoleController extends BaseController {

    private final ApiRoleService apiRoleService;

    public ApiRoleController(ApiRoleService apiRoleService) {
        this.apiRoleService = apiRoleService;
    }

    @GetMapping("/list")
    public GetArrayResponse<ApiRoleEntity> list(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        return apiRoleService.list(headerInfo, id, name, page, size);
    }

    @PostMapping("/create")
    public BaseResponse createApiRole(@RequestHeader Map<String, String> headers, @RequestBody CreateApiRoleRequest request) {
        logger.info("=>createApiRole req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleService.createApiRole(request);
            }
        }
        logger.info("<=createApiRole req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateApiRole(@RequestHeader Map<String, String> headers, @RequestBody UpdateApiRoleRequest request) {
        logger.info("=>updateApiRole req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleService.updateApiRole(request);
            }
        }
        logger.info("<=updateApiRole req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteApiRole(@RequestHeader Map<String, String> headers, @RequestBody DeleteApiRoleRequest request) {
        logger.info("=>deleteApiRole req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = apiRoleService.deleteApiRole(request);
            }
        }
        logger.info("<=deleteApiRole req: {}, resp: {}", request, response);
        return response;
    }

}
