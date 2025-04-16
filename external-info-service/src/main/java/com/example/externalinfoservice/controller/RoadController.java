package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.RoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/traffic")
@RequiredArgsConstructor
public class RoadController {
    private final RoadService roadService;

    @GetMapping("/{area-id}")
    public String getRoadData(@PathVariable("area-id") String id) {
        // TrafficService 호출하여 OpenAPI 데이터를 JSON 형식으로 반환
        return roadService.getTrafficData(id);
    }
}
