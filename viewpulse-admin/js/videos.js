checkAuth();

const adminUsername = localStorage.getItem('adminUsername');
const adminId = localStorage.getItem('adminId');
const adminRole = localStorage.getItem('adminRole');
const selectedDevice = JSON.parse(localStorage.getItem('selectedDevice')); // Check for device context

if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;

// Setup Role Badge
if (adminRole) {
    const badge = document.getElementById('adminRoleBadge');
    if (badge) {
        badge.textContent = adminRole.toUpperCase().replace('_', ' ');
        badge.className = 'role-badge ' + adminRole;
    }
}

// --- Main Logic ---
loadVideos();

async function loadVideos() {
    try {
        let url;
        
        // 1. If in "Device Drill-Down" Mode (Super/System Admin managing specific device)
        if (selectedDevice && (adminRole === 'super_admin' || adminRole === 'system_admin')) {
            // Fetch videos ONLY for this device's location
            url = `${API_BASE_URL}/video/admin/location/${selectedDevice.location_id}`;
            
            // Update Header to reflect context
            const header = document.querySelector('.page-header h1');
            if(header) header.textContent = `Videos for ${selectedDevice.device_code}`;
        }
        // 2. If Location Owner
        else if (adminRole === 'location_owner') {
            const adminLocationId = localStorage.getItem('adminLocationId');
            url = `${API_BASE_URL}/video/admin/location/${adminLocationId}`;
        } 
        // 3. Default Super Admin (View All)
        else {
            url = `${API_BASE_URL}/video/all`;
        }

        const response = await fetch(url);
        const data = await response.json();
        
        if (data.success && data.videos.length > 0) {
            displayVideos(data.videos);
        } else {
            document.getElementById('videosGrid').innerHTML = '<div class="empty-state">No videos uploaded yet</div>';
        }
    } catch (e) {
        console.error(e);
        document.getElementById('videosGrid').innerHTML = '<div class="loading">Connection error. Check backend.</div>';
    }
}

function displayVideos(videos) {
    const grid = document.getElementById('videosGrid');
    grid.innerHTML = videos.map(video => `
        <div class="admin-card">
            <div class="card-header">
                <div class="avatar">🎬</div>
                <div class="card-title">
                    <h3>${video.videoTitle}</h3>
                </div>
            </div>
            <div class="card-stats">
                <div class="stat-item">
                    <span class="stat-icon">📍</span>
                    <span class="stat-text">Loc ID: ${video.locationId || 'N/A'}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">⏳</span>
                    <span class="stat-text">Duration: ${video.duration}s</span>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">📅</span>
                    <span class="stat-text">Uploaded: ${video.createdAt ? new Date(video.createdAt).toLocaleDateString() : 'N/A'}</span>
                </div>
            </div>
            <video controls width="100%" style="margin-top:10px; border-radius:8px; background:black;">
                <source src="http://localhost:8080/uploads/videos/${video.videoPath}" type="video/mp4">
                Video playback not supported
            </video>
            <div class="card-actions" style="margin-top:10px; text-align:right;">
                 <button class="btn-sm btn-danger" onclick="deleteVideo(${video.videoId})">🗑️ Delete</button>
            </div>
        </div>
    `).join('');
}

// --- Upload Logic ---

async function loadLocations() {
    const select = document.getElementById('locationId');
    // If managing a specific device, PRE-SELECT its location and lock it
    if (selectedDevice) {
        select.innerHTML = `<option value="${selectedDevice.location_id}" selected>Current Device Location (ID: ${selectedDevice.location_id})</option>`;
        select.disabled = true; // Lock selection
        return;
    }

    // Otherwise load all locations (for Super Admin global view)
    try {
        // FIXED: Use API_BASE_URL
        const response = await fetch(`${API_BASE_URL}/location/all`);
        const data = await response.json();
        
        if (data.success && data.locations) {
            select.innerHTML = '<option value="">Select Location</option>' +
                data.locations.map(loc => 
                    `<option value="${loc.locationId}">${loc.locationName}</option>`
                ).join('');
        }
    } catch (error) {
        console.error('Error loading locations:', error);
        select.innerHTML = '<option value="">Error loading locations</option>';
    }
}

function showUploadModal() {
    loadLocations(); 
    document.getElementById('uploadModal').style.display = 'flex';
}

function closeUploadModal() {
    document.getElementById('uploadModal').style.display = 'none';
    document.getElementById('uploadVideoForm').reset();
}

async function uploadVideo(event) {
    event.preventDefault();
    
    const form = document.getElementById('uploadVideoForm');
    const formData = new FormData(form);
    
    // Handle locked location selection
    let locationId = formData.get('locationId');
    if (!locationId && selectedDevice) {
        locationId = selectedDevice.location_id; // Use hidden value if disabled
    }
    
    if (!locationId) {
        alert("Please select a location for the video.");
        return;
    }
    
    formData.set('locationId', locationId);
    
    try {
        // FIXED: Use API_BASE_URL
        const response = await fetch(`${API_BASE_URL}/video/upload-file`, {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        if (result.success) {
            alert("Video uploaded successfully!");
            closeUploadModal();
            loadVideos();
        } else {
            alert(result.message || 'Failed to upload.');
        }
    } catch (error) {
        alert('Error uploading video.');
        console.error(error);
    }
}

async function deleteVideo(videoId) {
    if (!confirm('Delete this video?')) return;
    try {
        // FIXED: Use API_BASE_URL
        const response = await fetch(`${API_BASE_URL}/video/${videoId}`, { method: 'DELETE' });
        const result = await response.json();
        if(result.success) loadVideos();
        else alert(result.message);
    } catch(e) { alert("Error deleting video"); }
}