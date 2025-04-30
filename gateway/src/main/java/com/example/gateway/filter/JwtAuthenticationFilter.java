package com.example.gateway.filter; // 실제 프로젝트 패키지 구조에 맞게 수정하세요

import com.example.gateway.security.JwtTokenProvider; // JwtTokenProvider 위치 확인
// import io.jsonwebtoken.JwtException; // 직접적인 사용은 줄어듦
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List; // 경로 제외 목록을 위해 추가

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    // 인증이 필요 없는 경로 목록 (필요에 따라 추가/수정)
    private final List<String> excludedPaths = List.of(
            "/user/auth/login",
            "/user/auth/signup",
            "/place/",
            "/congestion/",
            "/external/"
    );

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        boolean isExcluded = excludedPaths.stream().anyMatch(p -> path.startsWith(p));
        if (isExcluded) {
            log.debug("인증 필터 제외 경로: {}", path);
            return chain.filter(exchange); // 필터링 없이 다음 단계로
        }

        String token = extractToken(request);

        if (token == null) {
            log.warn("요청 헤더에 JWT 토큰이 없습니다: {}", path);
            return onError(exchange, "토큰 없음", HttpStatus.UNAUTHORIZED);
        }

        if (jwtTokenProvider.validateToken(token)) {
            log.debug("JWT 토큰 유효함: {}", path);

            // (선택 사항) 토큰이 유효하면 사용자 ID 같은 정보를 추출하여 요청 헤더에 추가할 수 있음
            // String userId = jwtTokenProvider.getUserIdFromToken(token);
            // ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            //     .header("X-USER-ID", userId) // 백엔드 서비스에서 사용할 헤더 이름 지정
            //     .build();
            // return chain.filter(exchange.mutate().request(modifiedRequest).build());

            // 기본: 수정 없이 다음 필터로 전달
            return chain.filter(exchange);
        } else {
            // validateToken에서 false를 반환 (내부적으로 로깅됨)
            log.warn("유효하지 않은 JWT 토큰입니다: {}", path);
            return onError(exchange, "토큰 유효하지 않음", HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값 반환
        }
        log.debug("요청 헤더에 Bearer 토큰이 없습니다.");
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(errMessage + " - 요청 URI: {}", exchange.getRequest().getURI());
        return response.setComplete();
    }

    /**
     * 필터의 실행 순서를 지정합니다.
     * 일반적으로 인증 필터는 다른 필터들보다 먼저 실행되어야 하므로 낮은 값을 가집니다.
     * Spring Security Filter보다는 뒤, 다른 로깅/라우팅 필터보다는 앞에 실행되도록 조정할 수 있습니다.
     * (예: NettyRoutingFilter는 Ordered.LOWEST_PRECEDENCE, LoadBalancerClientFilter는 10100)
     * @return 필터 순서 값
     */
    @Override
    public int getOrder() {
        return -1; // 높은 우선순위 (낮은 값)
    }
}