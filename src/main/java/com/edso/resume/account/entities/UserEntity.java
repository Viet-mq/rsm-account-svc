package com.edso.resume.account.entities;

import lombok.Data;

@Data
public class UserEntity {
    private String username;
    private String fullName;
    private String dateOfBirth;
    private String email;
    private Integer role;
    private Integer status;
    private Long createAt;
    private Long updateAt;
    private Long lastChangePasswordAt;
    private String createBy;
    private String updateBy;
}
