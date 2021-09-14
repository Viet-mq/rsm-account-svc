package com.edso.resume.account.controller;

import com.edso.resume.account.domain.request.LoginRequest;
import com.edso.resume.account.domain.request.LogoutRequest;
import com.edso.resume.account.domain.response.LoginResponse;
import com.edso.resume.account.service.AuthenticationService;
import com.edso.resume.lib.common.HeaderDefs;
import com.edso.resume.lib.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/auth")
public class AuthenticationController extends BaseController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response;
        logger.info("=>login req: {}", request);
        response = request.validate();
        if (response == null) {
            response = authenticationService.login(request);
        }
        logger.info("<=login req: {}, resp: {}", request, response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(
            @RequestHeader(HeaderDefs.USER_NAME_IN_HEADER) String username,
            @RequestBody LogoutRequest request) {
        logger.info("=>logout username: {}", username);
        BaseResponse response = authenticationService.logout(request);
        return ResponseEntity.ok(response);
    }

}
