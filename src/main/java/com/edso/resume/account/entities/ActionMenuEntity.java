package com.edso.resume.account.entities;

import com.edso.resume.account.domain.request.AddActionToViewRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ActionMenuEntity extends AddActionToViewRequest {
    private Boolean show;
}
