checkAuth();

const adminRole = localStorage.getItem('adminRole');
const adminId = localStorage.getItem('adminId');
const adminUsername = localStorage.getItem('adminUsername');

// 1. UI Setup
if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;
if (adminRole) {
    const badge = document.getElementById('adminRoleBadge');
    if (badge) {
        badge.textContent = adminRole.toUpperCase().replace('_', ' ');
        badge.className = 'role-badge ' + adminRole;
    }
    
    const dashLink = document.getElementById('nav-dashboard');
    if (dashLink) {
        if (adminRole === 'super_admin') dashLink.href = 'super-admin-dashboard.html';
        else dashLink.href = 'system-admin-dashboard.html';
    }
}

// 2. Determine Context
let targetSystemAdminId = null;
let targetSystemAdminName = "Me";

if (adminRole === 'super_admin') {
    const selected = JSON.parse(localStorage.getItem('selectedSystemAdmin'));
    if (selected) {
        targetSystemAdminId = selected.admin_id;
        targetSystemAdminName = selected.username;
        const breadcrumb = document.getElementById('pageBreadcrumb');
        if(breadcrumb) breadcrumb.textContent = `Super Admin > System Admin: ${targetSystemAdminName} > Owners`;
    } else {
        window.location.href = 'super-admin-dashboard.html';
    }
} else {
    targetSystemAdminId = adminId;
    const breadcrumb = document.getElementById('pageBreadcrumb');
    if(breadcrumb) breadcrumb.textContent = `Dashboard > My Owners`;
}

// 3. Load Owners
loadOwners();

async function loadOwners() {
    try {
        const response = await fetch(`${API_BASE_URL}/hierarchy/system-admin/${targetSystemAdminId}/owners`);
        const data = await response.json();
        
        if (data.success) {
            displayOwners(data.owners);
        } else {
            document.getElementById('ownersGrid').innerHTML = '<div class="empty-state">No owners found.</div>';
        }
    } catch (error) {
        console.error(error);
        document.getElementById('ownersGrid').innerHTML = '<div class="error">Error loading data.</div>';
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
            <div class="card-actions" style="position:static; margin-top:15px; display:flex; gap:10px;">
                <button class="btn-sm btn-danger" onclick="event.stopPropagation(); deleteOwner(${owner.admin_id})">
                    🗑️ Delete
                </button>
            </div>
        </div>
    `).join('');
}

function viewOwnerDevices(ownerId, username, locationId) {
    localStorage.setItem('selectedOwner', JSON.stringify({
        admin_id: ownerId,
        username: username,
        location_id: locationId
    }));
    window.location.href = 'devices.html';
}

// --- Owner Modal Logic ---
function showCreateModal() {
    document.getElementById('createModal').style.display = 'flex';
    loadLocations();
}
function closeCreateModal() {
    document.getElementById('createModal').style.display = 'none';
}

// --- NEW: Add Location Modal Logic ---
function showAddLocationModal() {
    document.getElementById('addLocationModal').style.display = 'flex';
}
function closeAddLocationModal() {
    document.getElementById('addLocationModal').style.display = 'none';
    document.getElementById('addLocationForm').reset();
}

// --- Load Locations ---
async function loadLocations() {
    try {
        const res = await fetch(`${API_BASE_URL}/location/all`);
        const data = await res.json();
        const select = document.getElementById('locationSelect');
        if (data.success) {
            select.innerHTML = '<option value="">Select Location</option>' + 
                data.locations.map(l => `<option value="${l.locationId}">${l.locationName}</option>`).join('');
        }
    } catch(e) { console.error(e); }
}

// --- Submit New Location ---
document.getElementById('addLocationForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const data = {
        location_name: fd.get('locationName'),
        address: fd.get('address')
    };
    
    try {
        const res = await fetch(`${API_BASE_URL}/location/create`, {
            method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data)
        });
        const json = await res.json();
        if(json.success) {
            alert("Location added!");
            closeAddLocationModal();
            await loadLocations(); // Refresh list
            // Auto-select the new location
            document.getElementById('locationSelect').value = json.location.locationId;
        } else {
            alert("Error: " + json.message);
        }
    } catch(err) { alert("Failed to create location"); }
});

// --- Submit New Owner ---
document.getElementById('createOwnerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const data = {
        username: fd.get('username'),
        password: fd.get('password'),
        locationId: fd.get('locationId'),
        systemAdminId: targetSystemAdminId,
        role: 'location_owner'
    };
    
    try {
        const res = await fetch(`${API_BASE_URL}/hierarchy/create-owner`, {
            method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data)
        });
        const json = await res.json();
        if(json.success) {
            alert("Owner Created!");
            closeCreateModal();
            loadOwners();
        } else { alert(json.message); }
    } catch(err) { alert("Error creating owner"); }
});

async function deleteOwner(id) {
    if(!confirm("Delete this owner?")) return;
    await fetch(`${API_BASE_URL}/hierarchy/owner/${id}`, { method: 'DELETE' });
    loadOwners();
}