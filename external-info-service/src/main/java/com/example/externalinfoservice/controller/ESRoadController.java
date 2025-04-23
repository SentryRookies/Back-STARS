package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.ESRoadService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/main/traffic")
@RequiredArgsConstructor
public class ESRoadController {
    private final ESRoadService roadService;

    @GetMapping("/{area-nm}")
    public JsonNode Traffic(@PathVariable("area-nm") String areaNm)  {
        return ESRoadService.getTrafficData(areaNm);
    }


}
