package com.example.placeservice.repository;

import com.example.placeservice.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    boolean existsByVisitId(String visitId);
    Optional<Attraction> findById(Long id);
}
