package com.example.placeservice.service;

import com.example.placeservice.entity.GuArea;
import com.example.placeservice.repository.GuAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuAreaService {

    private final GuAreaRepository guAreaRepository;

    public GuArea findOrCreateArea(String guName) {
        return guAreaRepository.findByGuName(guName)
                .orElseGet(() -> {
                    GuArea newArea = new GuArea();
                    newArea.setGuName(guName);
                    return guAreaRepository.save(newArea);
                });
    }
}
