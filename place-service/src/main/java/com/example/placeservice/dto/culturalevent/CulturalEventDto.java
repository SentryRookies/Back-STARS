package com.example.placeservice.dto.culturalevent;

import com.example.placeservice.entity.CulturalEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CulturalEventDto {

    private Long event_id;
    private String category;
    private String title;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String target;
    private String event_fee;
    private String event_img;
    private LocalDateTime start_date;
    private LocalDateTime end_date;

    public CulturalEventDto(CulturalEvent culturalEvent) {
        this.event_id = culturalEvent.getEventId();
        this.category = culturalEvent.getCategory();
        this.title = culturalEvent.getTitle();
        this.address = culturalEvent.getAddress();
        this.lat = culturalEvent.getLat();
        this.lon = culturalEvent.getLon();
        this.target = culturalEvent.getTarget();
        this.event_fee = culturalEvent.getEventFee();
        this.event_img = culturalEvent.getEventImg();
        this.start_date = culturalEvent.getStartDate();
        this.end_date = culturalEvent.getEndDate();

    }

}
