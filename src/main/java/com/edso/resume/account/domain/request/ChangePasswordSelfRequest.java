package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ChangePasswordSelfRequest extends BaseAuthRequest {

    private String newPassword;
    private String oldPassword;

    public BaseResponse validate() {
        BaseResponse response = new BaseResponse();
        if (Strings.isNullOrEmpty(newPassword)) {
            response.setResult(-1, "Vui lòng nhập mật khẩu mới");
            return response;
        }
//        if (!AppUtils.checkNormalIsdn(newPassword)) {
//            response.setResult(-1, "Mật khẩu mới không đúng định dạng");
//            return response;
//        }
        if (Strings.isNullOrEmpty(oldPassword)) {
            response.setResult(-1, "Vui lòng nhập mật khẩu cũ");
            return response;
        }
//        if (!AppUtils.checkNormalIsdn(oldPassword)) {
//            response.setResult(-1, "Mật khẩu cũ không đúng định dạng");
//            return response;
//        }
        return null;
    }


}
