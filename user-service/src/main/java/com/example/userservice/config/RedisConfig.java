package com.example.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.example.userservice.repository.redis")
public class RedisConfig {
    // 추가 설정 있으면 작성
}
