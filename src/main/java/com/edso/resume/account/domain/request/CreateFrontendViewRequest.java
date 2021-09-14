package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CreateFrontendViewRequest extends BaseAuthRequest {

    private String id;
    private String name;
    private String icon;
    private Boolean show;

    public BaseResponse validate() {
        BaseResponse response = new BaseResponse();
        if (Strings.isNullOrEmpty(id)) {
            response.setResult(-1, "Vui lòng nhập id");
            return response;
        }
        if (Strings.isNullOrEmpty(name)) {
            response.setResult(-1, "Vui lòng nhập tên");
            return response;
        }
        if (icon == null) {
            icon = "";
        }
        if (show == null) {
            show = true;
        }
        return null;
    }

    public BaseResponse validateDelete() {
        if (Strings.isNullOrEmpty(id)) {
            BaseResponse response = new BaseResponse();
            response.setResult(-1, "Vui lòng chọn một view");
            return response;
        }
        return null;
    }

}
