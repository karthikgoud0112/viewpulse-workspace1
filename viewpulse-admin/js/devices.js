checkAuth();

const adminUsername = localStorage.getItem('adminUsername');
const adminRole = localStorage.getItem('adminRole');
const adminLocationId = localStorage.getItem('adminLocationId');

// UI Setup
if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;

if (adminRole) {
    const badge = document.getElementById('adminRoleBadge');
    if(badge) {
        badge.textContent = adminRole.toUpperCase().replace('_', ' ');
        badge.className = 'role-badge ' + adminRole;
    }
    
    // Breadcrumb Setup
    const header = document.querySelector('.page-header div');
    let breadcrumb = document.querySelector('.breadcrumb');
    if (!breadcrumb && header) {
        breadcrumb = document.createElement('div');
        breadcrumb.className = 'breadcrumb';
        breadcrumb.style.cssText = "font-size:14px; color:#666; margin-bottom:5px;";
        header.prepend(breadcrumb);
    }

    // --- LOCATION OWNER LOGIC ---
    if (adminRole === 'location_owner') {
        const dashLink = document.getElementById('nav-dashboard');
        if(dashLink) dashLink.href = 'dashboard.html';
        
        const vidLink = document.getElementById('nav-videos');
        if(vidLink) vidLink.style.display = 'none';
        
        const addBtn = document.getElementById('addDeviceBtn');
        if(addBtn) {
            addBtn.style.display = 'none';
            addBtn.remove();
        }
        if(breadcrumb) breadcrumb.textContent = "Dashboard > My Devices";
    } 
    // --- ADMIN LOGIC (Drill Down) ---
    else {
        const selectedOwner = JSON.parse(localStorage.getItem('selectedOwner'));
        if (selectedOwner && breadcrumb) {
            breadcrumb.textContent = `... > Owner: ${selectedOwner.username} > Devices`;
        }
        
        const dashLink = document.getElementById('nav-dashboard');
        if(dashLink) {
            dashLink.href = (adminRole === 'super_admin') ? 'super-admin-dashboard.html' : 'system-admin-dashboard.html';
        }
    }
}

loadDevices();

async function loadDevices() {
    try {
        let url;
        
        if (adminRole === 'location_owner') {
            url = `${API_BASE_URL}/device/location/${adminLocationId}`;
        } else {
            const selectedOwner = JSON.parse(localStorage.getItem('selectedOwner'));
            if (selectedOwner) {
                url = `${API_BASE_URL}/hierarchy/owner/${selectedOwner.admin_id}/devices`;
            } else {
                url = `${API_BASE_URL}/device/all`; 
            }
        }
        
        const response = await fetch(url);
        const data = await response.json();
        
        if (data.success) {
            renderDevicesGrid(data.devices);
        } else {
             document.getElementById('devicesGrid').innerHTML = '<div class="empty-state">No devices found.</div>';
        }
    } catch (error) {
        console.error(error);
        document.getElementById('devicesGrid').innerHTML = '<div class="error">Error loading devices.</div>';
    }
}

function renderDevicesGrid(devices) {
    const grid = document.getElementById('devicesGrid');
    if (!grid) return;
    
    grid.innerHTML = '';
    if (!devices || devices.length === 0) {
        grid.innerHTML = '<div class="empty-state">No devices registered yet.</div>';
        return;
    }
    
    devices.forEach((device) => {
        // --- DATA NORMALIZATION FIX (Handles both camelCase and snake_case) ---
        const dId = device.deviceId || device.device_id;
        const dCode = device.deviceCode || device.device_code;
        const locId = device.locationId || device.location_id;
        const dPass = device.devicePassword || '***'; 
        const isLoggedIn = (device.isLoggedIn !== undefined) ? device.isLoggedIn : (device.is_logged_in !== undefined ? device.is_logged_in : false);
        // -----------------------------------------------------------------------

        const statusClass = isLoggedIn ? 'badge-success' : 'badge-inactive';
        const statusText = isLoggedIn ? '🟢 Online' : '⚪ Offline';
        
        // DELETE BUTTON (Super Admin/System Admin only)
        let deleteBtn = '';
        if (adminRole === 'super_admin' || adminRole === 'system_admin') {
            deleteBtn = `
                <button class="btn-sm btn-danger" style="padding: 6px 10px; background:#fee2e2; color:#991b1b; border:none;" onclick="deleteDevice(${dId})">
                    🗑️
                </button>
            `;
        }

        const card = document.createElement('div');
        card.className = 'admin-card'; 
        card.innerHTML = `
            <div class="card-header">
                <div class="avatar">📱</div>
                <div class="card-title">
                    <h3>${dCode}</h3>
                    <span class="badge ${statusClass}">${statusText}</span>
                </div>
                ${deleteBtn}
            </div>
            <div class="card-stats">
                <div class="stat-item">
                    <span class="stat-icon">📍</span>
                    <span class="stat-text">Loc ID: ${locId}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">🆔</span>
                    <span class="stat-text">Dev ID: ${dId}</span>
                </div>
            </div>
            <div class="card-actions" style="position:static; margin-top:15px; display:flex; gap:10px;">
                <button class="btn-sm btn-primary" style="flex:1;" onclick="enterDeviceView(${dId}, '${dCode}', ${locId})">
                    👁️ View Details
                </button>
            </div>
        `;
        grid.appendChild(card);
    });
}

// --- NAVIGATION LOGIC ---

function enterDeviceView(deviceId, deviceCode, locationId) {
    // 1. Save Device Context
    localStorage.setItem('selectedDevice', JSON.stringify({
        device_id: deviceId,
        device_code: deviceCode,
        location_id: locationId
    }));
    
    // 2. Save Filter for Feedback Page
    localStorage.setItem('filterDeviceId', deviceId);
    
    // 3. Go to Feedback (Default view for device)
    window.location.href = 'feedback.html';
}

// --- ACTION LOGIC ---

async function deleteDevice(deviceId) {
    if (!confirm("Are you sure you want to delete this device? This action cannot be undone.")) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/device/${deviceId}`, { method: 'DELETE' });
        const result = await response.json();
        
        if(result.success) {
            alert("Device deleted successfully.");
            loadDevices();
        } else {
            alert(result.message || "Failed to delete device.");
        }
    } catch(e) {
        alert("Error deleting device. Check backend endpoint.");
    }
}

// --- MODAL LOGIC (For Admins) ---

function showAddDeviceModal() {
    const modal = document.getElementById('addDeviceModal');
    const locationInfo = document.getElementById('modalLocationInfo');
    
    let locId = null;
    const selectedOwner = JSON.parse(localStorage.getItem('selectedOwner'));

    if (selectedOwner && selectedOwner.location_id) {
        locId = selectedOwner.location_id;
    } else if (adminRole === 'system_admin' && adminLocationId) {
        locId = adminLocationId;
    }

    if (locId) {
        locationInfo.textContent = `Adding device to Location ID: ${locId}`;
        locationInfo.style.display = 'block';
    } else {
        alert("Error: Location context is missing for this operation. Please select an owner.");
        return; 
    }

    modal.style.display = 'flex';
}

function closeAddDeviceModal() {
    document.getElementById('addDeviceModal').style.display = 'none';
    document.getElementById('addDeviceForm').reset();
}

const addDeviceForm = document.getElementById('addDeviceForm');
if (addDeviceForm) {
    addDeviceForm.addEventListener('submit', async function (e) {
        e.preventDefault();
        const deviceCode = document.getElementById('deviceCode').value.trim();
        const devicePassword = document.getElementById('devicePassword').value.trim();
        
        let targetLocationId = null;
        const selectedOwner = JSON.parse(localStorage.getItem('selectedOwner'));
        
        if (selectedOwner && selectedOwner.location_id) {
            targetLocationId = selectedOwner.location_id;
        } else if (adminRole === 'system_admin' && adminLocationId) {
            targetLocationId = adminLocationId;
        }

        if (!targetLocationId) {
            alert("Error: Could not determine the destination Location ID.");
            return;
        }

        const data = { 
            deviceCode: deviceCode, 
            devicePassword: devicePassword, 
            locationId: parseInt(targetLocationId) 
        };
        
        try {
            const response = await fetch(`${API_BASE_URL}/device/add`, {
                method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data)
            });
            const result = await response.json();
            if (result.success) {
                alert("Device added!");
                closeAddDeviceModal();
                loadDevices();
            } else {
                alert(result.message);
            }
        } catch (error) { alert('Error adding device.'); }
    });
}