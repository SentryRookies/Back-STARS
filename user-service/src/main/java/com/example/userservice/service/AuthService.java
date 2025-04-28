package com.example.userservice.service;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.jpa.MemberRepository;
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
        // 디버깅 로그
        System.out.println("로그인 시도: " + request.getUser_id());

        // 1. 사용자 조회 (userId로 조회)
        Optional<Member> memberOptional = memberRepository.findByUserId(request.getUser_id());
        if (memberOptional.isEmpty()) {
            System.out.println("사용자를 찾을 수 없음: " + request.getUser_id());
            throw new RuntimeException("User not found");
        }
        Member member = memberOptional.get();
        System.out.println("사용자 찾음: ID=" + member.getMemberId() + ", UserID=" + member.getUserId());

        // 2. 인증 시도
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

        // 3. 토큰 생성
        UserDetails userDetails = new User(member.getUserId(), member.getPassword(), Collections.emptyList());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 4. Refresh Token을 Redis에 저장
        tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

        // 5. 결과 리턴
        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .member_id(String.valueOf(member.getMemberId()))
                .user_id(member.getUserId())
                .nickname(member.getNickname())
                .build();
    }

    public AuthDto.LogoutResponse logout(AuthDto.LogoutRequest request) {
        // 로그아웃 로직도 나중에 redis에서 refresh token 삭제하는 걸 추가할 수 있음
        return AuthDto.LogoutResponse.builder()
                .success(true)
                .message("Logout successful")
                .build();
    }
}
