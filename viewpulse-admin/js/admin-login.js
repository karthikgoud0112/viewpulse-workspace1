

document.getElementById('adminLoginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const messageEl = document.getElementById('loginMessage');
    
    messageEl.textContent = 'Logging in...';
    messageEl.className = 'message';
    
    try {
        const response = await fetch(`${API_BASE_URL}/admin/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
		if (data.success) {
		            localStorage.setItem('adminLoggedIn', 'true');
		            localStorage.setItem('adminUsername', data.username);
		            localStorage.setItem('adminRole', data.role);
		            localStorage.setItem('adminId', data.admin_id);
		            if (data.location_id) {
		                localStorage.setItem('adminLocationId', data.location_id);
		            }
		            
		            messageEl.textContent = 'Login successful! Redirecting...';
		            messageEl.className = 'message success';
		            
		            // --- FIX: Route based on Role ---
		            setTimeout(() => {
		                if (data.role === 'super_admin') {
		                    window.location.href = 'super-admin-dashboard.html';
		                } else if (data.role === 'system_admin') {
		                    window.location.href = 'system-admin-dashboard.html';
		                } else {
		                    // Location Owners go here
		                    window.location.href = 'dashboard.html'; 
		                }
		            }, 1000);
		            // --------------------------------
		            
		        } else  {
            messageEl.textContent = '❌ ' + (data.message || 'Login failed');
            messageEl.className = 'message error';
        }
    } catch (error) {
        messageEl.textContent = '❌ Connection error: ' + error.message;
        messageEl.className = 'message error';
    }
});
