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
        // 인증 시도
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUser_id(),
                        request.getPassword()
                )
        );

        // 사용자 조회
        Optional<Member> memberOptional = memberRepository.findByMemberId(request.getUser_id());
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Member member = memberOptional.get();

        // JWT 토큰 생성
        UserDetails userDetails = new User(member.getMemberId(), member.getPassword(), Collections.emptyList());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 리프레시 토큰 저장
        tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user_id(member.getMemberId())
                .nickname(member.getNickname())
                .build();
    }

    // LogoutRequest 파라미터를 받는 메서드로 수정
    public AuthDto.LogoutResponse logout(AuthDto.LogoutRequest request) {
        // JWT는 stateless이므로 서버 측에서는 특별히 처리할 작업이 없음
        // 클라이언트 측에서 토큰을 제거하도록 안내

        return AuthDto.LogoutResponse.builder()
                .success(true)
                .message("Logout successful")
                .build();
    }
}