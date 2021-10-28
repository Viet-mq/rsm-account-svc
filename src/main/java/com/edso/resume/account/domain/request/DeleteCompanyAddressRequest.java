package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class DeleteCompanyAddressRequest extends BaseAuthRequest {
    private String idCompany;
    private String idAddress;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(idCompany)) {
            return new BaseResponse(-1, "Vui lòng nhập id company");
        }
        if (Strings.isNullOrEmpty(idAddress)) {
            return new BaseResponse(-1, "Vui lòng nhập id address");
        }
        return null;
    }
}
