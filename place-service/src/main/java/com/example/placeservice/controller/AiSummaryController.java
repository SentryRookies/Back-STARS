package com.example.placeservice.controller;

import com.example.placeservice.dto.AiSummaryParsedResponse;
import com.example.placeservice.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class AiSummaryController {

    private final AiSummaryService aiSummaryService;

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<AiSummaryParsedResponse> getSummaryParsed(
            @PathVariable String targetType,
            @PathVariable String targetId) {

        AiSummaryParsedResponse response = aiSummaryService.getSummaryParsed(targetType, targetId);
        return ResponseEntity.ok(response);
    }
}
