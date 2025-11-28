package com.viewpulse.repository;

import com.viewpulse.model.WhatsAppMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long> {
    
    // Find message by feedback ID
    Optional<WhatsAppMessage> findByFeedbackId(Integer feedbackId);
    
    // Find all messages by phone number
    List<WhatsAppMessage> findByCustomerPhone(String customerPhone);
    
    // Find messages by status
    List<WhatsAppMessage> findByStatus(String status);
    
    // Find message by Twilio SID
    Optional<WhatsAppMessage> findByTwilioSid(String twilioSid);
    
    // Get messages for a specific location (via feedback)
    @Query("SELECT w FROM WhatsAppMessage w " +
           "JOIN Feedback f ON w.feedbackId = f.feedbackId " +
           "WHERE f.locationId = :locationId " +
           "ORDER BY w.sentAt DESC")
    List<WhatsAppMessage> findByLocationId(@Param("locationId") Integer locationId);
    
    // Count messages by status for a location
    @Query("SELECT COUNT(w) FROM WhatsAppMessage w " +
           "JOIN Feedback f ON w.feedbackId = f.feedbackId " +
           "WHERE f.locationId = :locationId AND w.status = :status")
    Long countByLocationIdAndStatus(
        @Param("locationId") Integer locationId,
        @Param("status") String status
    );
}
