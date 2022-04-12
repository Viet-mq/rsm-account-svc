package com.edso.resume.account.entities;

import lombok.Data;
import org.bson.Document;

import java.util.List;

@Data
public class UserEntity {
    private String username;
    private String fullName;
    private String dateOfBirth;
    private String email;
    private Integer role;
    private List<Document> roles;
    private Integer status;
    private Long createAt;
    private Long updateAt;
    private List<String> organizations;
    private Long lastChangePasswordAt;
    private String createBy;
    private String updateBy;
}
