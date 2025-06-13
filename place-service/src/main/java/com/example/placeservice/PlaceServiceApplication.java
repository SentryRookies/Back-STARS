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

    /*
    @Bean

    public CommandLineRunner initData(RestaurantService restaurantService, RestaurantRepository restaurantRepository, CafeService cafeService, CafeRepository cafeRepository) {
        return args -> {
            // 음식점 데이터 초기화
            if (restaurantRepository.count() == 0) {
                System.out.println("음식점 데이터 저장 시작");
                restaurantService.fetchAndSaveRestaurants();
                System.out.println("음식점 데이터 저장 완료");
            } else {
                System.out.println("음식점 데이터가 이미 존재합니다. 건너뜁니다.");
            }

            // 카페 데이터 초기화
            if (cafeRepository.count() == 0) {
                System.out.println("카페 데이터 저장 시작");
                cafeService.processAllAreas();
                System.out.println("카페 데이터 저장 완료");
            } else {
                System.out.println("카페 데이터가 이미 존재합니다. 건너뜁니다.");
            }


        };
    }
    */
}