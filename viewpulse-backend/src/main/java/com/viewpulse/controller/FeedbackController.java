package com.viewpulse.controller;

import com.viewpulse.model.Feedback;
import com.viewpulse.repository.FeedbackRepository;
import com.viewpulse.service.EmotionService;
import com.viewpulse.service.FeedbackService;
import com.viewpulse.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private EmotionService emotionService;
    
    @Autowired
    private WhatsAppService whatsAppService;
    
    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "feedback");
        return ResponseEntity.ok(response);
    }
    
    // Submit feedback (Updated to handle Device ID)
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer locationId = (Integer) request.get("locationId");
            String customerPhone = (String) request.get("customerPhone");
            String feedbackText = (String) request.get("feedbackText");
            
            // Handle Device ID safely
            Long deviceId = null;
            if (request.get("deviceId") != null) {
                deviceId = Long.valueOf(request.get("deviceId").toString());
            }
            
            // Detect emotion using AI service
            Map<String, Object> emotionResult = emotionService.detectEmotion(feedbackText);
            String emotion = (String) emotionResult.get("emotion");
            Double confidence = (Double) emotionResult.get("confidence");
            
            // Save feedback
            Feedback feedback = new Feedback();
            feedback.setLocationId(locationId);
            feedback.setDeviceId(deviceId); 
            feedback.setCustomerPhone(customerPhone);
            feedback.setFeedbackText(feedbackText);
            feedback.setDetectedEmotion(emotion);
            feedback.setEmotionConfidence(confidence);
            
            Feedback savedFeedback = feedbackRepository.save(feedback);
            
            // Send WhatsApp message if phone number provided
            boolean whatsappSent = false;
            if (customerPhone != null && !customerPhone.trim().isEmpty()) {
                try {
                    whatsAppService.sendWhatsAppMessage(
                        savedFeedback.getFeedbackId(),
                        customerPhone,
                        emotion,
                        feedbackText
                    );
                    whatsappSent = true;
                } catch (Exception ignore) {}
            }
            
            response.put("success", true);
            response.put("message", "Feedback submitted successfully");
            response.put("feedback_id", savedFeedback.getFeedbackId());
            response.put("emotion", emotion);
            response.put("confidence", confidence);
            response.put("whatsapp_sent", whatsappSent);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error submitting feedback: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // NEW: Get feedback specific to a device
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getFeedbackByDevice(@PathVariable Long deviceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackRepository.findByDeviceId(deviceId);
            response.put("success", true);
            response.put("feedbacks", feedbacks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching device feedbacks: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get all feedbacks
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllFeedback() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            response.put("success", true);
            response.put("feedbacks", feedbacks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get feedbacks by location
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Map<String, Object>> getFeedbackByLocation(@PathVariable Integer locationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackByLocation(locationId);
            response.put("success", true);
            response.put("feedbacks", feedbacks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get feedback statistics by location
    @GetMapping("/location/{locationId}/stats")
    public ResponseEntity<Map<String, Object>> getFeedbackStats(@PathVariable Integer locationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackByLocation(locationId);
            
            // Simplified stat calculation (matching previous pattern)
            int positive = 0, negative = 0, neutral = 0;
            
            for (Feedback f : feedbacks) {
                String em = f.getDetectedEmotion();
                if (em != null) {
                    if (em.equals("joy") || em.equals("love") || em.equals("surprise")) positive++;
                    else if (em.equals("sadness") || em.equals("anger")) negative++;
                    else neutral++;
                }
            }
            
            response.put("success", true);
            response.put("total", feedbacks.size());
            response.put("positive", positive);
            response.put("negative", negative);
            response.put("neutral", neutral);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}