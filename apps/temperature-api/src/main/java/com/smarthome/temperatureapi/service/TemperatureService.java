package com.smarthome.temperatureapi.service;

import com.smarthome.temperatureapi.model.TemperatureResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
public class TemperatureService {

    private final Random random = new Random();

    private final Map<String, Double> baseTemperatures = Map.of(
            "LIVING_ROOM", 22.0,
            "BEDROOM", 20.0,
            "KITCHEN", 24.0,
            "UNKNOWN", 20.0
    );

    public TemperatureResponse getTemperatureReading(String location) {
        String normalizedLocation = location.toUpperCase();

        double baseTemp = baseTemperatures.getOrDefault(normalizedLocation, 20.0);

        double variation = (random.nextDouble() - 0.5) * 6.0;
        double temperature = (baseTemp + variation);

        return new TemperatureResponse(location, temperature, "°C");
    }

    public boolean isValidLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return false;
        }
        return baseTemperatures.containsKey(location.toUpperCase());
    }
}