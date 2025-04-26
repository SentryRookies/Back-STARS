package com.example.placeservice;

<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaceServiceApplication.class, args);
    }

=======
=======
import com.example.placeservice.repository.CafeRepository;
>>>>>>> 7d9e41f58fcc018b3cd8b94bb5aa79ab788ea9b5
import com.example.placeservice.repository.RestaurantRepository;
import com.example.placeservice.repository.CafeRepository;
import com.example.placeservice.service.RestaurantService;
import com.example.placeservice.service.CafeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
<<<<<<< HEAD

>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793
}
=======
}
>>>>>>> 7d9e41f58fcc018b3cd8b94bb5aa79ab788ea9b5
