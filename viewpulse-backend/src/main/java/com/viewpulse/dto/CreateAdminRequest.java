package com.viewpulse.dto;

public class CreateAdminRequest {
    private String username;
    private String password;
    private String role;
    private Integer locationId;
    private Integer systemAdminId; // For assigning owner to system admin
    
    // Constructors
    public CreateAdminRequest() {}
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Integer getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    
    public Integer getSystemAdminId() {
        return systemAdminId;
    }
    
    public void setSystemAdminId(Integer systemAdminId) {
        this.systemAdminId = systemAdminId;
    }
}
