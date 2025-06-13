package com.example.placeservice.repository;

import com.example.placeservice.entity.Accommodation;
import com.example.placeservice.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    boolean existsBySeoulAttractionId(String seoul_attraction_id);
    Optional<Attraction> findByAttractionId(Long attraction_id);

    List<Attraction> findByNameContainingIgnoreCase(String keyword);

    List<Attraction> findByAddressContainingIgnoreCase(String keyword);

    // 지역 ID로 관광지 목록 조회
    @Query("SELECT a FROM Attraction a WHERE a.area.areaId = :areaId")
    List<Attraction> findByAreaAreaId(@Param("areaId") Long areaId);
}
