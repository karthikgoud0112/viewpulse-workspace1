package com.viewpulse.dto;

public class DeviceLoginResponse {
    
    private boolean success;
    private String message;
    private Long deviceId;
    private Long locationId;
    
    // Constructors
    public DeviceLoginResponse() {}
    
    public DeviceLoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public DeviceLoginResponse(boolean success, String message, Long deviceId, Long locationId) {
        this.success = success;
        this.message = message;
        this.deviceId = deviceId;
        this.locationId = locationId;
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
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
