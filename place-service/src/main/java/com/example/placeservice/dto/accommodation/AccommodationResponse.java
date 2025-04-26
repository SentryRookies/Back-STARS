package com.example.placeservice.dto.accommodation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccommodationResponse {
    @XmlElement(name = "header")
    private Header header;

    @XmlElement(name = "body")
    private Body body;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        @XmlElement(name = "resultCode")
        private String resultCode;

        @XmlElement(name = "resultMsg")
        private String resultMsg;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "items")
        private Items items;

        @XmlElement(name = "numOfRows")
        private int numOfRows;

        @XmlElement(name = "pageNo")
        private int pageNo;

        @XmlElement(name = "totalCount")
        private int totalCount;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Items {
            @XmlElement(name = "item")
            private List<Item> item; // 여기를 List<Item>으로 변경

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Item {
                private String addr1;
                private String addr2;
                private String areacode;
                private String benikia;
                private String cat1;
                private String cat2;
                private String cat3;
                private String contentid;
                private String contenttypeid;
                private String createdtime;
                private String firstimage;
                private String firstimage2;
                private String cpyrhtDivCd;
                private String goodstay;
                private String hanok;
                private String mapx;
                private String mapy;
                private String mlevel;
                private String modifiedtime;
                private String tel;
                private String title;
                private String booktour;
                private String sigungucode;
            }
        }
    }
}