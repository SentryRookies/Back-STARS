package com.example.placeservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Accommodation {
    @Id
    @Column(name = "accommodation_id")
    private Long accommodationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", referencedColumnName = "area_id", nullable = false)
    private Area area;

    private String name;
    private String address;
    @Column(precision = 10, scale = 6)
    private BigDecimal lat;
    @Column(precision = 10, scale = 6)
    private BigDecimal lon;
    private String phone;
    private String gu;
    private String type;
    @Column(name = "kakaomap_url")
    private String kakaomapUrl;
}
