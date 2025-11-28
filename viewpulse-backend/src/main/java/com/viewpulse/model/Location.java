package com.viewpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "location")
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;
    
    @Column(name = "location_name", nullable = false, length = 255)
    private String locationName;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Location() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Location(String locationName, String address) {
        this.locationName = locationName;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", locationName='" + locationName + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
