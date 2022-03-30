package com.edso.resume.account.entities;

import lombok.Builder;
import lombok.Data;
import org.bson.Document;

import java.util.Set;

@Data
@Builder
public class Permission implements Comparable<Permission> {
    private String id;
    private String title;
    private String icon;
    private String path;
    private Long index;
    private Set<Document> actions;

    @Override
    public int compareTo(Permission p) {
        return index.compareTo(p.index);
    }
}
