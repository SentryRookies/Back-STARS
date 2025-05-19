package com.example.userservice.controller;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    //즐겨찾기 리스트 조회
    @GetMapping("/list")
    public List<FavoriteDto> getList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return favoriteService.getListData(userId);
    }

    //즐겨찾기 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addFavorite(@RequestBody FavoriteDto favoriteDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return favoriteService.addFavoriteData(userId,favoriteDto);
    }

    //즐겨찾기 삭제
    @DeleteMapping("/delete/{type}/{id}")
    public ResponseEntity<Map<String, String>> deleteFavorite(@PathVariable String type, @PathVariable String id, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return favoriteService.deleteFavorite(userId, type, id);
    }
}
