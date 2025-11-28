checkAuth();

// Verify Role (Security)
const adminRole = localStorage.getItem('adminRole');
if (adminRole !== 'super_admin') {
    window.location.href = 'index.html';
}

const adminUsername = localStorage.getItem('adminUsername');
if (adminUsername) document.getElementById('adminUsername').textContent = adminUsername;

const roleBadge = document.getElementById('adminRoleBadge');
if (roleBadge) {
    roleBadge.textContent = "SUPER ADMIN";
    roleBadge.className = 'role-badge super_admin';
}

// Load Data
loadSystemAdmins();

async function loadSystemAdmins() {
    try {
        const response = await fetch(`${API_BASE_URL}/hierarchy/system-admins`);
        const data = await response.json();
        
        if (data.success) {
            displaySystemAdmins(data.system_admins);
            updateStats(data.system_admins);
        }
    } catch (error) {
        console.error('Error loading system admins:', error);
        document.getElementById('systemAdminsGrid').innerHTML = '<div class="error">Error loading data. Backend running?</div>';
    }
}

function displaySystemAdmins(systemAdmins) {
    const grid = document.getElementById('systemAdminsGrid');
    
    if (!systemAdmins || systemAdmins.length === 0) {
        grid.innerHTML = '<div class="empty-state">No system admins found. Create one to get started.</div>';
        return;
    }
    
    grid.innerHTML = systemAdmins.map(admin => `
        <div class="admin-card" onclick="viewOwners(${admin.admin_id}, '${admin.username}')">
            <div class="card-header">
                <div class="avatar">👤</div>
                <div class="card-title">
                    <h3>${admin.username}</h3>
                    <span class="badge ${admin.is_active ? 'badge-success' : 'badge-inactive'}">
                        ${admin.is_active ? 'Active' : 'Inactive'}
                    </span>
                </div>
            </div>
            <div class="card-stats">
                <div class="stat-item">
                    <span class="stat-icon">🏢</span>
                    <span class="stat-text">${admin.owner_count} Owners</span>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">📅</span>
                    <span class="stat-text">Created ${new Date(admin.created_at).toLocaleDateString()}</span>
                </div>
            </div>
            <div class="card-actions" style="position:static; margin-top:15px;">
                <button class="btn-sm btn-danger" onclick="event.stopPropagation(); deleteSystemAdmin(${admin.admin_id}, '${admin.username}')">
                    🗑️ Delete
                </button>
            </div>
        </div>
    `).join('');
}

function updateStats(systemAdmins) {
    // 1. Calculate basic stats from the data we have
    const totalOwners = systemAdmins.reduce((sum, admin) => sum + (admin.owner_count || 0), 0);
    
    // 2. Update Elements SAFELY (Check if they exist first)
    const elSystemAdmins = document.getElementById('totalSystemAdmins');
    if (elSystemAdmins) elSystemAdmins.textContent = systemAdmins.length;

    const elOwners = document.getElementById('totalOwners');
    if (elOwners) elOwners.textContent = totalOwners;
    
    // 3. Placeholders for global stats (Backend update needed for real numbers)
    const elDevices = document.getElementById('totalDevices');
    if (elDevices) elDevices.textContent = "-"; 
    
    const elFeedback = document.getElementById('totalFeedback');
    if (elFeedback) elFeedback.textContent = "-"; 
}

function viewOwners(systemAdminId, username) {
    localStorage.setItem('selectedSystemAdmin', JSON.stringify({
        admin_id: systemAdminId,
        username: username
    }));
    window.location.href = 'owners.html';
}

// --- Create System Admin Logic ---
function showCreateModal() {
    document.getElementById('createModal').style.display = 'flex';
}

function closeCreateModal() {
    document.getElementById('createModal').style.display = 'none';
    document.getElementById('createSystemAdminForm').reset();
}

const createForm = document.getElementById('createSystemAdminForm');
if (createForm) {
    createForm.addEventListener('submit', async function(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const data = {
            username: formData.get('username'),
            password: formData.get('password'),
            role: 'system_admin'
        };
        
        try {
            const response = await fetch(`${API_BASE_URL}/hierarchy/create-system-admin`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            
            if (result.success) {
                alert('System admin created successfully!');
                closeCreateModal();
                loadSystemAdmins();
            } else {
                alert('Error: ' + result.message);
            }
        } catch (error) {
            console.error('Error creating system admin:', error);
            alert('Error creating system admin');
        }
    });
}

async function deleteSystemAdmin(adminId, username) {
    if (!confirm(`Delete system admin "${username}"?\n\nThis will fail if they have assigned owners.`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/hierarchy/system-admin/${adminId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('System admin deleted successfully!');
            loadSystemAdmins();
        } else {
            alert('Error: ' + result.message);
        }
    } catch (error) {
        console.error('Error deleting system admin:', error);
        alert('Error deleting system admin');
    }
}