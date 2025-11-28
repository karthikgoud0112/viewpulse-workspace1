package com.viewpulse.dto;

import java.util.List;
import java.util.Map;

public class HierarchyResponse {
    private boolean success;
    private String message;
    private List<Map<String, Object>> systemAdmins;
    private List<Map<String, Object>> owners;
    private List<Map<String, Object>> devices;
    
    // Constructors
    public HierarchyResponse() {}
    
    public HierarchyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
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
    
    public List<Map<String, Object>> getSystemAdmins() {
        return systemAdmins;
    }
    
    public void setSystemAdmins(List<Map<String, Object>> systemAdmins) {
        this.systemAdmins = systemAdmins;
    }
    
    public List<Map<String, Object>> getOwners() {
        return owners;
    }
    
    public void setOwners(List<Map<String, Object>> owners) {
        this.owners = owners;
    }
    
    public List<Map<String, Object>> getDevices() {
        return devices;
    }
    
    public void setDevices(List<Map<String, Object>> devices) {
        this.devices = devices;
    }
}
