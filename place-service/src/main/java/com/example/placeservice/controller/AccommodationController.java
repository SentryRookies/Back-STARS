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
@RequestMapping("/main")
public class AccommodationController {
    private final AccommodationService accommodationService;

    // 생성자 주입
    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @GetMapping("/accommodation/list")
    public List<AccommodationDto> getAccommodations() throws IOException {
        return accommodationService.showAccommodations();
    }

    @GetMapping("/info/accommodation/{accommodation_id}")
    public AccommodationDto getAccommodationById(@PathVariable Long accommodation_id) throws IOException {
        return accommodationService.getAccommodationById(accommodation_id);
    }

    @GetMapping("/accommodation/gu/{gu}")
    public List<AccommodationDto> getAccommodationByGu(@PathVariable String gu) throws IOException {
        return accommodationService.getAccommodationByGu(gu);
    }

    @GetMapping("/accommodation/type/{type}")
    public List<AccommodationDto> getAccommodationByType(@PathVariable String type) throws IOException {
        return accommodationService.getAccommodationByType(type);
    }
}
