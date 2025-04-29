package com.example.userservice.controller;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/list")
    public List<FavoriteDto> getList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return favoriteService.getListData(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<FavoriteDto> addFavorite(@RequestBody FavoriteDto favoriteDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return ResponseEntity.ok(favoriteService.addFavoriteData(userId,favoriteDto));
    }

    @DeleteMapping("/delete/{type}/{id}")
    public ResponseEntity<FavoriteDto> deleteFavorite(@PathVariable String type, @PathVariable String id, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        favoriteService.deleteFavorite(userId, type, id);

        return ResponseEntity.ok().build();
    }
}
