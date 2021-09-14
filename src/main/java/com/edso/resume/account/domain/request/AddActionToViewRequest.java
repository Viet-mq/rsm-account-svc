package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AddActionToViewRequest extends BaseAuthRequest {

    private String viewId;
    private String actionId;
    private String actionName;
    private String desc;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(viewId)) {
            return new BaseResponse(-1, "Vui lòng chọn view");
        }
        if (Strings.isNullOrEmpty(actionId)) {
            return new BaseResponse(-1, "Vui lòng nhập action");
        }
        if (Strings.isNullOrEmpty(actionName)) {
            return new BaseResponse(-1, "Vui lòng nhập tên");
        }
        if (desc == null) {
            desc = "";
        }
        return null;
    }

    public BaseResponse validateRemove() {
        if (Strings.isNullOrEmpty(viewId)) {
            return new BaseResponse(-1, "Vui lòng chọn view");
        }
        if (Strings.isNullOrEmpty(actionId)) {
            return new BaseResponse(-1, "Vui lòng chọn action");
        }
        return null;
    }

}
