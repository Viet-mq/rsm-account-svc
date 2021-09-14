package com.edso.resume.account.service;

import com.edso.resume.account.domain.request.LoginRequest;
import com.edso.resume.account.domain.request.LogoutRequest;
import com.edso.resume.account.domain.response.LoginResponse;
import com.edso.resume.lib.response.BaseResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);

    BaseResponse logout(LogoutRequest request);
}
