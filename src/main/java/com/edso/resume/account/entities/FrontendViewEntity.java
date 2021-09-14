package com.edso.resume.account.entities;

import com.edso.resume.account.domain.request.AddActionToViewRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FrontendViewEntity {
    private String id;
    private String name;
    private String icon;
    private Boolean show;
    private List<AddActionToViewRequest> actions;
}
