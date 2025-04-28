package com.example.userservice.controller;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.jwt.JwtTokenProvider;
import com.example.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// AdminSignup.java
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AdminSignup {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody MemberSign dto) {
        try {
            Member member = memberService.registerAdmin(dto);

            // 회원가입 성공하면 토큰 발급
            String accessToken = jwtTokenProvider.generateAccessToken(member.getNickname());
            String refreshToken = jwtTokenProvider.generateRefreshToken(member.getNickname());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "회원가입 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
