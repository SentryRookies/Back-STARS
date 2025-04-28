package com.example.userservice.service;

import com.example.userservice.entity.Member;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.redis.RefreshTokenRepository;
import com.example.userservice.repository.MemberRepository;
import com.example.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createNewAccessToken(String refreshToken) {
        // 리프레시 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 데이터베이스에서 리프레시 토큰 확인
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다.");
        }

        // 사용자 ID로 사용자 조회
        Long userId = storedToken.get().getUserId();
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새 액세스 토큰 생성
        UserDetails userDetails = new User(member.getNickname(), member.getPassword(), Collections.emptyList());
        return jwtUtil.generateToken(userDetails);
    }

    public void saveRefreshToken(Long memberId, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .id(String.valueOf(memberId))  // 여기!! id로 memberId를 문자열로 저장
                .refreshToken(refreshToken)
                .build();
        RefreshToken token = new RefreshToken(memberId, refreshToken);
        refreshTokenRepository.save(token);
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById((memberId));
    }
}

}