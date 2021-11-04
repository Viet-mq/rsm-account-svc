package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.CreateCompanyAddressRequest;
import com.edso.resume.account.domain.request.DeleteCompanyAddressRequest;
import com.edso.resume.account.domain.request.UpdateCompanyAddressRequest;
import com.edso.resume.account.service.CompanyAddressService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/address")
public class CompanyAddressController extends BaseController {

    private final CompanyAddressService companyAddressService;

    public CompanyAddressController(CompanyAddressService companyAddressService) {
        this.companyAddressService = companyAddressService;
    }

    @PostMapping("/create")
    public BaseResponse createCompanyAddress(@RequestHeader Map<String, String> headers, @RequestBody CreateCompanyAddressRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>createCompanyAddress u: {}, req: {}", headerInfo, request);
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = companyAddressService.createCompanyAddress(request);
            }
        }
        logger.info("<=createCompanyAddress u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateCompanyAddress(@RequestHeader Map<String, String> headers, @RequestBody UpdateCompanyAddressRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>updateCompanyAddress u: {}, req: {}", headerInfo, request);
        if (request == null) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = companyAddressService.updateCompanyAddress(request);
            }
        }
        logger.info("<=updateCompanyAddress u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteCompanyAddress(@RequestHeader Map<String, String> headers, @RequestBody DeleteCompanyAddressRequest request) {
        logger.info("=>deleteCompanyAddress req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = companyAddressService.deleteCompanyAddress(request);
            }
        }
        logger.info("<=deleteCompanyAddress req: {}, resp: {}", request, response);
        return response;
    }
}
