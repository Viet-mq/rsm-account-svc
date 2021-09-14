package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.AddActionToViewRequest;
import com.edso.resume.account.domain.request.CreateFrontendViewRequest;
import com.edso.resume.account.entities.FrontendViewEntity;
import com.edso.resume.account.service.FrontendViewService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/frontend-view")
public class FrontendViewController extends BaseController {

    private final FrontendViewService frontendViewService;

    public FrontendViewController(FrontendViewService frontendViewService) {
        this.frontendViewService = frontendViewService;
    }

    @GetMapping("/list")
    public GetArrayResponse<FrontendViewEntity> list(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        return frontendViewService.list(headerInfo, id, name, page, size);
    }

    @PostMapping("/create")
    public BaseResponse createView(@RequestHeader Map<String, String> headers, @RequestBody CreateFrontendViewRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>createView u: {}, req: {}", headerInfo, request);
        if (request == null || !headerInfo.validate()) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = frontendViewService.createView(request);
            }
        }
        logger.info("<=createView u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateView(@RequestHeader Map<String, String> headers, @RequestBody CreateFrontendViewRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>updateView u: {}, req: {}", headerInfo, request);
        if (request == null || !headerInfo.validate()) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = frontendViewService.updateView(request);
            }
        }
        logger.info("<=updateView u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteView(
            @RequestHeader Map<String, String> headers,
            @RequestBody CreateFrontendViewRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>deleteView u: {}, req: {}", headerInfo, request);
        if (request == null || !headerInfo.validate()) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validateDelete();
            if (response == null) {
                request.setInfo(headerInfo);
                response = frontendViewService.deleteView(request);
            }
        }
        logger.info("<=deleteView u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/add-action")
    public BaseResponse addActionOnView(@RequestHeader Map<String, String> headers, @RequestBody AddActionToViewRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>addActionOnView u: {}, req: {}", headerInfo, request);
        if (request == null || !headerInfo.validate()) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                request.setInfo(headerInfo);
                response = frontendViewService.addActionOnView(request);
            }
        }
        logger.info("<=addActionOnView u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

    @PostMapping("/remove-action")
    public BaseResponse removeActionOnView(@RequestHeader Map<String, String> headers, @RequestBody AddActionToViewRequest request) {
        BaseResponse response = new BaseResponse();
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        logger.info("=>removeActionOnView u: {}, req: {}", headerInfo, request);
        if (request == null || !headerInfo.validate()) {
            response.setResult(-1, "Vui lòng điền đầy đủ thông tin");
        } else {
            response = request.validateRemove();
            if (response == null) {
                request.setInfo(headerInfo);
                response = frontendViewService.removeActionOnView(request);
            }
        }
        logger.info("<=removeActionOnView u: {}, req: {}, resp: {}", headerInfo, request, response);
        return response;
    }

}
