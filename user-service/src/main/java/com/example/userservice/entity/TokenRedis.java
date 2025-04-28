package com.example.userservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7) // 리프레시토큰과 expiretime 일치
public class TokenRedis {

    @Id
    private String id;

    @Indexed
    private String accessToken;

    private String refreshToken;
}