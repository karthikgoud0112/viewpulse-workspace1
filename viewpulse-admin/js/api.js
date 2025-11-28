const API_BASE_URL = 'http://localhost:8080/api';

function checkAuth() {
    const loggedIn = localStorage.getItem('adminLoggedIn');
    if (!loggedIn || loggedIn !== 'true') {
        // Only redirect to index if we are NOT on index.html
        if (!window.location.href.includes('index.html')) {
            window.location.href = 'index.html';
        }
    }
}

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

// Get admin role from localStorage
function getAdminRole() {
    return localStorage.getItem('adminRole') || 'none';
}

// Get admin location ID
function getAdminLocationId() {
    const id = localStorage.getItem('adminLocationId');
    return id ? parseInt(id) : null;
}

// Check if can upload videos
function canUploadVideos() {
    const role = getAdminRole();
    return role === 'system_admin' || role === 'super_admin';
}

// Check if can view all feedback
function canViewAllFeedback() {
    const role = getAdminRole();
    return role === 'system_admin' || role === 'super_admin';
}

// Check if location_owner
function isLocationOwner() {
    return getAdminRole() === 'location_owner';
}

// Get all feedback
async function getAllFeedback() {
    try {
        const response = await fetch(`${API_BASE_URL}/feedback/all`, {
            headers: {
                'X-Admin-Role': getAdminRole(),
                'X-Admin-Location': getAdminLocationId() || ''
            }
        });
        return await response.json();
    } catch (error) {
        return [];
    }
}

// Get feedback by location
async function getFeedbackByLocation(locationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/feedback/location/${locationId}`, {
            headers: {
                'X-Admin-Role': getAdminRole(),
                'X-Admin-Location': getAdminLocationId() || ''
            }
        });
        return await response.json();
    } catch (error) {
        return [];
    }
}

// Get appropriate feedback based on role
async function getAdminFeedback() {
    const role = getAdminRole();
    const locationId = getAdminLocationId();
    
    if (isLocationOwner() && locationId) {
        return await getFeedbackByLocation(locationId);
    } else if (canViewAllFeedback()) {
        return await getAllFeedback();
    }
    return [];
}

// Get dashboard stats
async function getDashboardStats() {
    try {
        const feedbacks = await getAdminFeedback();
        const stats = {
            total: feedbacks.length,
            positive: 0,
            neutral: 0,
            negative: 0,
            emotions: {}
        };
        feedbacks.forEach(fb => {
            const emotion = fb.detectedEmotion || 'neutral';
            stats.emotions[emotion] = (stats.emotions[emotion] || 0) + 1;
            if (['joy', 'love', 'surprise'].includes(emotion)) {
                stats.positive++;
            } else if (['sadness', 'anger', 'fear'].includes(emotion)) {
                stats.negative++;
            } else {
                stats.neutral++;
            }
        });
        return stats;
    } catch (error) {
        return {total: 0, positive: 0, neutral: 0, negative: 0, emotions: {}};
    }
}
