package com.example.userservice.repository.place;

import com.example.userservice.entity.place.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    boolean existsById(Long id);
}
