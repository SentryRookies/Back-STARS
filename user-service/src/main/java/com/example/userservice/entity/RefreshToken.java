package com.example.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24 * 7) // 7일 동안 유지
public class RefreshToken {

    @Id
    private String id;  // Redis의 key (보통 memberId나 userId를 넣는다)

    private String refreshToken;
}
