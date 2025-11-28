package com.viewpulse.repository;

import com.viewpulse.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByDeviceCode(String deviceCode);
    
    // FIX: Changed from Long to Integer
    List<Device> findByLocationId(Integer locationId);
    
    List<Device> findByIsLoggedInTrue();
}