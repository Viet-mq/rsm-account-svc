package com.edso.resume.account.domain.request;

import com.edso.resume.lib.response.BaseResponse;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UpdateCompanyRequest extends BaseAuthRequest {
    private String id;
    private String companyName;
    private String companyWebsiteUrl;
    private String companyContactNumber;
    private String companyAddress;
    private String websiteTitle;
    private String websiteLink;
    private String keywords;
    private MultipartFile favIcon;
    private MultipartFile companyLogo;

    public BaseResponse validate() {
        if (Strings.isNullOrEmpty(id)) {
            return new BaseResponse(-1, "Vui lòng nhập id");
        }
        if (Strings.isNullOrEmpty(companyName)) {
            return new BaseResponse(-1, "Vui lòng nhập tên công ty");
        }
        if (Strings.isNullOrEmpty(companyWebsiteUrl)) {
            return new BaseResponse(-1, "Vui lòng nhập website url");
        }
        if (Strings.isNullOrEmpty(companyContactNumber)) {
            return new BaseResponse(-1, "Vui lòng nhập contact number");
        }
        if (Strings.isNullOrEmpty(companyAddress)) {
            return new BaseResponse(-1, "Vui lòng nhập địa chỉ công ty");
        }
        if (Strings.isNullOrEmpty(websiteTitle)) {
            return new BaseResponse(-1, "Vui lòng nhập tiêu đề website");
        }
        if (Strings.isNullOrEmpty(websiteLink)) {
            return new BaseResponse(-1, "Vui lòng nhập link website");
        }
        if (Strings.isNullOrEmpty(keywords)) {
            return new BaseResponse(-1, "Vui lòng nhập key work");
        }
        if (favIcon == null || favIcon.isEmpty()) {
            return new BaseResponse(-1, "Vui lòng nhập fav icon");
        }
        if (companyLogo == null || companyLogo.isEmpty()) {
            return new BaseResponse(-1, "Vui lòng nhập logo");
        }
        return null;
    }
}
