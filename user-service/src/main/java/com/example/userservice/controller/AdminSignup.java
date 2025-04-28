package com.example.userservice.controller;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AdminSignup {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody MemberSign dto) {
        try {
            // 디버깅 로그 출력
            System.out.println("회원가입 요청: userId=" + dto.getUserId() + ", nickname=" + dto.getNickname() + ", birth_year=" + dto.getBirthYear());

            Member member = memberService.registerAdmin(dto);

            System.out.println("회원가입 성공: memberId=" + member.getMemberId() + ", userId=" + member.getUserId() + ", nickname=" + member.getNickname());

            // 회원가입 성공하면 토큰 발급 - userId 사용
            UserDetails userDetails = new User(member.getUserId(), member.getPassword(), Collections.emptyList());
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            tokens.put("member_id", String.valueOf(member.getMemberId()));
            tokens.put("user_id", member.getUserId());
            tokens.put("nickname", member.getNickname());

            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "회원가입 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}