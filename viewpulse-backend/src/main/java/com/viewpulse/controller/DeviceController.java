package com.viewpulse.controller;

import com.viewpulse.dto.DeviceLoginRequest;
import com.viewpulse.model.Device;
import com.viewpulse.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/device")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody DeviceLoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Find device by code
        Optional<Device> deviceOpt = deviceRepository.findByDeviceCode(request.getDevice_code());
        
        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            
            // Check password
            if (device.getDevicePassword().equals(request.getDevice_password())) {
                // Update login status
                device.setIsLoggedIn(true);
                deviceRepository.save(device);
                
                response.put("success", true);
                response.put("message", "Device login successful");
                response.put("device_id", device.getDeviceId());
                response.put("location_id", device.getLocationId());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }
        } else {
            response.put("success", false);
            response.put("message", "Device not found");
            return ResponseEntity.status(404).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String deviceCode = request.get("device_code");
        
        // Find device by code
        Optional<Device> deviceOpt = deviceRepository.findByDeviceCode(deviceCode);
        
        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            
            // Update login status
            device.setIsLoggedIn(false);
            deviceRepository.save(device);
            
            response.put("success", true);
            response.put("message", "Device logout successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Device not found");
            return ResponseEntity.status(404).body(response);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllDevices() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Device> devices = deviceRepository.findAll();
            response.put("success", true);
            response.put("devices", devices);
            response.put("count", devices.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching devices: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // FIX: Updated parameter to Integer to match DB and Model
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Map<String, Object>> getDevicesByLocation(@PathVariable Integer locationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Device> devices = deviceRepository.findByLocationId(locationId);
            response.put("success", true);
            response.put("devices", devices);
            response.put("count", devices.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching devices: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/online")
    public ResponseEntity<Map<String, Object>> getOnlineDevices() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Device> devices = deviceRepository.findByIsLoggedInTrue();
            response.put("success", true);
            response.put("devices", devices);
            response.put("count", devices.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching online devices: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addDevice(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String deviceCode = (String) request.get("deviceCode");
            String devicePassword = (String) request.get("devicePassword");
            
            // FIX: Cast/Parse to Integer
            Integer locationId = Integer.parseInt(request.get("locationId").toString());

            Device device = new Device();
            device.setDeviceCode(deviceCode);
            device.setDevicePassword(devicePassword);
            device.setLocationId(locationId);
            device.setIsLoggedIn(false);

            Device saved = deviceRepository.save(device);
            response.put("success", true);
            response.put("device", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "device");
        return ResponseEntity.ok(response);
    }
}