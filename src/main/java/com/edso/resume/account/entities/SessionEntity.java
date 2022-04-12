package com.edso.resume.account.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("SessionEntity")
public class SessionEntity implements Serializable {
    @Id
    private String token;
    private String username;
    private Integer role;
    private List<String> organizations;
    private Set<String> apiPaths;
    private Long lastRequest;
    @Builder.Default
    private Boolean needToDie = false;

}
