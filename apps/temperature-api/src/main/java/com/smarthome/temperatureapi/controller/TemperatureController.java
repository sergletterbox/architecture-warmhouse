package com.smarthome.temperatureapi.controller;

import com.smarthome.temperatureapi.model.TemperatureResponse;
import com.smarthome.temperatureapi.service.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TemperatureController {
    private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);

    @Autowired
    private TemperatureService temperatureService;

    @GetMapping("/temperature")
    public ResponseEntity<?> getTemperature(@RequestParam String location) {
        log.info("getTemperature {}", location);

        if (location == null || location.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Location parameter is required",
                            "message", "Please provide a valid location parameter"
                    ));
        }

        if (!temperatureService.isValidLocation(location)) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Invalid location",
                            "message", "Valid locations: LIVING_ROOM, BEDROOM, KITCHEN",
                            "provided", location
                    ));
        }

        TemperatureResponse reading = temperatureService.getTemperatureReading(location);
        return ResponseEntity.ok(reading);
    }

    @GetMapping("/temperature/{sensorId}")
    public ResponseEntity<?> getTemperatureBySensorId(@PathVariable int sensorId) {
        log.info("getTemperatureBySensorId {}", sensorId);
        String location;
        switch (sensorId) {
            case 1:
                location = "Living Room";
                break;
            case 2:
                location = "Bedroom";
                break;
            case 3:
                location = "Kitchen";
                break;
            default:
                location = "Unknown";
        }

        TemperatureResponse reading = temperatureService.getTemperatureReading(location);
        return ResponseEntity.ok(reading);
    }


    @GetMapping("actuator/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Temperature API",
                "version", "1.0.0"
        ));
    }
}
