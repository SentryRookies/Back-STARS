package com.example.placeservice.init;

import com.example.placeservice.service.AttractionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AttractionDataInit implements CommandLineRunner {

    private final AttractionService attractionService;

    public AttractionDataInit(AttractionService attractionService) {
        this.attractionService = attractionService;
    }
    @Override
    public void run(String... args) throws Exception {
        attractionService.fetchDataFromVisitSeoul(); // 애플리케이션 시작 시 자동 실행
    }

}
