package com.example.userservice.service;

import com.example.userservice.entity.Member;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.MemberRepository;
import com.example.userservice.repository.RefreshTokenRepository;
import com.example.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

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

        // 사용자 ID를 문자열로 변환
        Long userId = storedToken.get().getUserId();
        String userIdStr = String.valueOf(userId);

        // 사용자 조회
        Member member = memberRepository.findById(userIdStr)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새 액세스 토큰 생성
        UserDetails userDetails = new User(member.getMemberId(), member.getPassword(), Collections.emptyList());
        return jwtUtil.generateToken(userDetails);
    }

    public void saveRefreshToken(String memberId, String refreshToken) {
        // String memberId를 고유 숫자 ID로 변환
        // 여기서는 memberId 문자열 자체 대신 해시값을 사용하여 숫자로 변환
        long userId = Math.abs(UUID.nameUUIDFromBytes(memberId.getBytes()).getMostSignificantBits());

        RefreshToken token = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(token);
    }
}