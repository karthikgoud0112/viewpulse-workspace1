package com.viewpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_admin_owners")
public class SystemAdminOwner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "system_admin_id", nullable = false)
    private Integer systemAdminId;
    
    @Column(name = "owner_id", nullable = false, unique = true)
    private Integer ownerId;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    // Constructors
    public SystemAdminOwner() {
        this.assignedAt = LocalDateTime.now();
    }
    
    public SystemAdminOwner(Integer systemAdminId, Integer ownerId) {
        this.systemAdminId = systemAdminId;
        this.ownerId = ownerId;
        this.assignedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getSystemAdminId() {
        return systemAdminId;
    }
    
    public void setSystemAdminId(Integer systemAdminId) {
        this.systemAdminId = systemAdminId;
    }
    
    public Integer getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
