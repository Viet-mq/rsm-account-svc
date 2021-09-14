package com.edso.resume.account.domain.request;

import com.edso.resume.account.entities.ApiRoleEntity;
import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UpdateRoleGroupRequest extends BaseAuthRequest {

    private String id;
    private String name;
    private List<ApiRoleEntity> roles;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(id)) {
            return new BaseResponse(-1, "Vui lòng chọn group api");
        }
        if (Strings.isNullOrEmpty(name)) {
            return new BaseResponse(-1, "Vui lòng nhập tên group api");
        }
        if (roles == null) {
            roles = new ArrayList<>();
        }
        return null;
    }

}
