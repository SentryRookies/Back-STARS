package com.example.userservice.service;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.entity.Member;
import com.example.userservice.entity.RefreshToken;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_VERSION_PREFIX = "token:version:";
    private static final long TOKEN_VERSION_TTL = 2700; // 45분(초 단위)

    /**
     * 로그인 처리
     * @param request 로그인 요청 정보 (user_id, password)
     * @return 토큰과 사용자 정보가 포함된 응답
     */
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        log.debug("로그인 시도: {}", request.getUser_id());

        // 1. 사용자 조회 (userId로 조회)
        Member member = memberRepository.findByUserId(request.getUser_id())
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: {}", request.getUser_id());
                    return new RuntimeException("사용자를 찾을 수 없습니다");
                });

        log.debug("사용자 찾음: ID={}, UserID={}", member.getMemberId(), member.getUserId());

        // 2. 인증 시도
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUser_id(),
                            request.getPassword()
                    )
            );
            log.debug("인증 성공");
        } catch (Exception e) {
            log.error("인증 실패: {}", e.getMessage());
            throw e;
        }

        // 3. 기존 토큰이 있으면 삭제
        Optional<RefreshToken> existingToken = tokenService.findRefreshTokenByMemberId(member.getMemberId());
        if (existingToken.isPresent()) {
            log.debug("기존 리프레시 토큰 발견: 사용자 ID = {}, 삭제 진행", member.getMemberId());
            tokenService.deleteRefreshToken(member.getMemberId());
        }

        // 기존 토큰 버전이 있으면 삭제 (이전 토큰 무효화)
        String versionKey = TOKEN_VERSION_PREFIX + member.getMemberId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(versionKey))) {
            log.debug("기존 토큰 버전 발견: 사용자 ID = {}, 삭제 진행", member.getMemberId());
            redisTemplate.delete(versionKey);
        }

        // 사용자 권한 설정
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole())
        );

        // 4. JWT 토큰 발급 (유효 시간 45분)
        UserDetails userDetails = new User(member.getUserId(), member.getPassword(), authorities);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 5. 토큰 저장 및 버전 관리
        tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

        // 토큰 ID 저장 (버전 관리)
        String tokenId = jwtUtil.extractTokenId(accessToken);
        saveTokenVersion(member.getMemberId(), tokenId);

        log.debug("새 토큰 발급 완료: 액세스 토큰 유효 시간 = 45분, 사용자 ID = {}, 토큰 ID = {}", member.getMemberId(), tokenId);

        // 6. 결과 반환
        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .member_id(String.valueOf(member.getMemberId()))
                .user_id(member.getUserId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }

    /**
     * 로그아웃 처리
     * @param request 로그아웃 요청 정보 (memberId)
     * @return 로그아웃 성공 여부와 메시지
     */
    public AuthDto.LogoutResponse logout(AuthDto.LogoutRequest request) {
        // 사용자 ID 파싱
        Long memberId = Long.parseLong(request.getMemberId());

        // 1. 토큰 버전 삭제 (토큰 무효화)
        String versionKey = TOKEN_VERSION_PREFIX + memberId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(versionKey))) {
            redisTemplate.delete(versionKey);
            log.debug("토큰 버전 삭제 완료: 사용자 ID = {}", memberId);
        }

        // 2. 리프레시 토큰 삭제
        tokenService.deleteRefreshToken(memberId);
        log.debug("리프레시 토큰 삭제 완료: 사용자 ID = {}", memberId);

        log.debug("로그아웃 완료: 사용자 ID = {}", memberId);

        // 3. 성공 리턴
        return AuthDto.LogoutResponse.builder()
                .success(true)
                .message("로그아웃 완료: 모든 토큰 무효화 완료")
                .build();
    }

    /**
     * 토큰 재발급
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰
     */
    public String refreshAccessToken(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. 데이터베이스(레디스)에서 리프레시 토큰 확인
        Optional<RefreshToken> storedToken = tokenService.findRefreshTokenByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다.");
        }

        // 3. 사용자 ID로 사용자 조회
        Long userId = Long.valueOf(storedToken.get().getId()); // id는 String으로 저장되니까 Long으로 변환
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 4. 현재 버전 삭제 (이전 토큰 무효화)
        String versionKey = TOKEN_VERSION_PREFIX + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(versionKey))) {
            redisTemplate.delete(versionKey);
        }

        // 5. 새 액세스 토큰 생성
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole())
        );

        UserDetails userDetails = new User(
                member.getUserId(),
                member.getPassword(),
                authorities
        );

        String newAccessToken = jwtUtil.generateToken(userDetails);

        // 6. 새 토큰 버전 저장
        String tokenId = jwtUtil.extractTokenId(newAccessToken);
        saveTokenVersion(userId, tokenId);

        log.debug("액세스 토큰 재발급 완료: 유효 시간 = 45분, 사용자 ID = {}, 토큰 ID = {}", userId, tokenId);

        return newAccessToken;
    }

    /**
     * 토큰 버전 저장 (현재 유효한 토큰 ID)
     * @param userId 사용자 ID
     * @param tokenId 토큰 ID
     */
    private void saveTokenVersion(Long userId, String tokenId) {
        String key = TOKEN_VERSION_PREFIX + userId;
        redisTemplate.opsForValue().set(key, tokenId, TOKEN_VERSION_TTL, TimeUnit.SECONDS);
        log.debug("토큰 버전 저장: 사용자 ID = {}, 토큰 ID = {}", userId, tokenId);
    }

    /**
     * 토큰이 최신 버전인지 확인
     * @param userId 사용자 ID
     * @param tokenId 토큰 ID
     * @return 최신 토큰이면 true, 아니면 false
     */
    public boolean isLatestToken(Long userId, String tokenId) {
        String key = TOKEN_VERSION_PREFIX + userId;
        String latestTokenId = redisTemplate.opsForValue().get(key);

        if (latestTokenId == null) {
            return false; // 저장된 토큰 ID가 없으면 유효하지 않음 (로그아웃 상태)
        }

        boolean isLatest = latestTokenId.equals(tokenId);
        if (!isLatest) {
            log.debug("토큰 버전 불일치: 사용자 ID = {}, 요청 토큰 ID = {}, 최신 토큰 ID = {}", userId, tokenId, latestTokenId);
        }

        return isLatest;
    }
}