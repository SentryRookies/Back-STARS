package com.example.userservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 21600) // 리프레시토큰 유효기간 6시간
public class TokenRedis {

    @Id
    private String id;

    @Indexed
    private String accessToken;

    private String refreshToken;
}
