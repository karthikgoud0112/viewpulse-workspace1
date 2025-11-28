package com.viewpulse.controller;

import com.viewpulse.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = "*")
public class WhatsAppController {
    
    @Autowired
    private WhatsAppService whatsAppService;
    
    // Send WhatsApp message
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            Integer feedbackId = (Integer) request.get("feedback_id");
            String customerPhone = (String) request.get("customer_phone");
            String emotion = (String) request.get("emotion");
            String feedbackText = (String) request.get("feedback_text");
            
            Map<String, Object> response = whatsAppService.sendWhatsAppMessage(
                feedbackId, customerPhone, emotion, feedbackText
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error sending WhatsApp: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get WhatsApp messages for a location
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Map<String, Object>> getMessagesForLocation(@PathVariable Integer locationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> messages = whatsAppService.getMessagesForLocation(locationId);
            response.put("success", true);
            response.put("messages", messages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching messages: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get message statistics for location
    @GetMapping("/location/{locationId}/stats")
    public ResponseEntity<Map<String, Object>> getMessageStats(@PathVariable Integer locationId) {
        try {
            Map<String, Object> stats = whatsAppService.getMessageStats(locationId);
            stats.put("success", true);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching stats: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Webhook for Twilio status updates
    @PostMapping("/webhook/status")
    public ResponseEntity<Map<String, Object>> handleStatusWebhook(@RequestBody Map<String, String> payload) {
        try {
            String twilioSid = payload.get("MessageSid");
            String status = payload.get("MessageStatus");
            
            Map<String, Object> response = whatsAppService.updateMessageStatus(twilioSid, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing webhook: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Test endpoint to check WhatsApp service
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testService() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "WhatsApp service is running");
        response.put("service", "Twilio WhatsApp Integration");
        return ResponseEntity.ok(response);
    }
}
