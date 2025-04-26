package com.example.placeservice.entity;

<<<<<<< HEAD
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
=======
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
<<<<<<< HEAD
    private Long area_id;

    @Column(name = "seoul_id", length = 10, nullable = false)
    private String seoul_id;
=======
    private Long areaId;

    @Column(name = "seoul_id", length = 10, nullable = false, unique = true)
    private String seoulId;
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793

    @Column(name = "name", length = 50, nullable = false)
    private String name;  // 지역명

    @Column(name = "name_eng", length = 100, nullable = false)
<<<<<<< HEAD
    private String name_eng;
=======
    private String nameEng;
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793

    @Column(name = "category", length = 20, nullable = false)
    private String category;  // 구분

<<<<<<< HEAD
    @Column(name = "x", precision = 10, scale = 6, nullable = false)
    private BigDecimal x; // 위도

    @Column(name = "y", precision = 10, scale = 6, nullable = false)
    private BigDecimal y; // 경도


=======
    @Column(name = "lat", precision = 10, scale = 6, nullable = false)
    private BigDecimal lat; // 위도

    @Column(name = "lon", precision = 10, scale = 6, nullable = false)
    private BigDecimal lon; // 경도

    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attraction> attractions;
<<<<<<< HEAD
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793
=======


>>>>>>> 7d9e41f58fcc018b3cd8b94bb5aa79ab788ea9b5
}
