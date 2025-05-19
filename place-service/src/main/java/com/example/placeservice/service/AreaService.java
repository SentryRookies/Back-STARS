package com.example.placeservice.service;

import com.example.placeservice.dto.AreaDto;
import com.example.placeservice.entity.Area;
import com.example.placeservice.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AreaService {
    private final AreaRepository areaRepository;


    public List<AreaDto> getAreaData() {
        try{
            List<Area> areas = areaRepository.findAll();

            return areas.stream()
                    .map(AreaDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }
}
