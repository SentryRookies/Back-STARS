package com.example.userservice.repository.place;

import com.example.userservice.entity.place.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    boolean existsById(Long id);
}