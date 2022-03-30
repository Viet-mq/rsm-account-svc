package com.edso.resume.account.domain.request;

import com.edso.resume.lib.common.AppUtils;
import com.edso.resume.lib.common.ErrorCodeDefs;
import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UpdateAccountRequest extends BaseAuthRequest {
    private String username;
    private String fullName;
    private String dateOfBirth;
    private List<String> roles;
    private String organization;
    private String email;

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
        if (Strings.isNullOrEmpty(email)) {
            response.setResult(-1, "Bạn chưa nhập email");
            return response;
        }
        if (!AppUtils.validateEmail(email.replaceAll(" ", ""))) {
            response.setResult(-1, "Bạn nhập email chưa đúng định dạng");
            return response;
        }
        if (roles == null || roles.isEmpty()) {
            response.setResult(ErrorCodeDefs.ROLES, "Bạn chưa chọn role nào");
            return response;
        }
        if (Strings.isNullOrEmpty(organization)) {
            response.setResult(-1, "Bạn chưa chọn tổ chức");
            return response;
        }
        if (Strings.isNullOrEmpty(dateOfBirth)) {
            response.setResult(-1, "Vui lòng nhập ngày tháng năm sinh");
            return response;
        }
        return null;
    }
}
