package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.ParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkService parkService;

    @GetMapping(value = "/{areaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getParking(@PathVariable String areaId) {
        return parkService.getParkingData(areaId);
    }
}
