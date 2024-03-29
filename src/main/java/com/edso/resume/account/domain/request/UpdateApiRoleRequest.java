package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UpdateApiRoleRequest extends BaseAuthRequest {

    private String id;
    private String name;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(id)) {
            return new BaseResponse(-1, "Vui lòng nhập id");
        }
        if (Strings.isNullOrEmpty(name)) {
            return new BaseResponse(-1, "Vui lòng nhập name");
        }
        return null;
    }

}
