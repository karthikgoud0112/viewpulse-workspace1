package com.viewpulse.repository;

import com.viewpulse.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    // JpaRepository provides findAll(), findById(), save(), delete() automatically
    
    // Custom query methods (optional)
    List<Location> findByLocationNameContaining(String name);
}
