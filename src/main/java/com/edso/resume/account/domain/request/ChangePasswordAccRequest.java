package com.edso.resume.account.domain.request;

import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ChangePasswordAccRequest extends BaseAuthRequest {
    private String username;
    private String newPassword;

    public BaseResponse validate() {
        BaseResponse response = new BaseResponse();
        if (Strings.isNullOrEmpty(username)) {
            response.setResult(-1, "Vui lòng nhập tên đăng nhập");
            return response;
        }
        if (!AppUtils.validateUsername(username)) {
            response.setResult(-1, "Tên đăng nhập không đúng");
            return response;
        }
        if (Strings.isNullOrEmpty(newPassword)) {
            response.setResult(-1, "Vui lòng nhập mật khẩu mới");
            return response;
        }
        return null;
    }
}
