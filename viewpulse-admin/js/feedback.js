checkAuth();

const adminUsername = localStorage.getItem('adminUsername');
const adminRole = localStorage.getItem('adminRole');
const adminLocationId = localStorage.getItem('adminLocationId');

if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;

const roleBadge = document.getElementById('adminRoleBadge');
if (roleBadge && adminRole) {
    roleBadge.textContent = adminRole.toUpperCase().replace('_', ' ');
    roleBadge.className = 'role-badge ' + adminRole;
    
    // Hide Videos for Owners
    if (adminRole === 'location_owner') {
        const videoLink = document.getElementById('nav-videos');
        if (videoLink) videoLink.style.display = 'none';
    }
}

loadFeedback();

async function loadFeedback() {
    try {
        let url;
        const filterDeviceId = localStorage.getItem('filterDeviceId');
        
        // 1. Check if we are filtering by a specific device (Drill-Down Mode)
        if (filterDeviceId) {
            url = `${API_BASE_URL}/feedback/device/${filterDeviceId}`;
            
            // Update header to show we are filtering
            const header = document.querySelector('.page-header h1');
            if(header) header.textContent = `Feedback for Device #${filterDeviceId}`;
            
            // Clear filter so subsequent page loads (via sidebar) show all
            // Note: sidebar-switcher.js handles the full cleanup on exitDeviceView
            // But we still clear it here so subsequent clicks to the sidebar link are clean.
            // localStorage.removeItem('filterDeviceId'); 
        } 
        // 2. Else check if Owner (Location-based)
        else if (adminRole === 'location_owner') {
            url = `${API_BASE_URL}/feedback/location/${adminLocationId}`;
        } 
        // 3. Else Super/System Admin (All)
        else {
            url = `${API_BASE_URL}/feedback/all`;
        }

        const response = await fetch(url);
        const data = await response.json();
        
        renderFeedbackTable(data.feedbacks);
    } catch (error) {
        console.error(error);
        const tbody = document.getElementById('feedbackBody');
        if(tbody) tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">Error loading feedback</td></tr>';
    }
}

function renderFeedbackTable(feedbacks) {
    const tbody = document.getElementById('feedbackBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    if (!feedbacks || feedbacks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">No feedback found.</td></tr>';
        return;
    }
    
    feedbacks.forEach((fb) => {
        tbody.innerHTML += `
            <tr>
                <td>${new Date(fb.createdAt).toLocaleString()}</td>
                <td>${fb.customerPhone || 'N/A'}</td>
                <td>${fb.feedbackText}</td>
                <td><span class="badge badge-success">${fb.detectedEmotion || '-'}</span></td>
                <td>${fb.emotionConfidence ? Math.round(fb.emotionConfidence * 100) + '%' : '-'}</td>
            </tr>
        `;
    });
}