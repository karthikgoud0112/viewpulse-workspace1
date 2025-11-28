package com.viewpulse.controller;

import com.viewpulse.model.Location;
import com.viewpulse.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {
    
    @Autowired
    private LocationRepository locationRepository;
    
    // ADDED: Endpoint to create a new location
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLocation(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String locationName = request.get("location_name");
            String address = request.get("address");

            if (locationName == null || locationName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Location name is required.");
                return ResponseEntity.badRequest().body(response);
            }

            Location location = new Location(locationName, address);
            Location saved = locationRepository.save(location);

            response.put("success", true);
            response.put("message", "Location created successfully");
            response.put("location", saved); // Return the saved object for auto-selection in frontend
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating location: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllLocations() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Location> locations = locationRepository.findAll();
            response.put("success", true);
            response.put("locations", locations);
            response.put("count", locations.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching locations: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/{locationId}")
    public ResponseEntity<Map<String, Object>> getLocationById(@PathVariable Integer locationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Location location = locationRepository.findById(locationId).orElse(null);
            if (location != null) {
                response.put("success", true);
                response.put("location", location);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Location not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching location: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "location");
        return ResponseEntity.ok(response);
    }
}