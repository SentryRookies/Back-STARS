package com.example.placeservice.controller;

import com.example.placeservice.dto.accommodation.AccommodationDto;
import com.example.placeservice.service.AccommodationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/main/accommodation")
public class AccommodationController {
    private final AccommodationService accommodationParseService;
    private final AccommodationService accommodationService;

    // 생성자 주입
    public AccommodationController(AccommodationService accommodationParseService, AccommodationService accommodationService) {
        this.accommodationParseService = accommodationParseService;
        this.accommodationService = accommodationService;
    }

    @GetMapping("/list")
    public List<AccommodationDto> getAccommodations() throws IOException {
        return accommodationParseService.showAccommodations();
    }

    @GetMapping("/{accommodation_id}")
    public AccommodationDto getAccommodationById(@PathVariable Long accommodation_id) throws IOException {
        return accommodationService.getAccommodationById(accommodation_id);
    }

    @GetMapping("/gu/{gu}")
    public List<AccommodationDto> getAccommodationByGu(@PathVariable String gu) throws IOException {
        return accommodationService.getAccommodationByGu(gu);
    }

    @GetMapping("/type/{type}")
    public List<AccommodationDto> getAccommodationByType(@PathVariable String type) throws IOException {
        return accommodationService.getAccommodationByType(type);
    }
}
