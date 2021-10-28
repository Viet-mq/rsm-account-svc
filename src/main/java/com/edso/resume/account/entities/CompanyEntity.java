package com.edso.resume.account.entities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompanyEntity {
    private String id;
    private String companyName;
    private String companyWebsiteUrl;
    private String companyContactNumber;
    private String companyAddress;
    private List<SubAddressEntity> companySubAddress;
    private String websiteTitle;
    private String websiteLink;
    private String keywords;
    private String favIcon;
    private String companyLogo;
}
