// CulturalEventRepository.java
package com.example.placeservice.repository;

import com.example.placeservice.entity.Accommodation;
import com.example.placeservice.entity.CulturalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {
    boolean existsByTitleAndAddressAndStartDate(String title, String address, LocalDateTime startDate);

    List<CulturalEvent> findBytitleContainingIgnoreCase(String keyword);
    List<CulturalEvent> findByAddressContainingIgnoreCase(String keyword);
}
