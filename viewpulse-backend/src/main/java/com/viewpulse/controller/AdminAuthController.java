package com.viewpulse.controller;

import com.viewpulse.dto.LoginRequest;
import com.viewpulse.model.AdminUser;
import com.viewpulse.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminAuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Find user by username
        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(request.getUsername());
        
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            
            // Check password (TODO: Use proper password hashing in production)
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("admin_id", user.getAdminId());
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                response.put("location_id", user.getLocationId());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }
        } else {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "admin_auth");
        return ResponseEntity.ok(response);
    }
}
