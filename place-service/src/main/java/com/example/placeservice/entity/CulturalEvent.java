package com.example.placeservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CulturalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")  // 외래키 컬럼
    @JsonIgnore
    private Area area;

    @Column(name = "category", length = 200)
    private String category;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "address", length = 50)
    private String address;  // PLACE → address

    @Column(name = "lat", precision = 10, scale = 6)
    private BigDecimal lat;

    @Column(name = "lon", precision = 10, scale = 6)
    private BigDecimal lon;

    @Column(name = "target", length = 100)
    private String target;

    @Column(name = "event_fee", columnDefinition = "TEXT")
    private String eventFee;

    @Column(name = "event_img", length = 200)
    private String eventImg;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
