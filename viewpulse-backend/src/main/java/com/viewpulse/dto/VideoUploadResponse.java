package com.viewpulse.dto;

public class VideoUploadResponse {
    
    private boolean success;
    private String message;
    private Long videoId;
    private String filePath;
    
    // Constructors
    public VideoUploadResponse() {}
    
    public VideoUploadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public VideoUploadResponse(boolean success, String message, Long videoId) {
        this.success = success;
        this.message = message;
        this.videoId = videoId;
    }
    
    public VideoUploadResponse(boolean success, String message, Long videoId, String filePath) {
        this.success = success;
        this.message = message;
        this.videoId = videoId;
        this.filePath = filePath;
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
    
    public Long getVideoId() {
        return videoId;
    }
    
    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

