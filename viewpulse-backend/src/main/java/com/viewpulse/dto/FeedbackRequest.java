package com.viewpulse.dto;

public class FeedbackRequest {
    private Integer locationId;
    private String customerPhone;
    private String feedbackText;
    
    // Constructors
    public FeedbackRequest() {}
    
    public FeedbackRequest(Integer locationId, String customerPhone, String feedbackText) {
        this.locationId = locationId;
        this.customerPhone = customerPhone;
        this.feedbackText = feedbackText;
    }
    
    // Getters and Setters
    public Integer getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getFeedbackText() {
        return feedbackText;
    }
    
    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }
}
