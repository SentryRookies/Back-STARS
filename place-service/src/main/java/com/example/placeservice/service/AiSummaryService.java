package com.example.placeservice.service;

import com.example.placeservice.dto.AiSummaryResponse;
import com.example.placeservice.dto.AiSummaryParsedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AiSummaryService {

    private final WebClient webClient;

    // fastapi-svc 값을 application.properties에서 주입
    public AiSummaryService(@Value("${fastapi-svc}") String fastapiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(fastapiUrl)
                .build();
    }

    public AiSummaryParsedResponse getSummaryParsed(String targetType, String targetId) {
        try {
            AiSummaryResponse raw = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/summary/{target_type}/{target_id}")
                            .build(targetType, targetId))
                    .retrieve()
                    .bodyToMono(AiSummaryResponse.class)
                    .block();
            return parseSummaryContent(Objects.requireNonNull(raw));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private AiSummaryParsedResponse parseSummaryContent(AiSummaryResponse raw) {
        String content = raw.getContent();

        List<String> posKeywords = extractKeywords(content, "\\[긍정 키워드\\](.*)");
        List<String> negKeywords = extractKeywords(content, "\\[부정 키워드\\](.*)");
        int posCount = extractInt(content, "\\[긍정 라벨 수\\]\\s*(\\d+)");
        int negCount = extractInt(content, "\\[부정 라벨 수\\]\\s*(\\d+)");

        return AiSummaryParsedResponse.builder()
                .positiveKeywords(posKeywords)
                .negativeKeywords(negKeywords)
                .positiveCount(posCount)
                .negativeCount(negCount)
                .build();
    }

    private List<String> extractKeywords(String content, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(content);
        if (matcher.find()) {
            String[] keywords = matcher.group(1).trim().split("\\s*,\\s*");
            return Arrays.stream(keywords)
                    .filter(k -> !k.isBlank())
                    .toList();
        }
        return List.of();
    }

    private int extractInt(String content, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(content);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
}
