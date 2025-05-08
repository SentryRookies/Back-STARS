//package com.example.gateway.config;
// 테스트

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//@Configuration
//public class Cors {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // 인증 정보 포함 X
//        config.addAllowedOrigin("http://localhost:5173"); // 특정 도메인
//        config.addAllowedOrigin("http://58.127.241.84:5173");
//        config.addAllowedOrigin("http://192.168.0.186:5173");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
//
//        return new CorsWebFilter(source);
//    }
//
//
//
//}
