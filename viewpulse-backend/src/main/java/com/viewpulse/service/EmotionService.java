package com.viewpulse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmotionService {
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public EmotionService() {
        this.restTemplate = new RestTemplate();
    }
    
    public Map<String, Object> detectEmotion(String text) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = aiServiceUrl + "/analyze";
            
            System.out.println("🤖 Calling AI service: " + url);
            
            // Prepare request
            Map<String, String> request = new HashMap<>();
            request.put("text", text);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            // Call AI service (FIXED: Parameterized Map)
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(url, entity, (Class<Map<String, Object>>)(Class<?>) Map.class);
            
            if (response.getBody() != null && (Boolean) response.getBody().get("success")) {
                String emotion = (String) response.getBody().get("emotion");
                Object confidenceObj = response.getBody().get("confidence");
                
                Double confidence;
                if (confidenceObj instanceof Integer) {
                    confidence = ((Integer) confidenceObj).doubleValue();
                } else if (confidenceObj instanceof Double) {
                    confidence = (Double) confidenceObj;
                } else {
                    confidence = 0.5;
                }
                
                result.put("emotion", emotion);
                result.put("confidence", confidence);
                
                System.out.println("✅ AI Response: " + emotion + " (" + String.format("%.1f", confidence * 100) + "%)");
            } else {
                System.err.println("⚠️ AI service returned unsuccessful response");
                result.put("emotion", "joy");
                result.put("confidence", 0.5);
            }
            
        } catch (Exception e) {
            System.err.println("❌ AI Service error: " + e.getMessage());
            e.printStackTrace();
            result.put("emotion", "joy");
            result.put("confidence", 0.5);
        }
        
        return result;
    }
}