package com.example.placeservice.dto.attraction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "NewDataSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttractionDto {

    @XmlElement(name = "Table1")
    private List<AttractionTable> attractions;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AttractionTable {

        @XmlElement(name = "COT_ART_ID")
        private String id;

        @XmlElement(name = "TITLE")
        private String title;

        @XmlElement(name = "COT_ADDR_NEW")
        private String newAddress;

        @XmlElement(name = "COT_MAP_POINTX")
        private String mapX;

        @XmlElement(name = "COT_MAP_POINTY")
        private String mapY;

        @XmlElement(name = "COT_TEL", nillable = true)
        private String tel;

        @XmlElement(name = "COT_HOMEPAGE", nillable = true)
        private String homepage;

        @XmlElement(name = "COT_CLOSE_DAY", nillable = true)
        private String closeDay;

        @XmlElement(name = "COT_USE_TIME_DESC", nillable = true)
        private String useTime;

        // 필요하면 나머지도 추가
    }

}
