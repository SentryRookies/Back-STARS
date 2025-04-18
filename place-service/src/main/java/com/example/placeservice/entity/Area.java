package com.example.placeservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long area_id;

    @Column(name = "seoul_id", length = 10, nullable = false)
    private String seoul_id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;  // 지역명

    @Column(name = "name_eng", length = 100, nullable = false)
    private String name_eng;

    @Column(name = "category", length = 20, nullable = false)
    private String category;  // 구분

    @Column(name = "lat", precision = 10, scale = 6, nullable = false)
    private BigDecimal lat; // 위도

    @Column(name = "lon", precision = 10, scale = 6, nullable = false)
    private BigDecimal lon; // 경도


    public Object getName() {
        return this.name;
    }

    public Long getAreaId() {
        return this.area_id;
    }

    public Number getY() {
        return this.lon;
    }

    public Number getX() {
        return this.lat;
    }
}
