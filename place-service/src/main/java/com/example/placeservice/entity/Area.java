package com.example.placeservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attraction> attractions;
}
