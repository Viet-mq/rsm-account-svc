package com.edso.resume.account.entities;

import lombok.Data;

import java.util.List;

@Data
public class MenuEntity {
    private String id;
    private String name;
    private String icon;
    private Boolean show;
    private List<ActionMenuEntity> actions;
    private List<MenuEntity> children;
}
