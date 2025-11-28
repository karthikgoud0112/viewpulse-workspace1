package com.viewpulse.dto;

public class FeedbackResponse {
    
    private boolean success;
    private String message;
    private Integer feedbackId;
    private String emotion;
    private Double confidence;
    
    // Constructors
    public FeedbackResponse() {}
    
    public FeedbackResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public FeedbackResponse(boolean success, String message, Integer feedbackId, String emotion, Double confidence) {
        this.success = success;
        this.message = message;
        this.feedbackId = feedbackId;
        this.emotion = emotion;
        this.confidence = confidence;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getFeedbackId() {
        return feedbackId;
    }
    
    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }
    
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
