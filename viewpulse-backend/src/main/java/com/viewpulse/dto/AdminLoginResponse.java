package com.viewpulse.dto;

public class AdminLoginResponse {
    
    private boolean success;
    private String message;
    private Long adminId;
    private String username;
    private String role;
    private Long locationId;
    
    // Constructors
    public AdminLoginResponse() {}
    
    public AdminLoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public AdminLoginResponse(boolean success, String message, Long adminId, String username, String role, Long locationId) {
        this.success = success;
        this.message = message;
        this.adminId = adminId;
        this.username = username;
        this.role = role;
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
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Long getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
