package com.viewpulse.repository;

import com.viewpulse.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    // FIX: Changed parameter type from Long to Integer
    List<Video> findByLocationId(Integer locationId);
    
    // FIX: Changed parameter type from Long to Integer
    List<Video> findByLocationIdAndIsActiveTrue(Integer locationId);
}