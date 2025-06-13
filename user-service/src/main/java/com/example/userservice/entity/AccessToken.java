package com.example.userservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "accessToken", timeToLive = 2700) // 45분(2700초) 후 자동 삭제
public class AccessToken {

    @Id
    private String id; // 사용자 ID를 저장

    @Indexed // 인덱싱하여 검색 가능하도록
    private String accessToken;

    @Builder
    public AccessToken(String id, String accessToken) {
        this.id = id;
        this.accessToken = accessToken;
    }
}