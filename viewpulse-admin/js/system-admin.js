checkAuth();

// 1. Role Verification (Stop Redirect Loops)
const adminRole = localStorage.getItem('adminRole');
if (adminRole !== 'system_admin') {
    // If you are NOT a system admin, you shouldn't be here.
    // This prevents Super Admins or Owners from accidentally loading this page and breaking.
    if (adminRole === 'super_admin') window.location.href = 'super-admin-dashboard.html';
    else window.location.href = 'dashboard.html'; // Owner
}

const adminId = localStorage.getItem('adminId');
const adminUsername = localStorage.getItem('adminUsername');

if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;

// 2. Load Data
loadMyOwners();

async function loadMyOwners() {
    try {
        // Fetch owners specifically for THIS System Admin
        const response = await fetch(`${API_BASE_URL}/hierarchy/system-admin/${adminId}/owners`);
        const data = await response.json();
        
        if (data.success) {
            displayOwners(data.owners);
            updateStats(data.owners);
        } else {
            document.getElementById('ownersGrid').innerHTML = '<div class="empty-state">No owners found.</div>';
        }
    } catch (error) {
        console.error(error);
        document.getElementById('ownersGrid').innerHTML = '<div class="error">Error connecting to backend.</div>';
    }
}

function displayOwners(owners) {
    const grid = document.getElementById('ownersGrid');
    
    if (!owners || owners.length === 0) {
        grid.innerHTML = '<div class="empty-state">No owners found. Create one to get started.</div>';
        return;
    }
    
    grid.innerHTML = owners.map(owner => `
        <div class="admin-card" onclick="viewOwnerDevices(${owner.admin_id}, '${owner.username}', ${owner.location_id})">
            <div class="card-header">
                <div class="avatar">🏪</div>
                <div class="card-title">
                    <h3>${owner.username}</h3>
                    <span class="badge ${owner.is_active ? 'badge-success' : 'badge-inactive'}">
                        ${owner.is_active ? 'Active' : 'Inactive'}
                    </span>
                </div>
            </div>
            <div class="card-stats">
                <div class="stat-item">
                    <span class="stat-icon">📍</span>
                    <span class="stat-text">Loc ID: ${owner.location_id || 'N/A'}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">📱</span>
                    <span class="stat-text">${owner.device_count || 0} Devices</span>
                </div>
            </div>
        </div>
    `).join('');
}

function updateStats(owners) {
    // Calculate total devices across all owners
    const totalDevices = owners.reduce((sum, owner) => sum + (owner.device_count || 0), 0);
    
    document.getElementById('totalOwners').textContent = owners.length;
    document.getElementById('totalDevices').textContent = totalDevices;
    // Feedback count requires a separate API call, leaving as '-' or placeholder
}

// Drill Down Logic
function viewOwnerDevices(ownerId, username, locationId) {
    localStorage.setItem('selectedOwner', JSON.stringify({
        admin_id: ownerId,
        username: username,
        location_id: locationId
    }));
    window.location.href = 'devices.html';
}

function goToOwners() {
    // Redirect to the main owners management page
    window.location.href = 'owners.html';
}