package com.example.userservice.service;

import com.example.userservice.entity.Member;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.repository.redis.RefreshTokenRepository;
import com.example.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_VERSION_PREFIX = "token:version:";
    private static final long TOKEN_VERSION_TTL = 2700; // 45분

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰 생성
     */
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
        Long userId = Long.valueOf(storedToken.get().getId());
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 토큰 버전 삭제 (이전 액세스 토큰 무효화)
        String key = TOKEN_VERSION_PREFIX + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
            System.out.println("기존 토큰 버전 삭제: 사용자 ID = " + userId);
        }

        // 사용자 권한 설정
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole());

        // 새 액세스 토큰 생성
        UserDetails userDetails = new User(
                member.getUserId(),
                member.getPassword(),
                Collections.singletonList(authority)
        );

        String accessToken = jwtUtil.generateToken(userDetails);

        // 토큰 버전 업데이트
        String tokenId = jwtUtil.extractTokenId(accessToken);
        saveTokenVersion(userId, tokenId);

        System.out.println("새 액세스 토큰 생성 완료: 사용자 ID = " + userId + ", 토큰 ID = " + tokenId);

        return accessToken;
    }

    /**
     * 토큰 버전 저장 (현재 유효한 토큰 ID)
     */
    public void saveTokenVersion(Long userId, String tokenId) {
        String key = TOKEN_VERSION_PREFIX + userId;
        redisTemplate.opsForValue().set(key, tokenId, TOKEN_VERSION_TTL, TimeUnit.SECONDS);
        System.out.println("토큰 버전 업데이트: 사용자 ID = " + userId + ", 토큰 ID = " + tokenId);
    }

    /**
     * 토큰이 최신 버전인지 확인
     */
    public boolean isLatestToken(Long userId, String tokenId) {
        String key = TOKEN_VERSION_PREFIX + userId;
        String latestTokenId = redisTemplate.opsForValue().get(key);

        if (latestTokenId == null) {
            System.out.println("저장된 토큰 버전 없음: 사용자 ID = " + userId);
            return false; // 저장된 토큰이 없으면 로그아웃된 상태로 간주하고 인증 실패
        }

        boolean isLatest = latestTokenId.equals(tokenId);
        if (!isLatest) {
            System.out.println("토큰 버전 불일치: 사용자 ID = " + userId +
                    ", 요청 토큰 ID = " + tokenId +
                    ", 최신 토큰 ID = " + latestTokenId);
        }

        return isLatest;
    }

    /**
     * 토큰 무효화 (토큰 버전 삭제)
     */
    public void invalidateToken(Long userId) {
        String key = TOKEN_VERSION_PREFIX + userId;
        redisTemplate.delete(key);
        System.out.println("토큰 무효화 완료: 사용자 ID = " + userId);
    }

    /**
     * 리프레시 토큰 저장
     */
    public void saveRefreshToken(Long memberId, String refreshToken) {
        // 기존 토큰이 있는지 확인하고 있으면 삭제
        Optional<RefreshToken> existingToken = refreshTokenRepository.findById(String.valueOf(memberId));
        if (existingToken.isPresent()) {
            refreshTokenRepository.deleteById(String.valueOf(memberId));
            System.out.println("기존 리프레시 토큰 삭제: 사용자 ID = " + memberId);
        }

        // 새 토큰 저장
        RefreshToken token = RefreshToken.builder()
                .id(String.valueOf(memberId))
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(token);
        System.out.println("새 리프레시 토큰 저장: 사용자 ID = " + memberId);
    }

    /**
     * 리프레시 토큰 삭제
     */
    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(String.valueOf(memberId));
        System.out.println("리프레시 토큰 삭제 완료: 사용자 ID = " + memberId);
    }

    /**
     * 사용자 ID로 리프레시 토큰 조회
     */
    public Optional<RefreshToken> findRefreshTokenByMemberId(Long memberId) {
        return refreshTokenRepository.findById(String.valueOf(memberId));
    }

    /**
     * 리프레시 토큰 값으로 리프레시 토큰 엔티티 조회
     */
    public Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }
}