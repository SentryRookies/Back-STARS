package com.example.userservice.controller;

import com.example.userservice.dto.SuggestFastResponseDto;
import com.example.userservice.dto.SuggestRequestDto;
import com.example.userservice.dto.SuggestFastRequestDto;
import com.example.userservice.entity.Member;
import com.example.userservice.service.MemberService;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/suggest")
public class SuggestController {
    private final MemberService memberService;
    private final RestTemplate restTemplate;

    public SuggestController(MemberService memberService) {
        this.memberService = memberService;
        restTemplate = new RestTemplate();
    }

    @PostMapping("/{user_id}")
    public ResponseEntity<?> suggest(@PathVariable String user_id, @RequestBody SuggestRequestDto suggestRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 2. 권한 확인 (요청한 user_id와 인증된 사용자가 동일한지)
        if (!authentication.getName().equals(user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자신의 정보에 대해서만 요청할 수 있습니다.");
        }

        Member member;
        try {
            member = memberService.findByUserId(user_id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 회원을 찾을 수 없습니다: " + user_id);
        }

        SuggestFastRequestDto suggestFastRequestDto = new SuggestFastRequestDto();

        suggestFastRequestDto.setQuestionType(suggestRequestDto.getQuestionType());
        suggestFastRequestDto.setStartTime(suggestRequestDto.getStartTime());
        suggestFastRequestDto.setFinishTime(suggestRequestDto.getFinishTime());
        suggestFastRequestDto.setStartPlace(suggestRequestDto.getStartPlace());
        suggestFastRequestDto.setOptionalRequest(suggestRequestDto.getOptionalRequest());
        suggestFastRequestDto.setBirthYear(member.getBirthYear());
        suggestFastRequestDto.setGender(member.getGender());
        suggestFastRequestDto.setMbti(member.getMbti());

        // FastApi 서버로 POST 요청을 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SuggestFastRequestDto> entity = new HttpEntity<>(suggestFastRequestDto, headers);
        
        String fastApiUrl = "";
        try {
            ResponseEntity<SuggestFastResponseDto> response = restTemplate.postForEntity(fastApiUrl, entity, SuggestFastResponseDto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                SuggestFastResponseDto suggestFastResponseDto = response.getBody();
                System.out.println("FastAPI 응답: " + suggestFastResponseDto);
                return ResponseEntity.ok(suggestFastResponseDto);
            }
            else {
                System.err.println("FastAPI 요청 실패: " + response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body("FastAPI 서버로부터 오류 응답: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("클라이언트 오류: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("FastAPI 호출 중 클라이언트 오류: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            System.err.println("서버 오류: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("FastAPI 호출 중 서버 오류: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            System.err.println("RestClientException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("FastAPI 서비스 통신 오류: " + e.getMessage());
        }
    }
}
