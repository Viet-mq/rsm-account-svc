package com.edso.resume.account.domain.response;

import com.edso.resume.lib.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LoginResponse extends BaseResponse {

    @JsonProperty("access_token")
    private String accessToken;
    private String username;
    private String fullName;
    private List<Document> permission;

    public LoginResponse() {
        super();
        this.fullName = "";
        this.accessToken = "";
    }

}
