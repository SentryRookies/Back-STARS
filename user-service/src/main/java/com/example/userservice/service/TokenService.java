package com.example.userservice.service;

import com.example.userservice.entity.Member;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.repository.redis.RefreshTokenRepository;
import com.example.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰 생성
     * @param refreshToken 리프레시 토큰
     * @return 새 액세스 토큰
     */
    public String createNewAccessToken(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. Redis에서 refreshToken과 일치하는 엔트리 찾기 (findByRefreshToken 대신 직접 탐색)
        RefreshToken matchedToken = null;
        for (RefreshToken token : refreshTokenRepository.findAll()) {

                if (token == null || token.getRefreshToken() == null) continue;

                if (token.getRefreshToken().trim().equals(refreshToken.trim())) {
                    matchedToken = token;
                    break;
                }
            }




        if (matchedToken == null) {
            throw new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다.");
        }

        // 3. 사용자 ID로 사용자 조회
        Long userId = Long.valueOf(matchedToken.getId());
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 4. 사용자 권한 설정
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole());

        // 5. 새 액세스 토큰 생성
        UserDetails userDetails = new User(
                member.getUserId(),
                member.getPassword(),
                Collections.singletonList(authority)
        );

        return jwtUtil.generateToken(userDetails);
    }

    /**
     * 리프레시 토큰 저장
     * @param memberId 회원 ID
     * @param refreshToken 리프레시 토큰
     */
    public void saveRefreshToken(Long memberId, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .id(String.valueOf(memberId))   // memberId를 String으로 변환해서 저장
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(token);
    }

    /**
     * 리프레시 토큰 삭제
     * @param memberId 회원 ID
     */
    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(String.valueOf(memberId));
    }
}
