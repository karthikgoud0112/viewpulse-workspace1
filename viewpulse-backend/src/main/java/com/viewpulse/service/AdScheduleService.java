package com.viewpulse.service;

import com.viewpulse.model.AdSchedule;
import com.viewpulse.model.Video;
import com.viewpulse.repository.AdScheduleRepository;
import com.viewpulse.repository.DeviceRepository;
import com.viewpulse.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdScheduleService {
    
    @Autowired
    private AdScheduleRepository adScheduleRepository;
    
    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    // Create ad schedule
    @Transactional
    public Map<String, Object> createSchedule(Long videoId, Long deviceId, String dayOfWeek,
                                                String startTime, String endTime, Integer priority) {
        Map<String, Object> response = new HashMap<>();
        
     // --- CRITICAL FIX: Ensure IDs are NOT NULL before checking existence ---
        if (videoId == null) {
            response.put("success", false);
            response.put("message", "Error: Video ID cannot be null.");
            return response;
        }
        if (deviceId == null) {
            response.put("success", false);
            response.put("message", "Error: Device ID cannot be null.");
            return response;
        }
        // ----------------------------------------------------------------------
        
        try {
            // 1. VALIDATION: Check if Video exists
            if (!videoRepository.existsById(videoId)) {
                response.put("success", false);
                response.put("message", "Error: Video ID " + videoId + " does not exist.");
                return response;
            }

            // 2. VALIDATION: Check if Device exists
            if (!deviceRepository.existsById(deviceId)) {
                response.put("success", false);
                response.put("message", "Error: Device ID " + deviceId + " does not exist.");
                return response;
            }

            // 3. Create and Save
            AdSchedule schedule = new AdSchedule();
            schedule.setVideoId(videoId);
            schedule.setDeviceId(deviceId);
            schedule.setDayOfWeek(dayOfWeek != null ? dayOfWeek.toLowerCase() : "all");
            
            // Handle Time Format (Supports HH:mm or HH:mm:ss)
            if (startTime.length() == 5) startTime += ":00";
            if (endTime.length() == 5) endTime += ":00";
            
            schedule.setStartTime(LocalTime.parse(startTime));
            schedule.setEndTime(LocalTime.parse(endTime));
            schedule.setPriority(priority != null ? priority : 1);
            schedule.setIsActive(true);
            
            AdSchedule saved = adScheduleRepository.save(schedule);
            
            response.put("success", true);
            response.put("message", "Schedule created successfully");
            response.put("schedule_id", saved.getScheduleId());
            
        } catch (Exception e) {
            // Log the actual error to the Eclipse console so we can see it
            e.printStackTrace(); 
            response.put("success", false);
            response.put("message", "Error creating schedule: " + e.getMessage());
        }
        
        return response;
    }
    
    // Get all schedules for a device
    public List<Map<String, Object>> getSchedulesForDevice(Long deviceId) {
        List<AdSchedule> schedules = adScheduleRepository.findByDeviceId(deviceId);
        
        return schedules.stream().map(schedule -> {
            Map<String, Object> map = new HashMap<>();
            map.put("schedule_id", schedule.getScheduleId());
            map.put("video_id", schedule.getVideoId());
            map.put("device_id", schedule.getDeviceId());
            map.put("day_of_week", schedule.getDayOfWeek());
            map.put("start_time", schedule.getStartTime().toString());
            map.put("end_time", schedule.getEndTime().toString());
            map.put("is_active", schedule.getIsActive());
            map.put("priority", schedule.getPriority());
            map.put("created_at", schedule.getCreatedAt());
            
            Optional<Video> videoOpt = videoRepository.findById(schedule.getVideoId());
            if (videoOpt.isPresent()) {
                Video video = videoOpt.get();
                map.put("video_title", video.getVideoTitle());
                map.put("video_path", video.getVideoPath());
                map.put("duration", video.getDuration());
            } else {
                map.put("video_title", "Unknown Video");
            }
            
            return map;
        }).collect(Collectors.toList());
    }
    
    // Get active videos for device based on current time
    public List<Map<String, Object>> getActiveVideosForDevice(Long deviceId, String dayOfWeek, LocalTime currentTime) {
        List<AdSchedule> schedules = adScheduleRepository.findActiveSchedulesForDeviceAndTime(
            deviceId, dayOfWeek.toLowerCase(), currentTime
        );
        
        return schedules.stream().map(schedule -> {
            Map<String, Object> map = new HashMap<>();
            Optional<Video> videoOpt = videoRepository.findById(schedule.getVideoId());
            if (videoOpt.isPresent()) {
                Video video = videoOpt.get();
                map.put("video_id", video.getVideoId());
                map.put("video_title", video.getVideoTitle());
                map.put("video_path", video.getVideoPath());
                map.put("duration", video.getDuration());
                map.put("sequence_order", video.getSequenceOrder());
                map.put("priority", schedule.getPriority());
                return map;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    // Update schedule
    @Transactional
    public Map<String, Object> updateSchedule(Long scheduleId, String dayOfWeek, 
                                                String startTime, String endTime, 
                                                Integer priority, Boolean isActive) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<AdSchedule> scheduleOpt = adScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Schedule not found");
            return response;
        }
        
        try {
            AdSchedule schedule = scheduleOpt.get();
            
            if (dayOfWeek != null) schedule.setDayOfWeek(dayOfWeek.toLowerCase());
            if (startTime != null) schedule.setStartTime(LocalTime.parse(startTime));
            if (endTime != null) schedule.setEndTime(LocalTime.parse(endTime));
            if (priority != null) schedule.setPriority(priority);
            if (isActive != null) schedule.setIsActive(isActive);
            
            adScheduleRepository.save(schedule);
            
            response.put("success", true);
            response.put("message", "Schedule updated successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating schedule: " + e.getMessage());
        }
        
        return response;
    }
    
    // Delete schedule
    @Transactional
    public Map<String, Object> deleteSchedule(Long scheduleId) {
        Map<String, Object> response = new HashMap<>();
        
        if (!adScheduleRepository.existsById(scheduleId)) {
            response.put("success", false);
            response.put("message", "Schedule not found");
            return response;
        }
        
        adScheduleRepository.deleteById(scheduleId);
        
        response.put("success", true);
        response.put("message", "Schedule deleted successfully");
        
        return response;
    }
}