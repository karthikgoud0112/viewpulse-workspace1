package com.viewpulse.repository;

import com.viewpulse.model.SystemAdminOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemAdminOwnerRepository extends JpaRepository<SystemAdminOwner, Long> {
    
    // Find all owners under a specific system admin
    List<SystemAdminOwner> findBySystemAdminId(Integer systemAdminId);
    
    // Find which system admin owns a specific owner
    Optional<SystemAdminOwner> findByOwnerId(Integer ownerId);
    
    // Check if owner is already assigned
    boolean existsByOwnerId(Integer ownerId);
    
    // Delete assignment by owner ID
    void deleteByOwnerId(Integer ownerId);
}
