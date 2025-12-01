// Check authentication
if (!checkDeviceAuth()) {
    // Will redirect to login if not authenticated
}

let videoList = [];
let currentVideoIndex = 0;
const videoPlayer = document.getElementById('videoPlayer');

// Load and play videos
async function loadVideos() {
    try {
        const data = await fetchVideos();
        
        if (data.success && data.videos && data.videos.length > 0) {
            videoList = data.videos;
            console.log('Loaded ' + videoList.length + ' videos');
            playVideoLoop();
        } else {
            console.error('No videos found');
            videoPlayer.innerHTML = '<div style="color:white; text-align:center; padding:50px;">No videos available</div>';
        }
    } catch (error) {
        console.error('Error loading videos:', error);
        videoPlayer.innerHTML = '<div style="color:white; text-align:center; padding:50px;">Error loading videos</div>';
    }
}

// Play videos in loop
function playVideoLoop() {
    if (!videoList || videoList.length === 0) {
        return;
    }
    
    currentVideoIndex = 0;
    playVideo(currentVideoIndex);
    
    // When video ends, play next
    videoPlayer.addEventListener('ended', function() {
        currentVideoIndex = (currentVideoIndex + 1) % videoList.length;
        playVideo(currentVideoIndex);
    });
}

// Play specific video
function playVideo(index) {
    const video = videoList[index];
    
    if (!video) {
        return;
    }
    
    console.log('Playing video:', video.videoTitle);
    
    // --- FIX STARTS HERE ---
    // define the Backend URL manually since API_BASE_URL includes '/api'
    const BACKEND_URL = 'http://13.232.75.118:8080'; 
    
    // Construct the full path: Backend + Folder + Filename
    videoPlayer.src = `${BACKEND_URL}/uploads/videos/${video.videoPath}`;
    // --- FIX ENDS HERE ---
    
    videoPlayer.load();
    videoPlayer.play().catch(error => {
        console.error('Error playing video:', error);
    });
}
// Go to feedback page
function goToFeedback() {
    window.location.href = 'feedback.html';
}

// Logout modal functions
function showLogoutModal() {
    document.getElementById('logoutModal').style.display = 'flex';
}

function hideLogoutModal() {
    document.getElementById('logoutModal').style.display = 'none';
}

// Initialize
window.addEventListener('load', function() {
    loadVideos();
    
    // Refresh video list every 5 minutes
    setInterval(loadVideos, 5 * 60 * 1000);
});

// Make video fullscreen on click (optional)
videoPlayer.addEventListener('click', function() {
    if (videoPlayer.requestFullscreen) {
        videoPlayer.requestFullscreen();
    } else if (videoPlayer.webkitRequestFullscreen) {
        videoPlayer.webkitRequestFullscreen();
    } else if (videoPlayer.mozRequestFullScreen) {
        videoPlayer.mozRequestFullScreen();
    }
});
