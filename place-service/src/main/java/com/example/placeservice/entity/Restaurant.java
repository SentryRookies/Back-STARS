package com.example.placeservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "restaurant") // 테이블명 명시
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long restaurantId; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area; // Area 엔티티와 다대일 관계

    @Column(length = 50, nullable = false)
    private String name; // 음식점 이름

    @Column(length = 200)
    private String address; // 음식점 주소

    @Column(precision = 10, scale = 6, nullable = false)
    private BigDecimal lat; // 위도

    @Column(precision = 10, scale = 6, nullable = false)
    private BigDecimal lon; // 경도

    @Column(length = 20)
    private String phone; // 전화번호

    @Column(length = 200)
    private String kakaomap_url; // 카카오맵 URL

    @Column(length = 20)
    private String category_code; // 카테고리 코드 (FD6: 음식점)

//    @Column(length = 30)
//    private String kakao_id;  // 카카오 API가 주는 id 저장 (String)

    //추가
    @Column(length = 100)
    private String categoryGroupName; // (예시) 음식점

    @Column(length = 200)
    private String categoryName; // (예시) 음식점 > 한식 > 육류,고기

    @Column(name = "kakao_id", length = 30)
    private String kakao_id;

    public String getKakao_id() {
        return kakao_id;
    }

    public void setKakao_id(String kakao_id) {
        this.kakao_id = kakao_id;
    }

}


