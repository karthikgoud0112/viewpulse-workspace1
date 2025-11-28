const API_BASE_URL = 'http://localhost:8080/api';

// Get device info from localStorage
function getDeviceId() {
    return localStorage.getItem('deviceId');
}

function getDeviceCode() {
    return localStorage.getItem('deviceCode');
}

function getLocationId() {
    return localStorage.getItem('deviceLocationId');
}

function isDeviceLoggedIn() {
    return localStorage.getItem('deviceLoggedIn') === 'true';
}

// Check if device is logged in, redirect to login if not
function checkDeviceAuth() {
    if (!isDeviceLoggedIn()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Device logout
async function logoutDevice() {
    const deviceCode = getDeviceCode();
    
    if (deviceCode) {
        try {
            await fetch(`${API_BASE_URL}/device/logout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ device_code: deviceCode })
            });
        } catch (error) {
            console.error('Logout API error:', error);
        }
    }
    
    // Clear localStorage
    localStorage.removeItem('deviceLoggedIn');
    localStorage.removeItem('deviceId');
    localStorage.removeItem('deviceCode');
    localStorage.removeItem('deviceLocationId');
    
    // Redirect to login
    window.location.href = 'login.html';
}

// Fetch videos for current location
async function fetchVideos() {
    const locationId = getLocationId();
    
    if (!locationId) {
        console.error('No location ID found');
        return { success: false, videos: [] };
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/video/location/${locationId}`);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching videos:', error);
        return { success: false, videos: [] };
    }
}

// Submit feedback
async function submitFeedback(feedbackData) {
    try {
        const response = await fetch(`${API_BASE_URL}/feedback/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(feedbackData)
        });
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error submitting feedback:', error);
        return { success: false, message: 'Connection error' };
    }
}
