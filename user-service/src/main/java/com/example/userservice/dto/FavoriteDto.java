package com.example.userservice.dto;

import com.example.userservice.entity.Favorite;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class FavoriteDto {
    private Long favorite_id;
    private String type;
    private String place_id;
    private String user_id;

    public FavoriteDto(Long favorite_id ,String type, String placeId, String userId) {
        this.favorite_id = favorite_id;
        this.type = type;
        this.place_id = placeId;
        this.user_id = userId;
    }

}
