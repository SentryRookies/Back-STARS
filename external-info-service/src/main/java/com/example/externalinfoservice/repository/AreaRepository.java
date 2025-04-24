package com.example.externalinfoservice.repository;

import com.example.externalinfoservice.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findBySeoulId(String seoulId);
}