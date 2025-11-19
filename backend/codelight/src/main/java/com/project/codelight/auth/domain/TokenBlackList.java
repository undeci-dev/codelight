package com.project.codelight.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "tokenBlackList")
public class TokenBlackList {

    @Id
    private String token;

    @TimeToLive
    private Long expiration;

    @Builder
    public TokenBlackList(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }
}
