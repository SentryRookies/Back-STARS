package com.example.placeservice.repository;

import com.example.placeservice.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    @Query("SELECT a.name FROM Area a")
    List<String> findAllAreaNames();
}
