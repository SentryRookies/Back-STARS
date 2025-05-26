package com.example.userservice.controller;

import com.example.userservice.dto.SuggestFastResponseDto;
import com.example.userservice.dto.SuggestRequestDto;
import com.example.userservice.dto.SuggestFastRequestDto;
import com.example.userservice.entity.Member;
import com.example.userservice.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/suggest")
@Slf4j
public class SuggestController {
    @Value("${fastapi-svc}")
    private String fastApiUrl;

    private final MemberService memberService;
    private final RestTemplate restTemplate;

    public SuggestController(MemberService memberService) {
        this.memberService = memberService;
        restTemplate = new RestTemplate();
    }

    @PostMapping("/{user_id}")
    public ResponseEntity<?> suggestTourPlan(@PathVariable String user_id, @RequestBody SuggestRequestDto suggestRequestDto) {
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

        try {
            ResponseEntity<SuggestFastResponseDto> response = restTemplate.postForEntity(fastApiUrl + "/suggest/" + user_id, entity, SuggestFastResponseDto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                SuggestFastResponseDto suggestFastResponseDto = response.getBody();
                log.debug("FastAPI 응답: {}", suggestFastResponseDto);
                return ResponseEntity.ok(suggestFastResponseDto);
            }
            else {
                log.error("FastAPI 요청 실패: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body("FastAPI 서버로부터 오류 응답: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("클라이언트 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("FastAPI 호출 중 클라이언트 오류: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("서버 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("FastAPI 호출 중 서버 오류: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("RestClientException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("FastAPI 서비스 통신 오류: " + e.getMessage());
        }
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getPrevChatsForPlan(@PathVariable String user_id) {
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

        try {
            ResponseEntity<SuggestFastResponseDto[]> responseEntity = restTemplate.getForEntity(fastApiUrl + "/suggest/" + user_id, SuggestFastResponseDto[].class);
            SuggestFastResponseDto[] responseArray = responseEntity.getBody();

            if (responseArray == null) {
                // FastAPI 응답 본문이 null인 경우 (예: FastAPI가 204 No Content를 반환했거나, 문제가 있는 경우)
                // 빈 배열 또는 적절한 오류 응답을 반환할 수 있습니다.
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FastAPI로부터 응답 데이터를 받지 못했습니다.");
            }

            return ResponseEntity.ok(responseArray);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("FastAPI 호출 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
