package com.example.userservice.repository;

import com.example.userservice.entity.TokenRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRedisRepository extends CrudRepository<TokenRedis, String> {

    Optional<TokenRedis> findByAccessToken(String accessToken); // AccessToken으로 찾아내기
}