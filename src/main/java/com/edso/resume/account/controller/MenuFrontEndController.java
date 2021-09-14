package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.AssignMenuToUserRequest;
import com.edso.resume.account.domain.request.CreateMenuFrontendRequest;
import com.edso.resume.account.domain.request.RevokeMenuFromUserRequest;
import com.edso.resume.account.domain.request.UpdateMenuFrontendRequest;
import com.edso.resume.account.service.MenuFrontEndService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/menu-web")
public class MenuFrontEndController extends BaseController {

    private final MenuFrontEndService menuFrontEndService;

    public MenuFrontEndController(MenuFrontEndService menuFrontEndService) {
        this.menuFrontEndService = menuFrontEndService;
    }

    @GetMapping("/list")
    public GetArrayResponse<Document> list(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        return menuFrontEndService.list(headerInfo, id, name, page, size);
    }

    @PostMapping("/create")
    public BaseResponse createMenu(@RequestHeader Map<String, String> headers, @RequestBody CreateMenuFrontendRequest request) {
        logger.info("=>createMenu req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = menuFrontEndService.createMenu(request);
            }
        }
        logger.info("<=createMenu req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/update")
    public BaseResponse updateMenu(@RequestHeader Map<String, String> headers, @RequestBody UpdateMenuFrontendRequest request) {
        logger.info("=>updateMenu req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = menuFrontEndService.updateMenu(request);
            }
        }
        logger.info("<=updateMenu req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/delete")
    public BaseResponse deleteMenu(@RequestHeader Map<String, String> headers, @RequestBody UpdateMenuFrontendRequest request) {
        logger.info("=>deleteMenu req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validateDelete();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = menuFrontEndService.deleteMenu(request);
            }
        }
        logger.info("<=deleteMenu req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/assign")
    public BaseResponse assignToUser(@RequestHeader Map<String, String> headers, @RequestBody AssignMenuToUserRequest request) {
        logger.info("=>assignToUser req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = menuFrontEndService.assignToUser(request);
            }
        }
        logger.info("<=assignToUser req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/revoke")
    public BaseResponse revokeToUser(@RequestHeader Map<String, String> headers, @RequestBody RevokeMenuFromUserRequest request) {
        logger.info("=>revokeToUser req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = menuFrontEndService.revokeToUser(request);
            }
        }
        logger.info("<=revokeToUser req: {}, resp: {}", request, response);
        return response;
    }

}
