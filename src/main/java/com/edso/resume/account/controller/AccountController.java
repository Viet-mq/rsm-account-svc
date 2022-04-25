package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.*;
import com.edso.resume.account.entities.UserEntity;
import com.edso.resume.account.service.AccountService;
import com.edso.resume.lib.entities.HeaderInfo;
import com.edso.resume.lib.response.BaseResponse;
import com.edso.resume.lib.response.GetArrayResponse;
import com.edso.resume.lib.utils.ParseHeaderUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/list")
    public GetArrayResponse<UserEntity> getAccounts(
            @RequestHeader Map<String, String> headers,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "role", required = false) Integer role,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
        return accountService.getAccounts(headerInfo, username, name, role, status, dateOfBirth, page, size);
    }

    @PostMapping("/create-account")
    public BaseResponse createAccount(@RequestHeader Map<String, String> headers, @RequestBody CreateAccountRequest request) {
        logger.info("=>createAccount req: {}", request);
        BaseResponse response = new BaseResponse();
        if (request == null) {
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin tài khoản");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = accountService.createAccount(request);
            }
        }
        logger.info("<=createAccount req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/update-account")
    public BaseResponse updateAccount(@RequestHeader Map<String, String> headers, @RequestBody UpdateAccountRequest request) {

        logger.info("=> updateAccount req: {}", request);

        BaseResponse response = new BaseResponse();

        if (request == null) {
            //thông báo lỗi
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = accountService.updateAccount(request);
            }
        }
        logger.info("<=updateAccount req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/delete-account")
    public BaseResponse deleteAccount(@RequestHeader Map<String, String> headers, @RequestBody DeleteAccountRequest request) {
        logger.info("=> deleteAccount req: {}", request);

        BaseResponse response = new BaseResponse();

        if (request == null) {
            //thông báo lỗi
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = accountService.deleteAccount(request);
            }
        }
        logger.info("<=deleteAccount req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/change-acc-password")
    public BaseResponse changePasswordAcc(@RequestHeader Map<String, String> headers, @RequestBody ChangePasswordAccRequest request) {

        logger.info("=> ChangeAccountPassword req: {}", request);

        BaseResponse response = new BaseResponse();
        if (request == null) {
            //thông báo lỗi
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {
                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);
                request.setInfo(headerInfo);
                response = accountService.changePasswordAcc(request);
            }
        }
        logger.info("<=ChangeAccountPassword req: {}, resp: {}", request, response);
        return response;
    }

    @PostMapping("/change-password-self")
    public BaseResponse changePasswordSelf(@RequestHeader Map<String, String> headers, @RequestBody ChangePasswordSelfRequest request) {

        logger.info("=> ChangePasswordSelf req: {}", request);

        BaseResponse response = new BaseResponse();

        if (request == null) {
            //thông báo lỗi
            response.setResult(-1, "Vui lòng nhập đầy đủ thông tin");
        } else {
            response = request.validate();
            if (response == null) {

                HeaderInfo headerInfo = ParseHeaderUtil.build(headers);

                request.setInfo(headerInfo);

                response = accountService.changePasswordSelf(request);

            }
        }
        logger.info("<=ChangePasswordSelf req: {}, resp: {}", request, response);
        return response;
    }

}
