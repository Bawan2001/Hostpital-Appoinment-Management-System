// ==========================================
// Student 1 (Gateway Lead & User/Auth Service) Client Logic
// Requests route through Central API Gateway (Port 8080)
// ==========================================

const GATEWAY = 'http://localhost:8080';
let jwtToken = localStorage.getItem('jwt_token') || '';
let currentUser = null;

// Toast container
document.body.insertAdjacentHTML('beforeend', '<div id="toast-container"></div>');

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

function setAuthHeaders(includeBearer = true) {
    const headers = { 'Content-Type': 'application/json' };
    if (includeBearer && jwtToken) headers['Authorization'] = `Bearer ${jwtToken}`;
    return headers;
}

async function apiCall(url, options = {}) {
    try {
        const res = await fetch(url, options);
        const text = await res.text();
        let data;
        try { data = JSON.parse(text); } catch { data = text; }
        if (!res.ok) throw new Error(data?.message || data || `HTTP ${res.status}`);
        return data;
    } catch (err) {
        throw err;
    }
}

function openModal(id) { document.getElementById(id).classList.add('active'); }
function closeModal(id) { document.getElementById(id).classList.remove('active'); }

function switchTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    if (event && event.target) {
        event.target.classList.add('active');
    }
}

function updateAuthUI() {
    const badge = document.getElementById('connectionBadge');
    const userInfo = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtnModal');
    const registerBtn = document.getElementById('registerBtnModal');
    const logoutBtn = document.getElementById('logoutBtn');
    const tokenDisplay = document.getElementById('tokenDisplay');

    if (jwtToken && currentUser) {
        badge.textContent = 'Connected';
        badge.className = 'status-badge connected';
        userInfo.textContent = `${currentUser.name} (${currentUser.role})`;
        loginBtn.classList.add('hidden');
        registerBtn.classList.add('hidden');
        logoutBtn.classList.remove('hidden');
        if (tokenDisplay) tokenDisplay.value = jwtToken;
    } else {
        badge.textContent = 'Disconnected';
        badge.className = 'status-badge disconnected';
        userInfo.textContent = 'Not logged in';
        loginBtn.classList.remove('hidden');
        registerBtn.classList.remove('hidden');
        logoutBtn.classList.add('hidden');
        if (tokenDisplay) tokenDisplay.value = '';
    }
}

function logout() {
    jwtToken = '';
    currentUser = null;
    localStorage.removeItem('jwt_token');
    updateAuthUI();
    showToast('Logged out successfully.', 'info');
}

// ==========================================
// STUDENT 1: AUTH & GATEWAY ENDPOINTS
// ==========================================

async function login() {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();
    if (!email || !password) return showToast('Please enter email and password.', 'error');
    try {
        const data = await apiCall(`${GATEWAY}/api/v1/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        jwtToken = data.access_token;
        localStorage.setItem('jwt_token', jwtToken);
        currentUser = { name: data.name, role: data.role, id: data.id, email: data.email };
        closeModal('loginModal');
        updateAuthUI();
        showToast(`Authentication Successful! Logged in as ${data.name} (${data.role})`, 'success');
        
        renderOutput('authResult', 'POST /api/v1/auth/login Response:', data);
    } catch (err) {
        showToast(`Login failed: ${err.message}`, 'error');
    }
}

async function register() {
    const name = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value.trim();
    const phone = (document.getElementById('regPhone')?.value || '+94771234567').trim();
    const role = document.getElementById('regRole').value;
    if (!name || !email || !password) return showToast('Name, email, and password are required.', 'error');
    try {
        const data = await apiCall(`${GATEWAY}/api/v1/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, phone, role })
        });
        jwtToken = data.access_token;
        localStorage.setItem('jwt_token', jwtToken);
        currentUser = { name: data.name, role: data.role, id: data.id, email: data.email };
        closeModal('registerModal');
        updateAuthUI();
        showToast(`Registration Successful! User created with ID: ${data.id}`, 'success');

        renderOutput('authResult', 'POST /api/v1/auth/register Response:', data);
    } catch (err) {
        showToast(`Registration failed: ${err.message}`, 'error');
    }
}

async function validateToken() {
    if (!jwtToken) return showToast('No token available. Please login or register first.', 'error');
    try {
        const isValid = await apiCall(`${GATEWAY}/api/v1/auth/validate?token=${jwtToken}`);
        const type = isValid === true ? 'success' : 'error';
        showToast(isValid === true ? 'JWT Token Signature & Expiry: VALID' : 'JWT Token: INVALID', type);
        renderOutput('authResult', 'GET /api/v1/auth/validate Result:', { isValid, token: jwtToken.substring(0, 30) + '...' });
    } catch (err) {
        showToast(`Validation check error: ${err.message}`, 'error');
    }
}

async function getUserProfile() {
    if (!currentUser || !currentUser.id) return showToast('Please login first to get User ID.', 'error');
    try {
        const user = await apiCall(`${GATEWAY}/api/v1/auth/user/${currentUser.id}`);
        showToast(`User Profile Loaded for ${user.name}`, 'success');
        renderOutput('authResult', `GET /api/v1/auth/user/${currentUser.id} Response:`, user);
    } catch (err) {
        showToast(`Failed to load profile: ${err.message}`, 'error');
    }
}

async function requestOAuthToken() {
    const grantType = document.getElementById('oauthGrantType').value;
    const username = document.getElementById('oauthUsername').value.trim();
    const password = document.getElementById('oauthPassword').value.trim();
    if (!username || !password) return showToast('Username and password are required for ROPC grant.', 'error');

    try {
        const data = await apiCall(`${GATEWAY}/api/v1/auth/oauth/token`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ grant_type: grantType, username, password })
        });
        showToast('OAuth 2.0 Access Token Granted!', 'success');
        renderOutput('oauthResult', 'POST /api/v1/auth/oauth/token Response (RFC 6749):', data);
    } catch (err) {
        showToast(`OAuth 2.0 Token Error: ${err.message}`, 'error');
    }
}

async function testRateLimit() {
    try {
        const start = Date.now();
        const data = await apiCall(`${GATEWAY}/api/v1/auth/validate?token=test-rate-limit`);
        const duration = Date.now() - start;
        showToast(`Rate Limit Test Request Allowed (${duration}ms)`, 'info');
        appendLog('rateLimitLog', `[SUCCESS 200] Request accepted in ${duration}ms`);
    } catch (err) {
        showToast(`Rate limit response: ${err.message}`, 'warning');
        appendLog('rateLimitLog', `[RESPONSE] ${err.message}`);
    }
}

async function spamRateLimit() {
    showToast('Dispatching 12 rapid requests to test 10 req/min Gateway Rate Limiter...', 'warning');
    const container = document.getElementById('rateLimitLog');
    container.innerHTML = '<p><em>Firing 12 concurrent requests...</em></p>';

    for (let i = 1; i <= 12; i++) {
        try {
            await apiCall(`${GATEWAY}/api/v1/auth/validate?token=rate-limit-test-${i}`);
            appendLog('rateLimitLog', `#${i}: HTTP 200 OK - Allowed by Gateway`);
        } catch (err) {
            appendLog('rateLimitLog', `#${i}: ${err.message} (Gateway Rate Limiter Blocked)`);
        }
    }
}

async function checkGatewayHealth() {
    try {
        const start = Date.now();
        const res = await fetch(`${GATEWAY}/api/v1/auth/validate?token=health-check`);
        const duration = Date.now() - start;
        renderOutput('gatewayHealth', 'Gateway Health Check Status:', {
            gatewayUrl: GATEWAY,
            status: res.status,
            statusText: res.statusText,
            responseTime: `${duration}ms`,
            timestamp: new Date().toISOString()
        });
        showToast(`Gateway pinged successfully (${duration}ms)`, 'success');
    } catch (err) {
        showToast(`Gateway connection error: ${err.message}`, 'error');
        renderOutput('gatewayHealth', 'Gateway Connection Error:', { error: err.message });
    }
}

function renderOutput(elementId, title, data) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.innerHTML = `
        <div style="background: #1e293b; color: #f8fafc; padding: 1rem; border-radius: 8px; font-family: monospace; overflow-x: auto;">
            <strong style="color: #38bdf8;">${title}</strong>
            <pre style="margin-top: 0.5rem; margin-bottom: 0;">${JSON.stringify(data, null, 2)}</pre>
        </div>
    `;
}

function appendLog(elementId, text) {
    const el = document.getElementById(elementId);
    if (!el) return;
    const p = document.createElement('p');
    p.style.fontFamily = 'monospace';
    p.style.fontSize = '0.85rem';
    p.style.margin = '0.25rem 0';
    if (text.includes('429') || text.includes('Blocked') || text.includes('Too Many')) {
        p.style.color = '#f87171'; // red
    } else {
        p.style.color = '#4ade80'; // green
    }
    p.textContent = text;
    el.appendChild(p);
}

// Global modal background click close
window.onclick = (e) => {
    if (e.target.classList.contains('modal')) {
        document.querySelectorAll('.modal').forEach(m => m.classList.remove('active'));
    }
};

document.addEventListener('DOMContentLoaded', () => {
    updateAuthUI();
    if (jwtToken) showToast('Previous session JWT token loaded.', 'info');
});
