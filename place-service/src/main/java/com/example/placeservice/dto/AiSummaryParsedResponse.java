package com.example.placeservice.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class AiSummaryParsedResponse {
    private List<String> positiveKeywords;
    private List<String> negativeKeywords;
    private int positiveCount;
    private int negativeCount;
}