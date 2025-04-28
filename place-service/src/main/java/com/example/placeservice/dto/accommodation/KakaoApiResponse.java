package com.example.placeservice.dto.accommodation; // 적절한 패키지 경로로 변경

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // API 응답에 모르는 필드가 있어도 무시
public class KakaoApiResponse {
    private Meta meta;
    private List<Document> documents;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;
        @JsonProperty("pageable_count")
        private Integer pageableCount;
        @JsonProperty("is_end")
        private Boolean isEnd;
        // 필요시 다른 meta 필드 추가
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        private String id; // 카카오에서 부여하는 장소 ID (String이지만 숫자 형태)
        @JsonProperty("place_name")
        private String placeName; // 장소명, 업체명
        @JsonProperty("category_name")
        private String categoryName; // 카테고리 이름 (예: 여행 > 숙박 > 호텔)
        @JsonProperty("category_group_code")
        private String categoryGroupCode; // 카테고리 그룹 코드
        @JsonProperty("category_group_name")
        private String categoryGroupName; // 카테고리 그룹명
        private String phone; // 전화번호
        @JsonProperty("address_name")
        private String addressName; // 전체 지번 주소
        @JsonProperty("road_address_name")
        private String roadAddressName; // 전체 도로명 주소
        private String x; // 경도(longitude) - String 형태
        private String y; // 위도(latitude) - String 형태
        @JsonProperty("place_url")
        private String placeUrl; // 장소 상세페이지 URL
        @JsonProperty("distance")
        private String distance; // 중심좌표와의 거리 (단위: m) - radius 파라미터 사용 시
    }
}
