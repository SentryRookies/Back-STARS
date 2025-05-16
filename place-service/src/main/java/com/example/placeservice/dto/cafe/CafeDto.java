package com.example.placeservice.dto.cafe;

import com.example.placeservice.entity.Cafe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeDto {
    private Long id;
    private Long areaId;
    private String areaName;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String phone;
    private String kakaomapUrl;
    private String categoryCode;

    /**
     * Cafe 엔티티를 CafeDto로 변환하는 정적 팩토리 메소드
     */
    public static CafeDto fromEntity(Cafe entity) {
        if (entity == null) {
            return null;
        }

        Long areaIdValue = null;
        String areaNameValue = null;
        if (entity.getArea() != null) {
            areaIdValue = entity.getArea().getAreaId();
            areaNameValue = entity.getArea().getName();
        }

        return new CafeDto(
                entity.getId(),
                areaIdValue,
                areaNameValue,
                entity.getName(),
                entity.getAddress(),
                entity.getLat(),
                entity.getLon(),
                entity.getPhone(),
                entity.getKakaomapUrl(),
                entity.getCategoryCode()
        );
    }
}