package com.example.placeservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Attraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attraction_id")
    private Long attraction_id;

    @Column(name = "visit_id", length = 10, nullable = false, unique = true)
    private String visitId;

    @Column(name="name", length = 200, nullable = false)
    private String name;

    @Column(name="address", length = 200, nullable = false)
    private String address;

    @Column(name = "lat", precision = 10, scale = 6, nullable = false)
    private BigDecimal lat; // 위도

    @Column(name = "lon", precision = 10, scale = 6, nullable = false)
    private BigDecimal lon; // 경도


    @Column(name="phone", length = 30)
    private String phone;

    @Column(name="homepage_url", columnDefinition = "TEXT")
    private String homepage_url;

    @Column(name="close_day", columnDefinition = "TEXT")
    private String close_day;

    @Column(name="use_time", columnDefinition = "TEXT")
    private String use_time;

    // 👇 추가: ManyToOne 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;
}
