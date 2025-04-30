// CulturalEventRepository.java
package com.example.placeservice.repository;

import com.example.placeservice.entity.CulturalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {
    boolean existsByTitleAndAddressAndStartDate(String title, String address, LocalDateTime startDate);

    // 지역 ID로 문화 행사 목록 조회
    @Query("SELECT e FROM CulturalEvent e WHERE e.area.areaId = :areaId")
    List<CulturalEvent> findByAreaAreaId(@Param("areaId") Long areaId);
}
