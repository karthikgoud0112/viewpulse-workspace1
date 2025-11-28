package com.viewpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;
    
    @Column(unique = true, nullable = false)
    private String deviceCode;
    
    @Column(nullable = false)
    private String devicePassword;
    
    // FIX: Changed from Long to Integer to match location.location_id type
    @Column(nullable = false)
    private Integer locationId;
    
    @Column(nullable = false)
    private Boolean isLoggedIn = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public Device() {}
    
    // FIX: Constructor parameter type is Integer
    public Device(String deviceCode, String devicePassword, Integer locationId) {
        this.deviceCode = deviceCode;
        this.devicePassword = devicePassword;
        this.locationId = locationId;
    }
    
    // Getters and Setters
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    
    public String getDevicePassword() { return devicePassword; }
    public void setDevicePassword(String devicePassword) { this.devicePassword = devicePassword; }
    
    // FIX: Return/Parameter Type is Integer
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    
    public Boolean getIsLoggedIn() { return isLoggedIn; }
    public void setIsLoggedIn(Boolean isLoggedIn) { this.isLoggedIn = isLoggedIn; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}