package com.example.congestionservice.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class CongestionPreviousCache {
    private final Map<String, String> previousLevels = new HashMap<>();

    public void updateLevel(String area, String level) {
        previousLevels.put(area, level);
    }

    public void clear() {
        previousLevels.clear();
    }

}
