package org.example.showcase.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DisruptionService {
    private final Map<String, Integer> disruptionMap = new HashMap<>();

    public void disrupt(String name) {
        disrupt(name, null);
    }

    public <T> T disrupt(String name, T returnValue) {
        var disruptionCount = disruptionMap.merge(name, 1, Integer::sum);
        if (disruptionCount <= 10) {
            throw new RuntimeException("Disrupting %s for the %d times".formatted(name, disruptionCount));
        }
        return returnValue;
    }
}
