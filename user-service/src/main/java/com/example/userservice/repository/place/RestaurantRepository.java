package com.example.userservice.repository.place;

import com.example.userservice.entity.place.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsById(Long id);

}