package com.example.userservice.service;

import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(Long memberId, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .id(String.valueOf(memberId))  // 여기!! id로 memberId를 문자열로 저장
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(token);
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById((memberId));
    }
}
