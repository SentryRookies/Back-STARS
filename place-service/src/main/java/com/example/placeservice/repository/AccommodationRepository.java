package com.example.placeservice.repository;

import com.example.placeservice.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    boolean existsByAccommodationId(Long accommodationId);

    List<Accommodation> findByGu(String gu);

    List<Accommodation> findByType(String type);
}
