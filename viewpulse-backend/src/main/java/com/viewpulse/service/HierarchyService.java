package com.viewpulse.service;

import com.viewpulse.model.AdminUser;
import com.viewpulse.model.Device;
import com.viewpulse.model.SystemAdminOwner;
import com.viewpulse.repository.AdminUserRepository;
import com.viewpulse.repository.DeviceRepository;
import com.viewpulse.repository.SystemAdminOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HierarchyService {
    
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
    private SystemAdminOwnerRepository systemAdminOwnerRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    // Get all system admins (for super admin)
    public List<Map<String, Object>> getAllSystemAdmins() {
        List<AdminUser> systemAdmins = adminUserRepository.findByRole("system_admin");
        
        return systemAdmins.stream().map(admin -> {
            Map<String, Object> map = new HashMap<>();
            map.put("admin_id", admin.getAdminId());
            map.put("username", admin.getUsername());
            map.put("role", admin.getRole());
            map.put("is_active", admin.getIsActive());
            map.put("created_at", admin.getCreatedAt());
            
            // Count owners under this system admin
            List<SystemAdminOwner> assignments = systemAdminOwnerRepository.findBySystemAdminId(admin.getAdminId().intValue());
            map.put("owner_count", assignments.size());
            
            return map;
        }).collect(Collectors.toList());
    }
    
    // Get all owners under a specific system admin
    public List<Map<String, Object>> getOwnersUnderSystemAdmin(Integer systemAdminId) {
        List<SystemAdminOwner> assignments = systemAdminOwnerRepository.findBySystemAdminId(systemAdminId);
        
        return assignments.stream().map(assignment -> {
            Optional<AdminUser> ownerOpt = adminUserRepository.findById(assignment.getOwnerId().longValue());
            if (ownerOpt.isPresent()) {
                AdminUser owner = ownerOpt.get();
                Map<String, Object> map = new HashMap<>();
                map.put("admin_id", owner.getAdminId());
                map.put("username", owner.getUsername());
                map.put("role", owner.getRole());
                map.put("location_id", owner.getLocationId());
                map.put("is_active", owner.getIsActive());
                map.put("created_at", owner.getCreatedAt());
                map.put("assigned_at", assignment.getAssignedAt());
                
                // FIX: findByLocationId expects Integer
                if (owner.getLocationId() != null) {
                    List<Device> devices = deviceRepository.findByLocationId(owner.getLocationId());
                    map.put("device_count", devices.size());
                } else {
                    map.put("device_count", 0);
                }
                
                return map;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    // Get all devices under a specific owner
    public List<Map<String, Object>> getDevicesUnderOwner(Integer ownerId) {
        Optional<AdminUser> ownerOpt = adminUserRepository.findById(ownerId.longValue());
        
        if (ownerOpt.isEmpty() || ownerOpt.get().getLocationId() == null) {
            return new ArrayList<>();
        }
        
        // FIX: Variable type is now Integer
        Integer locationId = ownerOpt.get().getLocationId();
        List<Device> devices = deviceRepository.findByLocationId(locationId);
        
        return devices.stream().map(device -> {
            Map<String, Object> map = new HashMap<>();
            map.put("device_id", device.getDeviceId());
            map.put("device_code", device.getDeviceCode());
            map.put("location_id", device.getLocationId());
            map.put("is_logged_in", device.getIsLoggedIn());
            map.put("created_at", device.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
    }
    
    // Create system admin (by super admin)
    @Transactional
    public Map<String, Object> createSystemAdmin(String username, String password, Integer createdBy) {
        Map<String, Object> response = new HashMap<>();
        
        if (adminUserRepository.findByUsername(username).isPresent()) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return response;
        }
        
        AdminUser systemAdmin = new AdminUser();
        systemAdmin.setUsername(username);
        systemAdmin.setPassword(password); 
        systemAdmin.setRole("system_admin");
        systemAdmin.setIsActive(true);
        
        // FIX: Pass Integer directly
        systemAdmin.setCreatedBy(createdBy);
        
        AdminUser saved = adminUserRepository.save(systemAdmin);
        
        response.put("success", true);
        response.put("message", "System admin created successfully");
        response.put("admin_id", saved.getAdminId());
        response.put("username", saved.getUsername());
        
        return response;
    }
    
    // Create owner and assign to system admin
    @Transactional
    public Map<String, Object> createOwner(String username, String password, Integer locationId, 
                                            Integer systemAdminId, Integer createdBy) {
        Map<String, Object> response = new HashMap<>();
        
        if (adminUserRepository.findByUsername(username).isPresent()) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return response;
        }
        
        AdminUser owner = new AdminUser();
        owner.setUsername(username);
        owner.setPassword(password); 
        owner.setRole("location_owner");
        
        // FIX: Pass Integer directly
        owner.setLocationId(locationId);
        owner.setIsActive(true);
        owner.setCreatedBy(createdBy);
        
        AdminUser savedOwner = adminUserRepository.save(owner);
        
        SystemAdminOwner assignment = new SystemAdminOwner(systemAdminId, savedOwner.getAdminId().intValue());
        systemAdminOwnerRepository.save(assignment);
        
        response.put("success", true);
        response.put("message", "Owner created and assigned successfully");
        response.put("admin_id", savedOwner.getAdminId());
        response.put("username", savedOwner.getUsername());
        response.put("location_id", savedOwner.getLocationId());
        
        return response;
    }
    
    // FIX: Method implementation for deletion (required by controller)
    @Transactional
    public Map<String, Object> deleteSystemAdmin(Integer systemAdminId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<AdminUser> adminOpt = adminUserRepository.findById(systemAdminId.longValue());
        if (adminOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "System admin not found");
            return response;
        }
        
        List<SystemAdminOwner> assignments = systemAdminOwnerRepository.findBySystemAdminId(systemAdminId);
        if (!assignments.isEmpty()) {
            response.put("success", false);
            response.put("message", "Cannot delete system admin with assigned owners");
            return response;
        }
        
        adminUserRepository.deleteById(systemAdminId.longValue());
        
        response.put("success", true);
        response.put("message", "System admin deleted successfully");
        
        return response;
    }
    
    // FIX: Method implementation for deletion (required by controller)
    @Transactional
    public Map<String, Object> deleteOwner(Integer ownerId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<AdminUser> ownerOpt = adminUserRepository.findById(ownerId.longValue());
        if (ownerOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Owner not found");
            return response;
        }
        
        // Remove assignment
        systemAdminOwnerRepository.deleteByOwnerId(ownerId);
        
        // Delete owner
        adminUserRepository.deleteById(ownerId.longValue());
        
        response.put("success", true);
        response.put("message", "Owner deleted successfully");
        
        return response;
    }
    
    // FIX: Method implementation for complete hierarchy (required by controller)
    public Map<String, Object> getCompleteHierarchy() {
        Map<String, Object> hierarchy = new HashMap<>();
        
        List<Map<String, Object>> systemAdmins = getAllSystemAdmins();
        
        for (Map<String, Object> systemAdmin : systemAdmins) {
            Long systemAdminIdLong = (Long) systemAdmin.get("admin_id");
            Integer systemAdminId = systemAdminIdLong.intValue();
            List<Map<String, Object>> owners = getOwnersUnderSystemAdmin(systemAdminId);
            
            for (Map<String, Object> owner : owners) {
                Long ownerIdLong = (Long) owner.get("admin_id");
                Integer ownerId = ownerIdLong.intValue();
                List<Map<String, Object>> devices = getDevicesUnderOwner(ownerId);
                owner.put("devices", devices);
            }
            
            systemAdmin.put("owners", owners);
        }
        
        hierarchy.put("system_admins", systemAdmins);
        
        return hierarchy;
    }
}