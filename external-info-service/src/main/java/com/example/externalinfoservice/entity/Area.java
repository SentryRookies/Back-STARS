package com.example.externalinfoservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "area")
@Getter
@Setter
public class Area {
    @Id
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "seoul_id")
    private String seoulId;

    @Column(name = "name")
    private String name;

    @Column(name = "name_eng")
    private String nameEng;

    @Column(name = "category")
    private String category;

    @Column(name = "lat")
    private Double latitude;

    @Column(name = "lon")
    private Double longitude;
}