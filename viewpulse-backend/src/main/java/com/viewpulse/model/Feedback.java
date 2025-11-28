package com.viewpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;
    
    @Column(name = "location_id", nullable = false)
    private Integer locationId;
    
    @Column(name = "device_id") // Added for per-device tracking
    private Long deviceId;
    
    @Column(name = "customer_phone", nullable = true)
    private String customerPhone;
    
    @Column(name = "feedback_text", nullable = false, columnDefinition = "TEXT")
    private String feedbackText;
    
    @Column(name = "detected_emotion", nullable = true)
    private String detectedEmotion;
    
    @Column(name = "emotion_confidence", nullable = true)
    private Double emotionConfidence;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public Feedback() {}
    
    public Feedback(Integer locationId, String customerPhone, String feedbackText) {
        this.locationId = locationId;
        this.customerPhone = customerPhone;
        this.feedbackText = feedbackText;
    }
    
    // Getters and Setters
    public Integer getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Integer feedbackId) { this.feedbackId = feedbackId; }
    
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    
    public Long getDeviceId() { return deviceId; } // New Getter
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; } // New Setter
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    
    public String getDetectedEmotion() { return detectedEmotion; }
    public void setDetectedEmotion(String detectedEmotion) { this.detectedEmotion = detectedEmotion; }
    
    public Double getEmotionConfidence() { return emotionConfidence; }
    public void setEmotionConfidence(Double emotionConfidence) { this.emotionConfidence = emotionConfidence; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}