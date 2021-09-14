package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AssignApiRoleRequest extends BaseAuthRequest {

    private String groupApiId;
    private String username;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(groupApiId)) {
            return new BaseResponse(-1, "Vui lòng chọn một group api");
        }
        if (Strings.isNullOrEmpty(username)) {
            return new BaseResponse(-1, "Vui lòng chọn tài khoản được gán quyền");
        }
        return null;
    }

}
