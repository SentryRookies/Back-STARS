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

        // 디버깅 로그 추가
        logger.info("요청 URI: " + request.getRequestURI());

        // 토큰 추출
        String jwt = extractTokenFromRequest(request);

        // 토큰이 없으면 다음 필터로 이동
        if (jwt == null) {
            logger.info("토큰이 없음, 인증 스킵");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰에서 사용자 이름(userId) 추출
            final String username = jwtUtil.extractUsername(jwt);
            logger.info("토큰에서 사용자 ID 추출: " + username);

            // 토큰에서 토큰 ID 추출
            final String tokenId = jwtUtil.extractTokenId(jwt);

            // 보안 컨텍스트에 인증 정보가 없으면 새로 설정
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 토큰이 유효한지 확인
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 사용자 ID 가져오기 (MemberRepository 사용)
                    Member member = memberRepository.findByUserId(username)
                            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
                    Long userId = member.getMemberId();

                    // 토큰이 최신 버전인지 확인
                    if (tokenService.isLatestToken(userId, tokenId)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.info("사용자 인증 성공: " + username + " (권한: " + userDetails.getAuthorities() + ")");
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

    // 요청에서 토큰 추출 헬퍼 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("Authorization 헤더에서 토큰 추출됨");
            return authHeader.substring(7);
        }

        // accessToken 헤더에서 토큰 추출
        final String accessTokenHeader = request.getHeader("accessToken");
        if (accessTokenHeader != null) {
            logger.info("accessToken 헤더에서 토큰 추출됨");
            return accessTokenHeader;
        }

        // 쿠키에서 토큰 추출
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