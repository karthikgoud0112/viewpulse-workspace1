// Check authentication
if (!checkDeviceAuth()) {
    // Will redirect to login if not authenticated
}

// Handle feedback form submission
document.getElementById('feedbackForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const customerPhone = document.getElementById('customerPhone').value.trim();
    const feedbackText = document.getElementById('feedbackText').value.trim();
    const messageEl = document.getElementById('feedbackMessage');
    
    // Validation
    if (!feedbackText || feedbackText.length < 5) {
        messageEl.textContent = '❌ Please enter at least 5 characters';
        messageEl.className = 'error';
        return;
    }
    
    // Show loading
    messageEl.textContent = 'Submitting feedback...';
    messageEl.className = '';
    messageEl.style.display = 'block';
    
    // --- CRITICAL UPDATE: SEND DEVICE ID ---
    // We fetch deviceId and locationId from localStorage (via api.js helper functions)
    const feedbackData = {
        locationId: parseInt(getLocationId()),
        deviceId: parseInt(getDeviceId()), // <--- This ensures the feedback is linked to this screen
        customerPhone: customerPhone || null,
        feedbackText: feedbackText
    };
    
    try {
        const data = await submitFeedback(feedbackData);
        
        if (data.success) {
            // Save emotion to localStorage for thank you page
            localStorage.setItem('lastEmotion', data.emotion || 'joy');
            localStorage.setItem('lastConfidence', data.confidence || '0.85');
            
            // Redirect to thank you page
            window.location.href = `thankyou.html?emotion=${data.emotion}&confidence=${data.confidence}`;
        } else {
            messageEl.textContent = '❌ ' + (data.message || 'Failed to submit feedback');
            messageEl.className = 'error';
        }
    } catch (error) {
        console.error('Feedback error:', error);
        messageEl.textContent = '❌ Connection error. Please try again.';
        messageEl.className = 'error';
    }
});

// Back button
function goBackToAds() {
    window.location.href = 'ads.html';
}