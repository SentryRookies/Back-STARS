package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24 * 7) // 7일 동안 유지
public class RefreshToken {

    @Id
    @Column(name = "user_id")
    private Long userId;
    private String id;  // Redis의 key (보통 memberId나 userId를 넣는다)

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;
}
