package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.CreateCompanyRequest;
import com.edso.resume.account.domain.request.DeleteCompanyRequest;
import com.edso.resume.account.domain.request.UpdateCompanyRequest;
import com.edso.resume.account.entities.CompanyEntity;
import com.edso.resume.account.service.CompanyService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/company")
public class CompanyController extends BaseController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public BaseResponse findAllCompany(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>findAllCompany u: {}, idProfile: {}, page: {}, size: {}", headerInfo, name, page, size);
        GetArrayResponse<CompanyEntity> resp = companyService.findAllCompany(headerInfo, name, page, size);
        logger.info("<=findAllCompany u: {}, idProfile: {}, page: {}, size: {}, resp: {}", headerInfo, name, page, size, resp.info());
        return resp;
    }

    @PostMapping("/create")
    public BaseResponse createCompany(@RequestHeader Map<String, String> headers, @ModelAttribute CreateCompanyRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>createCompany u: {}, req: {}", headerInfo, request);
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = companyService.createCompany(request);
            }
        }
        logger.info("<=createCompany u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateCompany(@RequestHeader Map<String, String> headers, @ModelAttribute UpdateCompanyRequest request) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>updateCompany u: {}, req: {}", headerInfo, request);
        BaseResponse response = request.validate();
        if (response == null) {
            request.setInfo(headerInfo);
            response = companyService.updateCompany(request);
        }
        logger.info("<=updateCompany u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteCompany(@RequestHeader Map<String, String> headers, @RequestBody DeleteCompanyRequest request) {
        logger.info("=>deleteCompany req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = companyService.deleteCompany(request);
            }
        }
        logger.info("<=deleteCompany req: {}, resp: {}", request, response);
        return response;
    }


}
