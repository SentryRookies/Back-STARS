package com.example.placeservice.repository;

import com.example.placeservice.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    boolean existsByAccommodationId(Long accommodationId);

    List<Accommodation> findByGu(String gu);

    List<Accommodation> findByType(String type);

    @Query(value = "SELECT * FROM Accommodation WHERE area_id = ?1", nativeQuery = true)
    List<Accommodation> findByAreaId(Long areaId);
}
