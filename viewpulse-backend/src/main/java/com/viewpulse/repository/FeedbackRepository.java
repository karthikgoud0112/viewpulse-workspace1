package com.viewpulse.repository;

import com.viewpulse.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByLocationId(Integer locationId);
    
    // ADDED: Search method for filtering by device
    List<Feedback> findByDeviceId(Long deviceId);
}