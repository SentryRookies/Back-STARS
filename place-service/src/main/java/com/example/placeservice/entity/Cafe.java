package com.example.placeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cafe")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "lat", precision = 10, scale = 6)
    private BigDecimal lat;  // 위도

    @Column(name = "lon", precision = 10, scale = 6)
    private BigDecimal lon;  // 경도

    @Column(name = "phone")
    private String phone;

    @Column(name = "kakaomap_url")
    private String kakaomapUrl;

    @Column(name = "category_code")
    private String categoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;
}