package com.example.externalinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD

@SpringBootApplication
public class ExternalInfoServiceApplication {

    public static void main(String[] args) {
=======
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExternalInfoServiceApplication {

    public static void main(String[] args) {

        
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793
        SpringApplication.run(ExternalInfoServiceApplication.class, args);
    }

}
