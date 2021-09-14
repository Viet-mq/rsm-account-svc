package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AssignMenuToUserRequest extends BaseAuthRequest {

    private String username;
    private String menuId;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(username)) {
            return new BaseResponse(-1, "Vui lòng chọn tài khoản");
        }
        if (Strings.isNullOrEmpty(menuId)) {
            return new BaseResponse(-1, "Vui lòng chọn menu id");
        }
        return null;
    }

}
