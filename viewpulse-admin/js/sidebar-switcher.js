// Run immediately
(function() {
    const adminRole = localStorage.getItem('adminRole');
    const selectedDevice = JSON.parse(localStorage.getItem('selectedDevice'));
    
    // Check if we are in "Device View Mode" (Super/System Admin + Selected Device)
    const isFeedbackPage = window.location.href.includes('feedback.html');
    const isSchedulePage = window.location.href.includes('ad-schedule.html');
    const isVideosPage = window.location.href.includes('videos.html'); 
    
    if ((adminRole === 'super_admin' || adminRole === 'system_admin') && selectedDevice && (isFeedbackPage || isSchedulePage || isVideosPage)) {
        
        const sidebarNav = document.querySelector('.sidebar-nav');
        if (!sidebarNav) return;

        // 1. Update Breadcrumb
        const breadcrumb = document.querySelector('.breadcrumb');
        if (breadcrumb) {
            const owner = JSON.parse(localStorage.getItem('selectedOwner'));
            const ownerName = owner ? owner.username : '...';
            breadcrumb.textContent = `... > Owner: ${ownerName} > Device: ${selectedDevice.device_code}`;
        }

        // 2. Rewrite Sidebar
        sidebarNav.innerHTML = `
            <div style="padding:15px 20px; border-bottom:1px solid rgba(255,255,255,0.1); margin-bottom:10px;">
                <small style="color:#9ca3af; display:block; font-size:10px; text-transform:uppercase;">Managing Device</small>
                <span style="color:white; font-weight:600;">${selectedDevice.device_code}</span>
            </div>

            <a href="feedback.html" class="nav-item ${isFeedbackPage ? 'active' : ''}">
                <span class="icon">💬</span> Feedbacks
            </a>
            
            <a href="ad-schedule.html" class="nav-item ${isSchedulePage ? 'active' : ''}">
                <span class="icon">📅</span> Schedule
            </a>

            <a href="videos.html" class="nav-item ${isVideosPage ? 'active' : ''}">
                <span class="icon">🎬</span> Videos
            </a>

            <a href="devices.html" class="nav-item" onclick="exitDeviceView()">
                <span class="icon">⬅️</span> Back to Devices
            </a>

            <a href="#" onclick="logout()" class="nav-item logout">
                <span class="icon">🚪</span> Logout
            </a>
        `;
    }
})();

function exitDeviceView() {
    // Clear the device selection so we go back to "List Mode"
    localStorage.removeItem('selectedDevice');
    localStorage.removeItem('filterDeviceId');
}