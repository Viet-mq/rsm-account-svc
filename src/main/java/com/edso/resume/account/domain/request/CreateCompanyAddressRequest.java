package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CreateCompanyAddressRequest extends BaseAuthRequest {
    private String idCompany;
    private String address;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(idCompany)) {
            return new BaseResponse(-1, "Vui lòng nhập id company");
        }
        if (Strings.isNullOrEmpty(address)) {
            return new BaseResponse(-1, "Vui lòng nhập địa chỉ");
        }
        return null;
    }
}
