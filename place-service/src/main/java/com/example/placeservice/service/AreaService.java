package com.example.placeservice.service;

import com.example.placeservice.dto.AreaDto;
import com.example.placeservice.entity.Area;
import com.example.placeservice.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AreaService {
    private final AreaRepository areaRepository;


    public List<AreaDto> getAreaData() {
        try{
            List<Area> areas = areaRepository.findAll();
            return areas.stream()
                    .map(area -> new AreaDto(
                            area.getAreaId(),
                            area.getSeoulId(),
                            area.getName(),
                            area.getNameEng(),
                            area.getCategory(),
                            area.getLat(),
                            area.getLon()
                    )).toList();
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }
}
