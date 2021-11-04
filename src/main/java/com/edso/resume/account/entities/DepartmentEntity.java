package com.edso.resume.account.entities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DepartmentEntity {
    private String id;
    private String idCompany;
    private String name;
    private List<SubDepartmentEntity> child;
}
