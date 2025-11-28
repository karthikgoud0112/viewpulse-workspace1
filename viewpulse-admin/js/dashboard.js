checkAuth();

const adminUsername = localStorage.getItem('adminUsername');
const adminRole = localStorage.getItem('adminRole');
const adminLocationId = localStorage.getItem('adminLocationId');

if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;

const roleBadge = document.getElementById('adminRoleBadge');
if (roleBadge && adminRole) {
    roleBadge.textContent = adminRole.toUpperCase().replace('_', ' ');
    roleBadge.className = 'role-badge ' + adminRole;
    
    // --- HIDE VIDEOS LINK FOR OWNERS ---
    if (adminRole === 'location_owner') {
        const videoLink = document.getElementById('nav-videos');
        if (videoLink) videoLink.style.display = 'none';
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // Load stats based on role
    if (adminRole === 'location_owner' && adminLocationId) {
        loadOwnerStats(adminLocationId);
        loadOwnerFeedbacks(adminLocationId);
    } else {
        console.log("Super/System Admin Dashboard loaded");
    }
});

async function loadOwnerStats(locationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/feedback/location/${locationId}/stats`);
        const stats = await response.json();
        
        if (stats.success) {
            document.getElementById('statTotal').textContent = stats.total || 0;
            document.getElementById('statPositive').textContent = stats.positive || 0;
            document.getElementById('statNeutral').textContent = stats.neutral || 0;
            document.getElementById('statNegative').textContent = stats.negative || 0;
        }
    } catch (e) {
        console.error("Stats error", e);
    }
}

async function loadOwnerFeedbacks(locationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/feedback/location/${locationId}`);
        const data = await response.json();
        
        const tbody = document.getElementById('dashboardFeedbackBody');
        tbody.innerHTML = '';
        
        if (!data.feedbacks || data.feedbacks.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center">No feedback yet.</td></tr>';
            return;
        }
        
        data.feedbacks.slice(0, 10).forEach(fb => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${new Date(fb.createdAt).toLocaleString()}</td>
                <td>${fb.feedbackText}</td>
                <td><span class="badge badge-success">${fb.detectedEmotion || '-'}</span></td>
                <td>${fb.emotionConfidence ? Math.round(fb.emotionConfidence * 100) + '%' : '-'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error("Load feedback error", e);
        document.getElementById('dashboardFeedbackBody').innerHTML = '<tr><td colspan="4">Error loading data.</td></tr>';
    }
}