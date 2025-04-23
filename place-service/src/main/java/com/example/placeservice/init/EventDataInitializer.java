package com.example.placeservice.init;

import com.example.placeservice.service.CulturalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventDataInitializer implements CommandLineRunner {

    private final CulturalEventService culturalEventService;

    @Override
    public void run(String... args) {
        // 애플리케이션 시작 시 데이터를 자동으로 받아와 저장
        culturalEventService.fetchAndSaveAllEvents();
    }
}
