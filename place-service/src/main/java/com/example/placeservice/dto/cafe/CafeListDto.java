package com.example.placeservice.dto.cafe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CafeListDto {
    private Long id;
    private String name;
    private BigDecimal lat;
    private BigDecimal lon;
}