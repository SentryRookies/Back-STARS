package com.example.userservice.controller;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.dto.MemberDto;
import com.example.userservice.entity.Favorite;
import com.example.userservice.service.FavoriteService;
import com.example.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final FavoriteService favoriteService;

    /**
     * 전체 회원 목록 조회 API
     * @return 모든 회원 목록
     */
    @GetMapping("/user/list")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한이 있는 사용자만 접근 가능
    public ResponseEntity<List<MemberDto.MemberResponse>> getAllMembers() {
        List<MemberDto.MemberResponse> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/favorite/list")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한이 있는 사용자만 접근 가능
    public ResponseEntity<List<FavoriteDto>> getAllFavorites() {
        List<FavoriteDto> favorites = favoriteService.getAllFavoriteData();
        return ResponseEntity.ok(favorites);
    }
}