package com.example.userservice.controller;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    /**
     * 로그인 API
     * @param request 로그인 요청 (user_id, password)
     * @param response HTTP 응답 (쿠키 설정용)
     * @return 로그인 성공 시 토큰 및 사용자 정보
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(
            @RequestBody AuthDto.LoginRequest request,
            HttpServletResponse response) {

        // 디버깅 로그 출력
        System.out.println("로그인 컨트롤러 호출: " + request.getUser_id());

        AuthDto.LoginResponse loginResponse = authService.login(request);

        // 쿠키에 토큰 저장
        cookieUtil.addCookie(response, "access_token", loginResponse.getAccessToken(), cookieUtil.getDaysInSeconds(1));
        cookieUtil.addCookie(response, "refresh_token", loginResponse.getRefreshToken(), cookieUtil.getDaysInSeconds(30));

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 로그아웃 API
     * @param request HTTP 요청 (토큰 추출용)
     * @param response HTTP 응답 (쿠키 삭제용)
     * @return 로그아웃 성공 여부 및 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthDto.LogoutResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        // 쿠키 삭제
        cookieUtil.addCookie(response, "access_token", "", 0);
        cookieUtil.addCookie(response, "refresh_token", "", 0);

        // 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            // 쿠키에서 토큰 찾기 (선택적)
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (jwt != null) {
            // 토큰에서 사용자 정보 추출
            String userId = jwtUtil.extractUsername(jwt);
            // 사용자 ID로 Member 조회
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

            // 로그아웃 처리
            return ResponseEntity.ok(authService.logout(
                    AuthDto.LogoutRequest.builder()
                            .memberId(String.valueOf(member.getMemberId()))
                            .build()));
        }

        return ResponseEntity.badRequest().body(
                AuthDto.LogoutResponse.builder()
                        .success(false)
                        .message("유효한 토큰이 없습니다.")
                        .build());
    }
}