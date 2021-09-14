package com.edso.resume.account.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("UserSessionEntity")
public class UserSessionEntity implements Serializable {
    @Id
    private String username;
    private Set<String> tokens;
}
