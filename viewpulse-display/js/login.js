// Redirect if already logged in
if (isDeviceLoggedIn()) {
    window.location.href = 'ads.html';
}

// Handle login form submission
document.getElementById('deviceLoginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const deviceCode = document.getElementById('deviceCode').value.trim();
    const devicePassword = document.getElementById('devicePassword').value.trim();
    const messageEl = document.getElementById('loginMessage');
    
    // Validation
    if (!deviceCode || !devicePassword) {
        messageEl.textContent = '❌ Please fill in all fields';
        messageEl.className = 'error';
        return;
    }
    
    // Show loading
    messageEl.textContent = 'Logging in...';
    messageEl.className = '';
    messageEl.style.display = 'block';
    
    try {
        const response = await fetch(`${API_BASE_URL}/device/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                device_code: deviceCode,
                device_password: devicePassword
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            // Save to localStorage
            localStorage.setItem('deviceLoggedIn', 'true');
            localStorage.setItem('deviceId', data.device_id);
            localStorage.setItem('deviceCode', deviceCode);
            localStorage.setItem('deviceLocationId', data.location_id);
            
            // Show success message
            messageEl.textContent = '✅ Login successful! Redirecting...';
            messageEl.className = 'success';
            
            // Redirect to ads page
            setTimeout(() => {
                window.location.href = 'ads.html';
            }, 1000);
        } else {
            messageEl.textContent = '❌ ' + (data.message || 'Invalid credentials');
            messageEl.className = 'error';
        }
    } catch (error) {
        console.error('Login error:', error);
        messageEl.textContent = '❌ Connection error. Please check if backend is running.';
        messageEl.className = 'error';
    }
});
