package com.example.placeservice.config;

import com.example.placeservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AccommodationService accommodationService;

    @Override
    public void run(String... args) throws Exception {
        // API 호출 및 XML 파싱
        accommodationService.saveAccommodations();

        System.out.println("데이터 초기화가 완료되었습니다.");
    }
}