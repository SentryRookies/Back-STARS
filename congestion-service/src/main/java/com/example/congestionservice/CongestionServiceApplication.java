package com.example.congestionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CongestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CongestionServiceApplication.class, args);
    }

}
