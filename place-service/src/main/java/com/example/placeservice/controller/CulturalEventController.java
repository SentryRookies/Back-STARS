package com.example.placeservice.controller;

import com.example.placeservice.dto.culturalevent.CulturalEventDto;
import com.example.placeservice.entity.CulturalEvent;
import com.example.placeservice.repository.CulturalEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
@Slf4j
public class CulturalEventController {

    private final CulturalEventRepository culturalEventRepository;

    // /main/events: 저장된 모든 이벤트 조회
    @GetMapping("/events")
    public List<CulturalEventDto> getAllEvents() {
        List<CulturalEvent> events = culturalEventRepository.findAll();
        return events.stream()
                .map(CulturalEventDto::new)  // 생성자 이용해서 CulturalEvent -> DTO 변환
                .collect(Collectors.toList());

    }
}
