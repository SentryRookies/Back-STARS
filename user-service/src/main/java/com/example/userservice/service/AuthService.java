package com.example.userservice.service;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.MemberRepository;
import com.example.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        // 디버깅 로그 추가
        System.out.println("로그인 시도: " + request.getUser_id());

        // 사용자 조회 테스트 - userId로 조회하도록 변경
        Optional<Member> memberOptional = memberRepository.findByUserId(request.getUser_id());
        if (memberOptional.isEmpty()) {
            System.out.println("사용자를 찾을 수 없음: " + request.getUser_id());
            throw new RuntimeException("User not found");
        } else {
            System.out.println("사용자 찾음: ID=" + memberOptional.get().getMemberId() + ", UserID=" + memberOptional.get().getUserId());
        }

        // 인증 시도 - userId 사용
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUser_id(),
                            request.getPassword()
                    )
            );
            System.out.println("인증 성공");
        } catch (Exception e) {
            System.out.println("인증 실패: " + e.getMessage());
            throw e;
        }

        Member member = memberOptional.get();

        // JWT 토큰 생성 - userId 사용
        UserDetails userDetails = new User(member.getUserId(), member.getPassword(), Collections.emptyList());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 리프레시 토큰 저장
        tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .member_id(String.valueOf(member.getMemberId()))  // 내부 ID 추가
                .user_id(member.getUserId())  // 로그인 ID 추가
                .nickname(member.getNickname())
                .build();
    }

    public AuthDto.LogoutResponse logout(AuthDto.LogoutRequest request) {
        return AuthDto.LogoutResponse.builder()
                .success(true)
                .message("Logout successful")
                .build();
    }
}