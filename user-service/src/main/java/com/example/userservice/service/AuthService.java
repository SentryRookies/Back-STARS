package com.example.userservice.service;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * 로그인 처리
     * @param request 로그인 요청 정보 (user_id, password)
     * @return 토큰과 사용자 정보가 포함된 응답
     */
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        System.out.println("로그인 시도: " + request.getUser_id());

        // 1. 사용자 조회 (userId로 조회)
        Member member = memberRepository.findByUserId(request.getUser_id())
                .orElseThrow(() -> {
                    System.out.println("사용자를 찾을 수 없음: " + request.getUser_id());
                    return new RuntimeException("사용자를 찾을 수 없습니다");
                });

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

        // 사용자 권한 설정
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole())
        );

        // 3. JWT 토큰 발급
        UserDetails userDetails = new User(member.getUserId(), member.getPassword(), authorities);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 4. Refresh Token Redis 저장
        tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

        // 5. 결과 반환
        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .member_id(String.valueOf(member.getMemberId()))
                .user_id(member.getUserId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }

    /**
     * 로그아웃 처리
     * @param request 로그아웃 요청 정보 (memberId)
     * @return 로그아웃 성공 여부와 메시지
     */
    public AuthDto.LogoutResponse logout(AuthDto.LogoutRequest request) {
        // 1. 사용자의 Redis Refresh Token 삭제
        Long memberId = Long.parseLong(request.getMemberId());
        tokenService.deleteRefreshToken(memberId);

        // 2. 성공 리턴
        return AuthDto.LogoutResponse.builder()
                .success(true)
                .message("로그아웃 완료: RefreshToken 삭제 완료")
                .build();
    }
}