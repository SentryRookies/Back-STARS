package com.example.userservice.repository.place;

import com.example.userservice.entity.place.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    boolean existsById(Long id);

}
