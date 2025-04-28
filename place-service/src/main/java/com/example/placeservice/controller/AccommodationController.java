package com.example.placeservice.controller;

import com.example.placeservice.dto.accommodation.AccommodationDto;
import com.example.placeservice.service.AccommodationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/main/accommodation")
public class AccommodationController {
    private final AccommodationService accommodationParseService;

    // 생성자 주입
    public AccommodationController(AccommodationService accommodationParseService) {
        this.accommodationParseService = accommodationParseService;
    }

    @GetMapping("/list")
    public List<AccommodationDto> getAccommodations() throws IOException {
        return accommodationParseService.showAccommodations();
    }
}
