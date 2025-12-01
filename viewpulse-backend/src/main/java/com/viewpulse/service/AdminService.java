package com.viewpulse.service;

import com.viewpulse.model.AdminUser;
import com.viewpulse.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminUserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // create and save admin with bcrypt hashed password
    public AdminUser createAdmin(String username, String rawPassword, String role, Integer locationId) {
        AdminUser u = new AdminUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword)); // store bcrypt hash
        u.setRole(role);
        u.setLocationId(locationId);
        return repo.save(u);
    }
}
