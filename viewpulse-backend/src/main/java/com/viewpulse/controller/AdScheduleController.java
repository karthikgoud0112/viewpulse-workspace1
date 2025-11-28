package com.viewpulse.controller;

import com.viewpulse.dto.AdScheduleRequest;
import com.viewpulse.service.AdScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ad-schedule")
@CrossOrigin(origins = "*")
public class AdScheduleController {
    
    @Autowired
    private AdScheduleService adScheduleService;
    
    // Create ad schedule
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createSchedule(@RequestBody AdScheduleRequest request) {
        Map<String, Object> response = adScheduleService.createSchedule(
            request.getVideoId(),
            request.getDeviceId(),
            request.getDayOfWeek(),
            request.getStartTime(),
            request.getEndTime(),
            request.getPriority()
        );
        return ResponseEntity.ok(response);
    }
    
    // Get schedules for device
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getSchedulesForDevice(@PathVariable Long deviceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> schedules = adScheduleService.getSchedulesForDevice(deviceId);
            response.put("success", true);
            response.put("schedules", schedules);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching schedules: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get active videos for device (used by display app)
    @GetMapping("/device/{deviceId}/active")
    public ResponseEntity<Map<String, Object>> getActiveVideos(@PathVariable Long deviceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Get current day and time
            String dayOfWeek = DayOfWeek.from(java.time.LocalDate.now()).name().toLowerCase();
            LocalTime currentTime = LocalTime.now();
            
            List<Map<String, Object>> videos = adScheduleService.getActiveVideosForDevice(
                deviceId, dayOfWeek, currentTime
            );
            
            response.put("success", true);
            response.put("videos", videos);
            response.put("current_time", currentTime.toString());
            response.put("current_day", dayOfWeek);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching active videos: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Update schedule
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Map<String, Object>> updateSchedule(
        @PathVariable Long scheduleId,
        @RequestBody Map<String, Object> updates
    ) {
        Map<String, Object> response = adScheduleService.updateSchedule(
            scheduleId,
            (String) updates.get("day_of_week"),
            (String) updates.get("start_time"),
            (String) updates.get("end_time"),
            (Integer) updates.get("priority"),
            (Boolean) updates.get("is_active")
        );
        return ResponseEntity.ok(response);
    }
    
    // Delete schedule
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Map<String, Object>> deleteSchedule(@PathVariable Long scheduleId) {
        Map<String, Object> response = adScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(response);
    }
}
