package com.example.userservice.dto;

import lombok.Data;

@Data
public class FavoriteDto {
    private Long favorite_id;
    private String type;
    private String place_id;
    private String user_id;
    private String name;
    private String address;

    public FavoriteDto(Long favorite_id ,String type, String placeId, String userId, String name, String address) {
        this.favorite_id = favorite_id;
        this.type = type;
        this.place_id = placeId;
        this.user_id = userId;
        this.name = name;
        this.address = address;
    }


}
