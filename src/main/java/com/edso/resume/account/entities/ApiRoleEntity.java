package com.edso.resume.account.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiRoleEntity {
    private String id;
    private String path;
    private String method;
    private String name;
}
