package com.example.placeservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accommodation_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", referencedColumnName = "area_id", nullable = false)
    private Area area;

    private Long content_id;
    private String category;
    private String name;
    private String address;
    @Column(precision = 10, scale = 6)
    private BigDecimal lat;
    @Column(precision = 10, scale = 6)
    private BigDecimal lon;
    private String phone;
    private String sigungu_code;
    private String image_url;
}
