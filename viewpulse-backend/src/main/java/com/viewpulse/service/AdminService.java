package com.viewpulse.service;

import com.viewpulse.model.AdminUser;
import com.viewpulse.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminUserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create admin and store bcrypt hash
    public AdminUser createAdmin(String username, String rawPassword, String role, Integer locationId) {
        AdminUser u = new AdminUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setLocationId(locationId);
        return repo.save(u);
    }

    public Optional<AdminUser> findByUsername(String username) {
        return repo.findByUsername(username);
    }
}
