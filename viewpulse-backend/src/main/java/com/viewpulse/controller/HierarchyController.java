package com.viewpulse.controller;

import com.viewpulse.dto.CreateAdminRequest;
import com.viewpulse.service.HierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hierarchy")
@CrossOrigin(origins = "*")
public class HierarchyController {
    
    @Autowired
    private HierarchyService hierarchyService;
    
    // Get all system admins (for super admin)
    @GetMapping("/system-admins")
    public ResponseEntity<Map<String, Object>> getAllSystemAdmins() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> systemAdmins = hierarchyService.getAllSystemAdmins();
            response.put("success", true);
            response.put("system_admins", systemAdmins);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching system admins: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get owners under a system admin
    @GetMapping("/system-admin/{systemAdminId}/owners")
    public ResponseEntity<Map<String, Object>> getOwnersUnderSystemAdmin(@PathVariable Integer systemAdminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> owners = hierarchyService.getOwnersUnderSystemAdmin(systemAdminId);
            response.put("success", true);
            response.put("owners", owners);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching owners: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Get devices under an owner
    @GetMapping("/owner/{ownerId}/devices")
    public ResponseEntity<Map<String, Object>> getDevicesUnderOwner(@PathVariable Integer ownerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> devices = hierarchyService.getDevicesUnderOwner(ownerId);
            response.put("success", true);
            response.put("devices", devices);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching devices: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // FIX 1: getCompleteHierarchy was missing in the service; assuming service uses a generic implementation now
    @GetMapping("/complete")
    public ResponseEntity<Map<String, Object>> getCompleteHierarchy() {
        try {
            Map<String, Object> hierarchy = hierarchyService.getCompleteHierarchy();
            hierarchy.put("success", true);
            return ResponseEntity.ok(hierarchy);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching hierarchy: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Create system admin (by super admin)
    @PostMapping("/create-system-admin")
    public ResponseEntity<Map<String, Object>> createSystemAdmin(@RequestBody CreateAdminRequest request) {
        Map<String, Object> response = hierarchyService.createSystemAdmin(
            request.getUsername(),
            request.getPassword(),
            1 // Assuming super admin ID is 1
        );
        return ResponseEntity.ok(response);
    }
    
    // Create owner (by system admin or super admin)
    @PostMapping("/create-owner")
    public ResponseEntity<Map<String, Object>> createOwner(@RequestBody CreateAdminRequest request) {
        Map<String, Object> response = hierarchyService.createOwner(
            request.getUsername(),
            request.getPassword(),
            request.getLocationId(),
            request.getSystemAdminId(),
            request.getSystemAdminId() // created_by is the system admin
        );
        return ResponseEntity.ok(response);
    }
    
    // FIX 2 & 3: deleteSystemAdmin and deleteOwner methods were causing errors
    @DeleteMapping("/system-admin/{systemAdminId}")
    public ResponseEntity<Map<String, Object>> deleteSystemAdmin(@PathVariable Integer systemAdminId) {
        Map<String, Object> response = hierarchyService.deleteSystemAdmin(systemAdminId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/owner/{ownerId}")
    public ResponseEntity<Map<String, Object>> deleteOwner(@PathVariable Integer ownerId) {
        Map<String, Object> response = hierarchyService.deleteOwner(ownerId);
        return ResponseEntity.ok(response);
    }
}