package com.example.placeservice.repository;

import com.example.placeservice.entity.Accommodation;
import com.example.placeservice.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // 기본 CRUD 사용 (findAll, findById, save 등)
    List<Restaurant> findByNameContainingIgnoreCase(String keyword);

    List<Restaurant> findByAddressContainingIgnoreCase(String keyword);
}