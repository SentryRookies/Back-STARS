package com.example.placeservice;

import com.example.placeservice.repository.RestaurantRepository;
import com.example.placeservice.service.RestaurantService;
import com.example.placeservice.service.CafeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class PlaceServiceApplication {

    public static void main(String[] args) {
        // .env 로드하고 시스템 환경변수로 설정
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        System.setProperty("KAKAO_API_KEY", dotenv.get("KAKAO_API_KEY"));

        SpringApplication.run(PlaceServiceApplication.class, args);
    }

    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner initData(RestaurantService restaurantService, RestaurantRepository restaurantRepository, CafeService cafeService) {
        return args -> {
            cafeService.processAllAreas();
            if (restaurantRepository.count() > 0) {
                System.out.println("");
            } else {
                System.out.println("음식점 데이터 저장 시작");
                restaurantService.fetchAndSaveRestaurants();
                System.out.println("음식점 데이터 저장 완료");
            }
        };
    }

}
