package com.viewpulse.dto;

public class VideoUploadRequest {
    
    private Long locationId;
    private String videoTitle;
    private String videoPath;
    private Integer duration;
    private String uploadedBy;
    private Boolean isActive;
    private Integer sequenceOrder;
    
    // Constructors
    public VideoUploadRequest() {}
    
    public VideoUploadRequest(Long locationId, String videoTitle, String videoPath, Integer duration) {
        this.locationId = locationId;
        this.videoTitle = videoTitle;
        this.videoPath = videoPath;
        this.duration = duration;
        this.isActive = true;
        this.sequenceOrder = 1;
    }
    
    // Getters and Setters
    public Long getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
    
    public String getVideoTitle() {
        return videoTitle;
    }
    
    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
    
    public String getVideoPath() {
        return videoPath;
    }
    
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getSequenceOrder() {
        return sequenceOrder;
    }
    
    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
}
