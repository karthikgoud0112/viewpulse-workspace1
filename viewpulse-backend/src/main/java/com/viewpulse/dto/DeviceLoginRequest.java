package com.viewpulse.dto;

public class DeviceLoginRequest {
    private String device_code;
    private String device_password;
    
    // Constructors
    public DeviceLoginRequest() {}
    
    public DeviceLoginRequest(String device_code, String device_password) {
        this.device_code = device_code;
        this.device_password = device_password;
    }
    
    // Getters and Setters
    public String getDevice_code() {
        return device_code;
    }
    
    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }
    
    public String getDevice_password() {
        return device_password;
    }
    
    public void setDevice_password(String device_password) {
        this.device_password = device_password;
    }
}
