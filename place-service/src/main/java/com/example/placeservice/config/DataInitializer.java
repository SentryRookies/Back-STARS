package com.example.placeservice.config;

import com.example.placeservice.dto.AccommodationResponse;
import com.example.placeservice.service.AccommodationParseService;
import com.example.placeservice.service.AccommodationSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AccommodationSaveService accommodationSaveService;
    private final AccommodationParseService accommodationParseService;

    @Override
    public void run(String... args) throws Exception {
        // API 호출 및 XML 파싱
        List<AccommodationResponse.Body.Items.Item> items = accommodationParseService.parseXmlData();

        // 파싱된 데이터 저장
        accommodationSaveService.saveAccommodation(items);

        System.out.println("데이터 초기화가 완료되었습니다.");
    }
}