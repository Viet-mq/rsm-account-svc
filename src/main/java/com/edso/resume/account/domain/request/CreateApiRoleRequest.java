package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CreateApiRoleRequest extends BaseAuthRequest {

    private String path;
    private String method;
    private String name;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(path)) {
            return new BaseResponse(-1, "Vui lòng nhập path");
        }
        if (Strings.isNullOrEmpty(method)) {
            return new BaseResponse(-1, "Vui lòng nhập phương thức");
        }
        if (Strings.isNullOrEmpty(name)) {
            return new BaseResponse(-1, "Vui lòng nhập name");
        }
        return null;
    }

}
