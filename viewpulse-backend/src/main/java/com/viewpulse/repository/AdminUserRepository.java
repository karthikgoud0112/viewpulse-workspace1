package com.viewpulse.repository;

import com.viewpulse.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    Optional<AdminUser> findByUsername(String username);
    
    List<AdminUser> findByRole(String role);
    
    List<AdminUser> findByLocationId(Long locationId);
}
