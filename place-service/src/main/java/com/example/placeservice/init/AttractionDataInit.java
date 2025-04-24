package com.example.placeservice.init;

import com.example.placeservice.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// SB 실행 시, attraction data 저장(area 데이터가 먼저 저장되어 있어야 함)
@Component
@RequiredArgsConstructor
public class AttractionDataInit implements CommandLineRunner {

    private final AttractionService attractionService;

    @Override
    public void run(String... args) throws Exception {
        attractionService.fetchDataFromVisitSeoul(); // 애플리케이션 시작 시 자동 실행
    }

}
