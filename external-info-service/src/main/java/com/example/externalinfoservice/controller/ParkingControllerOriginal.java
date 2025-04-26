//package com.example.externalinfoservice.controller;
//
//import com.example.externalinfoservice.service.ParkServiceOriginal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/parking")
//@RequiredArgsConstructor
//public class ParkingControllerOriginal {
//
//    private final ParkServiceOriginal parkServiceOriginal;
//
//    @GetMapping(value = "/{areaId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String getParking(@PathVariable String areaId) {
//        return parkServiceOriginal.getParkingData(areaId);
//    }
//}
