package com.example.placeservice.controller;

import com.example.placeservice.dto.attraction.AreaAttractionsDto;
import com.example.placeservice.dto.attraction.AttractionInfoDto;
import com.example.placeservice.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    // 관광지 목록 조회
    @GetMapping("/attraction/list")
    public List<AreaAttractionsDto> getAttraction() {
        return attractionService.getAttractionData();
    }

    // 관광지 정보 조회(place-code)
    @GetMapping("info/attraction/{place-code}")
    public AttractionInfoDto getAttractionInfo(@PathVariable("place-code") long attractionId) {
        return attractionService.getAttractionInfoData(attractionId);
    }
}
