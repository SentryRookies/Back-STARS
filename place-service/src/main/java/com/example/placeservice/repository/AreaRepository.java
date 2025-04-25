package com.example.placeservice.repository;

import com.example.placeservice.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

public interface AreaRepository extends JpaRepository<Area, Long> {
=======
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    @Query("SELECT a.name FROM Area a")
    List<String> findAllAreaNames();
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793
}
