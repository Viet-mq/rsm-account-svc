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
public class CreateAccountRequest extends BaseAuthRequest {
    private String username;
    private String password;
    private String fullName;
    private Integer role;
    private String dateOfBirth;

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
        if (Strings.isNullOrEmpty((password))) {
            response.setResult(-1, "Vui lòng nhập mật khẩu");
            return response;
        }
        if (Strings.isNullOrEmpty(fullName)) {
            response.setResult(-1, "Bạn chưa nhập họ và tên");
            return response;
        }
        if (!AppUtils.validateFullName(fullName)) {
            response.setResult(-1, "Họ và tên không đúng định dạng");
            return response;
        }
        if (role == 0) {
            response.setResult(-1, "Bạn chưa chọn quyền nào");
            return response;
        }
        if (Strings.isNullOrEmpty(dateOfBirth)) {
            response.setResult(-1, "Bạn chưa điền ngày tháng năm sinh");
            return response;
        }
        return null;
    }

}
