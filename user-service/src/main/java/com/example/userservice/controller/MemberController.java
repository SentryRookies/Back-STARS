package com.example.userservice.controller;

import com.example.userservice.dto.MemberDto;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    /**
     * 사용자 프로필 조회 API
     * @param request HTTP 요청 (토큰에서 사용자 ID 추출용)
     * @return 사용자 프로필 정보
     */
    @GetMapping("/mypage/profile")
    public ResponseEntity<MemberDto.ProfileResponse> getProfile(HttpServletRequest request) {
        // 현재 인증된 사용자의 userId 추출
        String userId = extractUserIdFromRequest(request);

        // 서비스를 통해 프로필 정보 조회
        MemberDto.ProfileResponse profile = memberService.getProfile(userId);

        return ResponseEntity.ok(profile);
    }

    /**
     * 사용자 프로필 수정 API
     * @param request HTTP 요청 (토큰에서 사용자 ID 추출용)
     * @param updateRequest 프로필 수정 정보 (닉네임, 생년월일, MBTI, 성별, 비밀번호 등)
     * @return 업데이트된 사용자 정보
     */
    @PostMapping("/mypage/profile/edit")
    public ResponseEntity<MemberDto.MemberResponse> updateProfile(
            HttpServletRequest request,
            @RequestBody MemberDto.UpdateRequest updateRequest) {

        // 현재 인증된 사용자의 userId 추출
        String userId = extractUserIdFromRequest(request);

        // 서비스를 통해 프로필 정보 업데이트
        MemberDto.MemberResponse updatedProfile = memberService.updateProfile(userId, updateRequest);

        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * JWT 토큰에서 userId 추출 헬퍼 메서드
     * @param request HTTP 요청
     * @return 추출된 사용자 ID
     */
    private String extractUserIdFromRequest(HttpServletRequest request) {
        // 요청 디버깅 로그
        log.debug("요청 URI: {}", request.getRequestURI());
        log.debug("Authorization 헤더: {}", request.getHeader("Authorization"));
        log.debug("accessToken 헤더: {}", request.getHeader("accessToken"));

        String authHeader = request.getHeader("Authorization");
        String accessTokenHeader = request.getHeader("accessToken");
        String jwt = null;

        // Authorization 헤더 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.debug("Authorization 헤더에서 토큰 추출: {}", jwt);
        }
        // accessToken 헤더 확인
        else if (accessTokenHeader != null) {
            jwt = accessTokenHeader;
            log.debug("accessToken 헤더에서 토큰 추출: {}", jwt);
        }

        // 토큰 유효성 검사 및 사용자 ID 추출
        if (jwt != null) {
            try {
                String userId = jwtUtil.extractUsername(jwt);
                log.debug("추출된 사용자 ID: {}", userId);
                return userId;
            } catch (Exception e) {
                log.error("토큰 처리 중 오류 발생: {}", e.getMessage());
                e.printStackTrace();
            }
        }

        throw new RuntimeException("인증 토큰이 유효하지 않습니다. 'Authorization: Bearer [token]' 또는 'accessToken' 헤더가 필요합니다.");
    }
}