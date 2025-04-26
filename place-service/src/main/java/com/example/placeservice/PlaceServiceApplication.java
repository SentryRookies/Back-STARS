package com.example.placeservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlaceServiceApplication {

    public static void main(String[] args) {
        // .env 로드하고 시스템 환경변수로 설정
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        System.setProperty("KAKAO_API_KEY", dotenv.get("KAKAO_API_KEY"));

        SpringApplication.run(PlaceServiceApplication.class, args);
    }
}