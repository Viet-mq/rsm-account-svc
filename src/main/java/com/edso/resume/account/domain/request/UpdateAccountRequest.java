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
public class UpdateAccountRequest extends BaseAuthRequest {
    private String username;
    private String fullName;
    private String dateOfBirth;

    public BaseResponse validate() {
        BaseResponse response = new BaseResponse();
        if (Strings.isNullOrEmpty(username)) {
            response.setResult(-1, "Vui lòng nhập tên đăng nhập");
            return response;
        }
        if (Strings.isNullOrEmpty(fullName)) {
            response.setResult(-1, "Vui lòng nhập họ và tên");
            return response;
        }
        if (!AppUtils.validateFullName(fullName)) {
            response.setResult(-1, "Họ và tên không đúng");
            return response;
        }
        if (Strings.isNullOrEmpty(dateOfBirth)) {
            response.setResult(-1, "Vui lòng nhập ngày tháng năm sinh");
            return response;
        }
        return null;
    }
}
