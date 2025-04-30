package com.example.placeservice.repository;

import com.example.placeservice.entity.Accommodation;
import com.example.placeservice.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // 기본 CRUD 사용 (findAll, findById, save 등)
    List<Restaurant> findByNameContainingIgnoreCase(String keyword);


    List<Restaurant> findByAddressContainingIgnoreCase(String keyword);

    // 지역 ID로 음식점 목록 조회
    @Query("SELECT r FROM Restaurant r WHERE r.area.areaId = :areaId")
    List<Restaurant> findByAreaAreaId(@Param("areaId") Long areaId);

}