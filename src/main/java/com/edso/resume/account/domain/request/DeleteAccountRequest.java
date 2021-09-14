package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class DeleteAccountRequest extends BaseAuthRequest {
    private String username;

    public BaseResponse validate() {
        BaseResponse response = new BaseResponse();
        if (Strings.isNullOrEmpty(username)) {
            response.setResult(-1, "Vui lòng nhập tên đăng nhập");
            return response;
        }
        return null;
    }
}
