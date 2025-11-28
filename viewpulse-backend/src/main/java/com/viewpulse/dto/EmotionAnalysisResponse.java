package com.viewpulse.dto;

public class EmotionAnalysisResponse {
    
    private String emotion;
    private Double confidence;
    
    // Constructors
    public EmotionAnalysisResponse() {}
    
    public EmotionAnalysisResponse(String emotion, Double confidence) {
        this.emotion = emotion;
        this.confidence = confidence;
    }
    
    // Getters and Setters
    public String getEmotion() {
        return emotion;
    }
    
    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
