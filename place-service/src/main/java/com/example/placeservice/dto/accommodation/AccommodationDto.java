package com.example.placeservice.dto.accommodation;

import com.example.placeservice.entity.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDto {

    private Long accommodationId;
    private Long areaId;
    private String areaName;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String phone;
    private String gu;
    private String type;
    private String kakaoUrl;

    /**
     * Accommodation 엔티티를 AccommodationDto로 변환하는 정적 팩토리 메소드
     * @param entity 변환할 Accommodation 엔티티
     * @return 변환된 AccommodationDto 객체
     */
    public static AccommodationDto fromEntity(Accommodation entity) {
        if (entity == null) {
            return null;
        }

        // Area 정보 추출 (N+1 문제를 방지하기 위해 Fetch Join 고려)
        Long areaIdValue = null;
        String areaNameValue = null;
        if (entity.getArea() != null) {
            areaIdValue = entity.getArea().getAreaId();
            areaNameValue = entity.getArea().getName(); // Area 엔티티에 getName() 메소드가 있다고 가정
        }

        return new AccommodationDto(
                entity.getAccommodationId(),
                areaIdValue,
                areaNameValue,
                entity.getName(),
                entity.getAddress(),
                entity.getLat(),
                entity.getLon(),
                entity.getPhone(),
                entity.getGu(),
                entity.getType(),
                entity.getKakaoUrl()
        );
    }
}