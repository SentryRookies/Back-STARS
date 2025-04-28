package com.example.placeservice.dto.accommodation;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDto {
    private Long accommodationId;
    private Long areaId;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String phone;
    private String sigunguCode;
    private String imageUrl;
}
