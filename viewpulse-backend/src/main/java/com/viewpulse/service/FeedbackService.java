package com.viewpulse.service;

import com.viewpulse.model.Feedback;
import com.viewpulse.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Transactional
    public Feedback saveFeedback(Integer locationId, String customerPhone, 
                                   String feedbackText, String emotion, Double confidence) {
        Feedback feedback = new Feedback();
        feedback.setLocationId(locationId);
        feedback.setCustomerPhone(customerPhone);
        feedback.setFeedbackText(feedbackText);
        feedback.setDetectedEmotion(emotion);
        feedback.setEmotionConfidence(confidence);
        
        return feedbackRepository.save(feedback);
    }
    
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
    
    public List<Feedback> getFeedbackByLocation(Integer locationId) {
        return feedbackRepository.findByLocationId(locationId);
    }
    
    public Feedback getFeedbackById(Integer feedbackId) {
        return feedbackRepository.findById(feedbackId).orElse(null);
    }
    
    @Transactional
    public boolean deleteFeedback(Integer feedbackId) {
        if (feedbackRepository.existsById(feedbackId)) {
            feedbackRepository.deleteById(feedbackId);
            return true;
        }
        return false;
    }
}
