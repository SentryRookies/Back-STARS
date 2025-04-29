package com.example.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // 필터링하지 않을 URL 패턴 목록
    private final List<String> excludedPaths = Arrays.asList(
            "/auth/signup",
            "/auth/admin/signup",
            "/auth/login",
            "/auth/logout"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 인증이 필요없는 경로는 필터를 적용하지 않음
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 토큰 추출 시도
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;

        // Authorization 헤더가 있으면 해당 토큰 사용
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // "Bearer " 이후의 문자열 추출
        }
        // 아니면 쿠키에서 토큰 찾기
        else {
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

        // 토큰이 없으면 다음 필터로 이동
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰에서 사용자 이름(userId) 추출
            final String username = jwtUtil.extractUsername(jwt);

            // 보안 컨텍스트에 인증 정보가 없으면 새로 설정
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 토큰이 유효한지 확인
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() // 사용자 권한 포함
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("사용자 인증 성공: " + username + " (권한: " + userDetails.getAuthorities() + ")");
                }
            }
        } catch (Exception e) {
            // 토큰 검증 과정에서 오류가 발생하면 로그만 남기고 계속 진행
            logger.error("JWT 토큰 검증 실패: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
}