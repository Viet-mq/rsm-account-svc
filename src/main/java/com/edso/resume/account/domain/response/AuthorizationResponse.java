package com.edso.resume.account.domain.response;

import com.edso.resume.lib.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthorizationResponse extends BaseResponse {
    private boolean allow = false;

    public AuthorizationResponse() {
        super();
    }
}
