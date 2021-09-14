package com.edso.resume.account.entities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiRoleGroupEntity {
    private String id;
    private String name;
    private Long createAt;
    private Long updateAt;
    private List<ApiRoleEntity> roles;
}
