package com.example.userservice.security;

import com.example.userservice.entity.Member;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.service.TokenService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    // 완전 일치 제외 경로 목록
    private final List<String> excludedExactPaths = Arrays.asList(
            "/auth/signup",
            "/auth/admin/signup",
            "/auth/login",
            "/auth/logout",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("요청 URI: " + request.getRequestURI());

        String jwt = extractTokenFromRequest(request);

        if (jwt == null) {
            logger.info("토큰이 없음, 인증 스킵");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.extractUsername(jwt);
            logger.info("토큰에서 사용자 ID 추출: " + username);

            String tokenId = jwtUtil.extractTokenId(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    Member member = memberRepository.findByUserId(username)
                            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
                    Long userId = member.getMemberId();

                    if (tokenService.isLatestToken(userId, tokenId)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.info("사용자 인증 성공: " + username);
                    } else {
                        logger.warn("토큰 검증 실패: 이전 버전의 토큰");
                    }
                } else {
                    logger.warn("토큰 검증 실패: 유효하지 않은 토큰");
                }
            }
        } catch (Exception e) {
            logger.error("JWT 토큰 검증 실패: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Swagger 및 인증 제외 경로 처리
    public boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludedExactPaths.contains(path)
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    // 토큰 추출 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("Authorization 헤더에서 토큰 추출됨");
            return authHeader.substring(7);
        }

        final String accessTokenHeader = request.getHeader("accessToken");
        if (accessTokenHeader != null) {
            logger.info("accessToken 헤더에서 토큰 추출됨");
            return accessTokenHeader;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    logger.info("쿠키에서 토큰 추출됨");
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
