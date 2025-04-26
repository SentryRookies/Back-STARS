package com.example.userservice.controller;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminSignup {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSign dto) {
        try{
            memberService.registerAdmin(dto);
            return ResponseEntity.ok("관리자 회원가입 성공");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("회원가입 실패:"+e.getMessage());
        }
    }

}
