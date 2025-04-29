package com.example.userservice.controller;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.MemberService;
import com.example.userservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AdminSignup {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    /**
     * 일반 사용자 회원가입 API
     * @param dto 회원가입 정보 (user_id, password, nickname, birth_year, mbti, gender)
     * @return 토큰 및 사용자 정보가 포함된 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody MemberSign dto) {
        try {
            // 디버깅 로그 출력
            System.out.println("회원가입 요청: userId=" + dto.getUserId() + ", nickname=" + dto.getNickname()
                    + ", birth_year=" + dto.getBirthYear()
                    + ", mbti=" + dto.getMbti()
                    + ", gender=" + dto.getGender());

            // 일반 사용자로 등록
            Member member = memberService.registerUser(dto);

            System.out.println("회원가입 성공: memberId=" + member.getMemberId()
                    + ", userId=" + member.getUserId()
                    + ", nickname=" + member.getNickname()
                    + ", role=" + member.getRole());

            // 사용자 권한 설정
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(member.getRole())
            );

            // 회원가입 성공하면 토큰 발급
            UserDetails userDetails = new User(member.getUserId(), member.getPassword(), authorities);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 리프레시 토큰 저장
            tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

            // 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("member_id", String.valueOf(member.getMemberId()));
            response.put("user_id", member.getUserId());
            response.put("nickname", member.getNickname());
            response.put("role", member.getRole());

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "회원가입 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    //회원탈퇴
    @DeleteMapping("/signout/{userId}")
    public ResponseEntity<String> signout(@PathVariable String userId) {
        try {
            memberService.deleteMember(userId);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("회원 탈퇴 실패: " + e.getMessage());
        }
    }


    /**
     * 관리자 회원가입 API
     * @param dto 관리자 회원가입 정보
     * @return 토큰 및 관리자 정보가 포함된 응답
     */
    @PostMapping("/admin/signup")
    public ResponseEntity<Map<String, String>> adminSignup(@RequestBody MemberSign dto) {
        try {
            // 디버깅 로그 출력
            System.out.println("관리자 회원가입 요청: userId=" + dto.getUserId() + ", nickname=" + dto.getNickname());

            // 관리자로 등록
            Member member = memberService.registerAdmin(dto);

            System.out.println("관리자 회원가입 성공: memberId=" + member.getMemberId()
                    + ", userId=" + member.getUserId()
                    + ", nickname=" + member.getNickname()
                    + ", role=" + member.getRole());

            // 관리자 권한 설정
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(member.getRole())
            );

            // 회원가입 성공하면 토큰 발급
            UserDetails userDetails = new User(member.getUserId(), member.getPassword(), authorities);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 리프레시 토큰 저장
            tokenService.saveRefreshToken(member.getMemberId(), refreshToken);

            // 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("member_id", String.valueOf(member.getMemberId()));
            response.put("user_id", member.getUserId());
            response.put("nickname", member.getNickname());
            response.put("role", member.getRole());

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "관리자 회원가입 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}