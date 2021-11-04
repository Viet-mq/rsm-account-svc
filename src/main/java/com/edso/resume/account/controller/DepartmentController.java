package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.CreateDepartmentRequest;
import com.edso.resume.account.domain.request.DeleteDepartmentRequest;
import com.edso.resume.account.domain.request.UpdateDepartmentRequest;
import com.edso.resume.account.entities.DepartmentEntity;
import com.edso.resume.account.service.DepartmentService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Phong ban: R&D, SS, BDA
@RestController
@RequestMapping("/department")
public class DepartmentController extends BaseController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/list")
    public BaseResponse findAllDeparment(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "idCompany") String idCompany,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>findAllDeparment u: {}, idCompany: {}, page: {}, size: {}", headerInfo, idCompany, page, size);
        GetArrayResponse<DepartmentEntity> resp = departmentService.findAll(headerInfo, idCompany, page, size);
        logger.info("<=findAllDeparment u: {}, idCompany: {}, page: {}, size: {}, resp: {}", headerInfo, idCompany, page, size, resp.info());
        return resp;
    }

    @PostMapping("/create")
    public BaseResponse createDepartment(@RequestHeader Map<String, String> headers, @RequestParam(value = "idParent", required = false) String idParent, @RequestBody CreateDepartmentRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>createDepartment u: {}, idParent: {}, req: {}", headerInfo, idParent, request);
        if (request == null) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = departmentService.createDepartment(request, idParent);
            }
        }
        logger.info("<=createDepartment u: {}, idParent: {}, req: {}, resp: {}", headerInfo, idParent, request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateDepartment(@RequestHeader Map<String, String> headers, @RequestParam(value = "idParent", required = false) String idParent, @RequestBody UpdateDepartmentRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>updateDepartment u: {}, idParent: {}, req: {}", headerInfo, idParent, request);
        if (request == null) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = departmentService.updateDepartment(request, idParent);
            }
        }
        logger.info("<=updateDepartment u: {}, idParent: {}, req: {}, resp: {}", headerInfo, idParent, request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteDepartment(@RequestHeader Map<String, String> headers, @RequestBody DeleteDepartmentRequest request) {
        logger.info("=>deleteDepartment req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = departmentService.deleteDepartment(request);
            }
        }
        logger.info("<=deleteDepartment req: {}, resp: {}", request, response);
        return response;
    }
}
