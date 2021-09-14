package com.edso.resume.account.domain.request;

import com.edso.resume.account.domain.response.LoginResponse;
import com.edso.resume.lib.common.AppUtils;
import com.google.common.base.Strings;
import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;

    public LoginResponse validate() {
        LoginResponse response = new LoginResponse();
        if (Strings.isNullOrEmpty(username)) {
            response.setResult(-1, "Vui lòng nhập tên đăng nhập");
            return response;
        }
        if (!AppUtils.validateUsername(username)) {
            response.setResult(-1, "Tên đăng nhập không đúng định dạng");
            return response;
        }
        if (Strings.isNullOrEmpty(password)) {
            response.setResult(-1, "Vui lòng nhập mật khẩu");
            return response;
        }
        return null;
    }

}
