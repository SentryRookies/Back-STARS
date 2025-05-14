
//package com.example.externalinfoservice.scheduler;
//
//import com.example.externalinfoservice.controller.AccidentEsController;
//import com.example.externalinfoservice.service.AccidentEsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class AccidentPushScheduler {
//
//    private final AccidentEsService accidentEsService;
//    private final AccidentEsController accidentSseController;
//
//    @Scheduled(fixedRate = 300_000) // 5분마다 실행
//    public void pushAccidentsToClients() {
//        var accidentList = accidentEsService.getAllAccidentsFromES();
//        accidentSseController.sendToClients(accidentList);
//    }
//}
