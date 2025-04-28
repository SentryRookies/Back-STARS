package com.example.userservice.service;

import com.example.userservice.entity.Member;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.repository.redis.RefreshTokenRepository;
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

        // 데이터베이스(레디스)에서 리프레시 토큰 확인
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다.");
        }

        // 사용자 ID로 사용자 조회
        Long userId = Long.valueOf(storedToken.get().getId()); // id는 String으로 저장되니까 Long으로 변환
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새 액세스 토큰 생성
        UserDetails userDetails = new User(member.getUserId(), member.getPassword(), Collections.emptyList());
        return jwtUtil.generateToken(userDetails);
    }

    public void saveRefreshToken(Long memberId, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .id(String.valueOf(memberId))   // memberId를 String으로 변환해서 저장
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(token);
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(Long.valueOf(String.valueOf(memberId)));  // 여기서도 String 변환 필요!
    }
}
