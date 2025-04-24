package com.example.externalinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExternalInfoServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExternalInfoServiceApplication.class, args);
    }

}
