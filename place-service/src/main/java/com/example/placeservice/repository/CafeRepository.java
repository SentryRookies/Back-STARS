package com.example.placeservice.repository;

import com.example.placeservice.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    @Query("SELECT c FROM Cafe c WHERE c.area.area_id = :areaId")
    List<Cafe> findByAreaId(@org.springframework.data.repository.query.Param("areaId") Long areaId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cafe c WHERE c.area.area_id = :areaId")
    void deleteByAreaId(Long areaId);
}
