package com.edso.resume.account.domain.request;

import com.edso.resume.account.entities.MenuEntity;
import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UpdateMenuFrontendRequest extends BaseAuthRequest {

    private String id;
    private String name;
    private List<MenuEntity> menu;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(id)) {
            return new BaseResponse(-1, "Vui lòng chọn menu");
        }
        if (Strings.isNullOrEmpty(name)) {
            return new BaseResponse(-1, "Vui lòng nhập tên");
        }
        if (menu == null || menu.isEmpty()) {
            return new BaseResponse(-1, "Vui lòng chọn danh sách menu");
        }
        return null;
    }

    public BaseResponse validateDelete() {
        if (Strings.isNullOrEmpty(id)) {
            return new BaseResponse(-1, "Vui lòng chọn menu");
        }
        return null;
    }

}
