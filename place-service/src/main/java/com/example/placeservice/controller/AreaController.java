package com.example.placeservice.controller;

import com.example.placeservice.dto.AreaDto;
import com.example.placeservice.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    // 지역 목록 조회
    @GetMapping("/area/list")
    public List<AreaDto> getAreaList() {

        return areaService.getAreaData();

    }
}
