package com.viewpulse.service;

import com.viewpulse.model.WhatsAppMessage;
import com.viewpulse.repository.WhatsAppMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class WhatsAppService {
    
    @Autowired
    private WhatsAppMessageRepository whatsAppMessageRepository;
    
    @Value("${whatsapp.access.token}")
    private String metaAccessToken;
    
    @Value("${whatsapp.business.phone.id}")
    private String whatsAppBusinessPhoneId; 
    
    private final RestTemplate restTemplate = new RestTemplate(); 

    private static final String META_API_BASE = "https://graph.facebook.com/v18.0";

    // --- TEMPLATE LOGIC ---
    // NOTE: For now, we will use the standard 'hello_world' template, 
    // and ignore the emotion-based message body, as custom text bodies 
    // are blocked outside the 24-hour window.
    // The message received will be the generic "Welcome and congratulations..." message.
    private static final String DEFAULT_TEMPLATE_NAME = "hello_world";
    
    @Transactional
    public Map<String, Object> sendWhatsAppMessage(Integer feedbackId, String customerPhone, 
                                                     String emotion, String feedbackText) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cleanPhone = cleanPhoneNumber(customerPhone);
            String apiUrl = META_API_BASE + "/" + whatsAppBusinessPhoneId + "/messages";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(metaAccessToken);
            
            // --- UPDATED JSON BODY TO SEND TEMPLATE ---
            
            // 1. Build the Language object
            Map<String, String> language = new HashMap<>();
            language.put("code", "en_US");

            // 2. Build the Template object
            Map<String, Object> template = new HashMap<>();
            template.put("name", DEFAULT_TEMPLATE_NAME);
            template.put("language", language);

            // 3. Build the Root Message Body
            Map<String, Object> message = new HashMap<>();
            message.put("messaging_product", "whatsapp");
            message.put("to", cleanPhone);
            message.put("type", "template"); // Critical change!
            message.put("template", template);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);
            
            // 4. Execute the API Call
            ResponseEntity<Map<String, Object>> apiResponse = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            // 5. Extract Message ID
            Object messagesObject = apiResponse.getBody().get("messages");
            String metaMessageId = "UNKNOWN_ID";
            if (messagesObject instanceof List) {
                List messagesList = (List) messagesObject;
                if (!messagesList.isEmpty()) {
                    metaMessageId = (String) ((Map) messagesList.get(0)).get("id");
                }
            } 

            // Save success to database
            WhatsAppMessage msg = new WhatsAppMessage();
            msg.setFeedbackId(feedbackId);
            msg.setCustomerPhone(cleanPhone); 
            msg.setMessageText("Sent template: " + DEFAULT_TEMPLATE_NAME); // Note the generic message body
            msg.setEmotion(emotion);
            msg.setStatus("sent");
            msg.setTwilioSid(metaMessageId); 
            
            whatsAppMessageRepository.save(msg);
            
            response.put("success", true);
            response.put("message", "Template message sent successfully via Meta API");
            response.put("meta_id", metaMessageId);
            
        } catch (Exception e) {
            System.err.println("❌ META API CALL FAILED. Reason: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Error sending WhatsApp: " + e.getMessage());
            
            // Error saving logic remains the same
            try {
                WhatsAppMessage errorMessage = new WhatsAppMessage();
                errorMessage.setFeedbackId(feedbackId);
                errorMessage.setCustomerPhone(customerPhone);
                errorMessage.setMessageText("Error attempting to send response.");
                errorMessage.setEmotion(emotion);
                errorMessage.setStatus("failed");
                errorMessage.setErrorMessage(e.getMessage());
                errorMessage.setSentAt(LocalDateTime.now());
                whatsAppMessageRepository.save(errorMessage);
            } catch (Exception saveError) {
                System.err.println("Error saving failed message: " + saveError.getMessage());
            }
        }
        
        return response;
    }
    
    private String cleanPhoneNumber(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");
        
        if (cleaned.length() == 10 && !cleaned.startsWith("91")) {
            cleaned = "91" + cleaned;
        }
        
        return cleaned; 
    }

    public List<Map<String, Object>> getMessagesForLocation(Integer locationId) { return new ArrayList<>(); }
    public Map<String, Object> updateMessageStatus(String twilioSid, String status) { return new HashMap<>(); }
    public Map<String, Object> getMessageStats(Integer locationId) { return new HashMap<>(); }
}