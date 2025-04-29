package com.example.userservice.service;

import com.example.userservice.dto.MemberDto;
import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.jpa.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 일반 사용자 회원가입 (기본 권한은 ROLE_USER)
    public Member registerUser(MemberSign dto) {
        return registerMember(dto, "ROLE_USER");
    }

    // 관리자 회원가입
    public Member registerAdmin(MemberSign dto) {
        return registerMember(dto, "ROLE_ADMIN");
    }

    // 공통 회원가입 로직
    private Member registerMember(MemberSign dto, String defaultRole) {
        // userId 중복 체크
        if (memberRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalStateException("이미 존재하는 사용자 ID입니다.");
        }

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        // DTO에서 역할이 지정되었고 관리자로 등록하는 경우 해당 역할 사용, 아니면 기본 역할 사용
        String role = dto.getRole() != null && defaultRole.equals("ROLE_ADMIN")
                ? dto.getRole()
                : defaultRole;

        // 회원 엔티티 생성
        Member member = Member.builder()
                .userId(dto.getUserId())
                .password(passwordEncoder.encode(dto.getPassword())) // 비밀번호 암호화
                .nickname(dto.getNickname())
                .birthYear(dto.getBirthYear())
                .mbti(dto.getMbti())
                .gender(dto.getGender())
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        // 저장 후 반환
        return memberRepository.save(member);
    }

    // 회원 정보 조회
    public MemberDto.ProfileResponse getProfile(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return MemberDto.ProfileResponse.builder()
                .nickname(member.getNickname())
                .birth_year(member.getBirthYear())
                .mbti(member.getMbti())
                .gender(member.getGender())
                .role(member.getRole())
                .created_at(member.getCreatedAt())
                .build();
    }

    // 회원 정보 수정
    @Transactional
    public MemberDto.MemberResponse updateProfile(String userId, MemberDto.UpdateRequest request) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경이 요청된 경우
        if (request.getCurrent_password() != null && request.getNew_password() != null) {
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(request.getCurrent_password(), member.getPassword())) {
                throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호로 업데이트 (암호화)
            member.setPassword(passwordEncoder.encode(request.getNew_password()));
        }

        // 기타 정보 업데이트
        if (request.getNickname() != null) {
            member.setNickname(request.getNickname());
        }

        if (request.getBirth_year() != null) {
            member.setBirthYear(request.getBirth_year());
        }

        if (request.getMbti() != null) {
            member.setMbti(request.getMbti());
        }

        if (request.getGender() != null) {
            member.setGender(request.getGender());
        }

        // 저장
        Member updatedMember = memberRepository.save(member);

        // 응답 DTO로 변환
        return MemberDto.MemberResponse.builder()
                .user_id(updatedMember.getUserId())
                .nickname(updatedMember.getNickname())
                .birth_year(updatedMember.getBirthYear())
                .mbti(updatedMember.getMbti())
                .gender(updatedMember.getGender())
                .role(updatedMember.getRole())
                .created_at(updatedMember.getCreatedAt())
                .build();
    }
}