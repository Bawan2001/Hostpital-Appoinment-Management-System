/* ============================================================
   MediCare Pro — Production JavaScript
   ============================================================ */

const API_BASE = 'http://localhost:8080/api/v1';
let authToken = null;
let currentUser = null;

// ── JWT Utilities ─────────────────────────────────────────────
/**
 * Decode a JWT payload without verifying the signature.
 * Signature verification MUST be done server-side by the Gateway.
 * Client-side decoding is used only for UI personalization.
 */
function decodeJwtPayload(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return null;
        // Base64Url → Base64 → decode
        const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
        const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
        const json = atob(padded);
        return JSON.parse(json);
    } catch {
        return null;
    }
}

/**
 * Extract a normalized role string from JWT claims.
 * Handles common claim formats: 'role', 'roles', 'authorities', 'scope'.
 */
function extractRoleFromToken(token) {
    if (!token) return null;
    const payload = decodeJwtPayload(token);
    if (!payload) return null;

    // Try various common claim names
    const raw = payload.role || payload.roles || payload.authority ||
                payload.authorities || payload.scope || null;

    if (!raw) return null;

    // Normalize to uppercase string — handle arrays, space/comma-separated strings
    let normalized;
    if (Array.isArray(raw)) {
        normalized = raw[0];
    } else if (typeof raw === 'string') {
        normalized = raw.split(/[,\s]+/)[0];
    } else {
        normalized = String(raw);
    }

    // Strip 'ROLE_' prefix if present
    return normalized.replace(/^ROLE_/, '').toUpperCase();
}

// ── RBAC ──────────────────────────────────────────────────────
// Pages accessible without any login
const PUBLIC_PAGES = [];

// Role hierarchy: what each role can see
const ROLE_CONFIG = {
    ADMIN: {
        label: 'Administrator',
        welcome: 'Full system access — manage all microservices, users, and system health.',
        icon: '🛡️',
        avatarClass: 'avatar-admin',
    },
    DOCTOR: {
        label: 'Doctor',
        welcome: 'View your scheduled appointments and patient notifications.',
        icon: '🩺',
        avatarClass: 'avatar-doctor',
    },
    PATIENT: {
        label: 'Patient',
        welcome: 'Book and track your appointments. View your notifications.',
        icon: '🏥',
        avatarClass: 'avatar-patient',
    },
};

/**
 * Apply role-based UI permissions.
 * Hides nav items and dashboard cards that don't match the current user's role.
 * If no user is logged in, only public items remain visible.
 */
function applyRolePermissions() {
    const role = currentUser?.role || null;

    // ── Nav items
    document.querySelectorAll('.nav-item[data-roles]').forEach(el => {
        const allowed = el.dataset.roles.split(',').map(r => r.trim().toUpperCase());
        if (!role || !allowed.includes(role)) {
            el.style.display = 'none';
        } else {
            el.style.display = '';
        }
    });

    // ── Admin-only dashboard card
    const archCard = document.getElementById('archCard');
    if (archCard) {
        archCard.style.display = (role === 'ADMIN') ? '' : 'none';
    }

    // ── Role badge in topbar
    const roleBadge = document.getElementById('roleBadge');
    if (roleBadge) {
        if (role && ROLE_CONFIG[role]) {
            roleBadge.textContent = ROLE_CONFIG[role].label;
            roleBadge.className = `role-badge role-badge-${role.toLowerCase()}`;
            roleBadge.classList.remove('hidden');
        } else {
            roleBadge.classList.add('hidden');
        }
    }

    // ── Welcome banner
    const banner   = document.getElementById('welcomeBanner');
    const wIcon    = document.getElementById('welcomeIcon');
    const wTitle   = document.getElementById('welcomeTitle');
    const wSub     = document.getElementById('welcomeSub');

    if (banner && role && ROLE_CONFIG[role]) {
        const cfg = ROLE_CONFIG[role];
        wIcon.textContent  = cfg.icon;
        wTitle.textContent = `Welcome, ${currentUser?.name || currentUser?.email || 'User'}`;
        wSub.textContent   = cfg.welcome;
        banner.style.display = '';
    } else if (banner) {
        banner.style.display = 'none';
    }

    // ── If current page is no longer accessible, redirect to dashboard
    _enforcePageAccess(role);
}

/**
 * Guard: if the user is on a page they no longer have access to, navigate away.
 */
function _enforcePageAccess(role) {
    const activePage = document.querySelector('.page.active');
    if (!activePage) return;
    const pageId = activePage.id.replace('page-', '');
    if (pageId === 'dashboard') return; // Dashboard always accessible

    const navEl = document.getElementById(`nav-${pageId}`);
    if (!navEl) return;
    const allowed = (navEl.dataset.roles || '').split(',').map(r => r.trim().toUpperCase());
    if (!role || !allowed.includes(role)) {
        navigateTo('dashboard');
    }
}

/**
 * Override navigateTo with role-check gate.
 * Prevents direct navigation to pages the current user's role can't access.
 */
const _originalNavigateTo = navigateTo;
// Will be overridden after function definitions — see bottom of file.

// ── Utility: Toast Notifications ─────────────────────────────
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span class="toast-dot"></span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('toast-fade');
        setTimeout(() => toast.remove(), 350);
    }, 3500);
}

// ── Utility: API Request ──────────────────────────────────────
async function apiRequest(method, url, body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (authToken) headers['Authorization'] = `Bearer ${authToken}`;

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    try {
        const res = await fetch(url, options);
        const text = await res.text();
        let data;
        try { data = JSON.parse(text); } catch { data = text; }

        if (!res.ok) {
            const msg = (data && (data.message || data.error)) || `HTTP ${res.status}`;
            throw new Error(msg);
        }
        return data;
    } catch (e) {
        if (e.name === 'TypeError') throw new Error('Cannot connect to API Gateway. Is Docker running?');
        throw e;
    }
}

// ── Utility: Build Table ──────────────────────────────────────
function buildTable(columns, rows, emptyText = 'No records found') {
    if (!rows || rows.length === 0) {
        return `<div class="empty-state"><p>${emptyText}</p></div>`;
    }
    const thead = `<thead><tr>${columns.map(c => `<th>${c.label}</th>`).join('')}</tr></thead>`;
    const tbody = `<tbody>${rows.map(row =>
        `<tr>${columns.map(c => `<td>${c.render ? c.render(row) : (row[c.key] ?? '—')}</td>`).join('')}</tr>`
    ).join('')}</tbody>`;
    return `<table>${thead}${tbody}</table>`;
}

function statusBadge(status) {
    const map = {
        SCHEDULED: 'badge-blue', CONFIRMED: 'badge-blue',
        COMPLETED: 'badge-green',
        CANCELLED: 'badge-red', CANCELED: 'badge-red',
        PENDING: 'badge-amber',
        SENT: 'badge-green', READ: 'badge-gray',
        ACTIVE: 'badge-green', INACTIVE: 'badge-gray',
    };
    const cls = map[(status || '').toUpperCase()] || 'badge-gray';
    return `<span class="badge ${cls}">${status || '—'}</span>`;
}

function formatDate(val) {
    if (!val) return '—';
    try { return new Date(val).toLocaleDateString('en-GB', { day:'2-digit', month:'short', year:'numeric' }); }
    catch { return val; }
}

// ── Navigation ───────────────────────────────────────────────
const PAGE_TITLES = {
    dashboard: 'Dashboard',
    patients: 'Patient Management',
    doctors: 'Doctor Management',
    appointments: 'Appointment Booking',
    notifications: 'Notifications',
    auth: 'Auth & Security',
    gateway: 'Gateway & API Docs',
};

function navigateTo(name) {
    // Deactivate all pages
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    const page = document.getElementById(`page-${name}`);
    const navBtn = document.getElementById(`nav-${name}`);
    if (page) page.classList.add('active');
    if (navBtn) navBtn.classList.add('active');

    document.getElementById('topbarTitle').textContent = PAGE_TITLES[name] || name;

    // Close sidebar on mobile
    if (window.innerWidth <= 768) {
        document.getElementById('sidebar').classList.remove('open');
    }
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('open');
}

// ── Connection Health Check ───────────────────────────────────
async function checkConnection() {
    const dot = document.getElementById('connDot');
    const label = document.getElementById('connLabel');
    dot.className = 'conn-dot checking';
    label.textContent = 'Checking...';
    try {
        await fetch('http://localhost:8080/actuator/health', { signal: AbortSignal.timeout(3000) });
        dot.className = 'conn-dot connected';
        label.textContent = 'Gateway Connected';
    } catch {
        dot.className = 'conn-dot disconnected';
        label.textContent = 'Gateway Offline';
    }
}

// ── Auth ──────────────────────────────────────────────────────
async function login() {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    if (!email || !password) { showToast('Please fill in all fields', 'error'); return; }
    try {
        const data = await apiRequest('POST', `${API_BASE}/auth/login`, { email, password });
        authToken = data.token || data.accessToken || data.access_token;

        // Extract role from JWT payload
        const role = extractRoleFromToken(authToken);

        currentUser = {
            id: data.id,
            name: data.name || email.split('@')[0],
            email: data.email || email,
            role: role || (data.role ? data.role.replace(/^ROLE_/, '').toUpperCase() : null),
        };

        // Fetch linked Patient or Doctor entity by email for ID linking
        await fetchLinkedEntity();

        updateUserUI();
        applyRolePermissions();
        closeModal('loginModal');
        showToast(`Welcome, ${currentUser.name}! Signed in as ${ROLE_CONFIG[currentUser.role]?.label || 'User'} ✓`, 'success');
        if (authToken) document.getElementById('tokenDisplay').value = authToken;
        refreshDashboard();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function fetchLinkedEntity() {
    if (!currentUser || !currentUser.email) return;
    try {
        if (currentUser.role === 'PATIENT') {
            const patient = await apiRequest('GET', `${API_BASE}/patients/email/${encodeURIComponent(currentUser.email)}`);
            if (patient && patient.id) {
                currentUser.patientId = patient.id;
                currentUser.patientName = patient.name;
            }
        } else if (currentUser.role === 'DOCTOR') {
            const doctor = await apiRequest('GET', `${API_BASE}/doctors/email/${encodeURIComponent(currentUser.email)}`);
            if (doctor && doctor.id) {
                currentUser.doctorId = doctor.id;
                currentUser.doctorName = doctor.name;
            }
        }
    } catch (e) {
        // Profile may not exist yet — not a blocking error
    }
}

// Switch between Login and Register modals
function switchModal(closeId, openId) {
    closeModal(closeId);
    setTimeout(() => openModal(openId), 150);
    return false;
}

// Role toggle handler in Register modal
function selectRegisterRole(role) {
    document.getElementById('regRole').value = role;
    ['Patient', 'Doctor', 'Admin'].forEach(r => {
        const btn = document.getElementById(`roleToggle${r}`);
        if (btn) btn.classList.toggle('active', r.toUpperCase() === role);
    });
    const pf = document.getElementById('patientFields');
    const df = document.getElementById('doctorFields');
    if (pf) pf.style.display = (role === 'PATIENT') ? '' : 'none';
    if (df) df.style.display = (role === 'DOCTOR') ? '' : 'none';
}

async function register() {
    const role = document.getElementById('regRole').value;
    const body = {
        name:     document.getElementById('regName').value.trim(),
        email:    document.getElementById('regEmail').value.trim(),
        password: document.getElementById('regPassword').value,
        phone:    document.getElementById('regPhone').value.trim() || '+94771234567',
        role:     role,
    };
    if (!body.name || !body.email || !body.password) { showToast('Please fill in required fields', 'error'); return; }
    if (body.password.length < 6) { showToast('Password must be at least 6 characters', 'error'); return; }

    if (role === 'PATIENT') {
        body.age        = parseInt(document.getElementById('regAge').value) || 30;
        body.gender     = document.getElementById('regGender').value || 'OTHER';
        body.bloodGroup = document.getElementById('regBloodGroup').value || 'O_POSITIVE';
        body.address    = document.getElementById('regAddress').value.trim() || 'Colombo, Sri Lanka';
    } else if (role === 'DOCTOR') {
        body.specialty       = document.getElementById('regSpecialty').value.trim() || 'General Medicine';
        body.experienceYears = parseInt(document.getElementById('regExpYears').value) || 5;
        body.consultationFee = parseFloat(document.getElementById('regFee').value) || 3000;
        body.hospitalName    = document.getElementById('regHospital').value.trim() || 'National Hospital Colombo';
        body.availableDays   = ['Monday', 'Wednesday', 'Friday'];
    }

    try {
        const data = await apiRequest('POST', `${API_BASE}/auth/register`, body);
        closeModal('registerModal');
        showToast(`Account created! Welcome, ${body.name}. Please sign in.`, 'success');
        // Pre-fill login modal with new email
        document.getElementById('loginEmail').value = body.email;
        document.getElementById('loginPassword').value = body.password;
        setTimeout(() => openModal('loginModal'), 400);
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function validateToken() {
    if (!authToken) { showToast('No active token. Please login first.', 'error'); return; }
    try {
        const data = await apiRequest('GET', `${API_BASE}/auth/validate?token=${authToken}`);
        document.getElementById('authResult').textContent = JSON.stringify(data, null, 2);
        showToast('Token is valid ✓', 'success');
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function getUserProfile() {
    if (!authToken || !currentUser?.id) { showToast('Please login first', 'error'); return; }
    try {
        const data = await apiRequest('GET', `${API_BASE}/auth/user/${currentUser.id}`);
        document.getElementById('authResult').textContent = JSON.stringify(data, null, 2);
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function requestOAuthToken() {
    const username = document.getElementById('oauthUsername').value.trim();
    const password = document.getElementById('oauthPassword').value;
    try {
        const data = await apiRequest('POST', `${API_BASE}/auth/oauth/token`, {
            grant_type: 'password', username, password,
            client_id: 'hospital-client', client_secret: 'hospital-secret',
        });
        authToken = data.access_token || data.token;
        updateUserUI();
        document.getElementById('tokenDisplay').value = authToken || '';
        showToast('OAuth token obtained!', 'success');
    } catch (e) {
        showToast(e.message, 'error');
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    updateUserUI();
    applyRolePermissions(); // Re-apply restrictions (hides admin-only items)
    navigateTo('dashboard'); // Return to dashboard
    const tokenDisplay = document.getElementById('tokenDisplay');
    if (tokenDisplay) tokenDisplay.value = '';
    showToast('Logged out successfully.', 'info');
    resetDashboardStats();
}

function updateUserUI() {
    const loginBtn    = document.getElementById('topLoginBtn');
    const registerBtn = document.getElementById('topRegisterBtn');
    const logoutBtn   = document.getElementById('topLogoutBtn');
    const chip        = document.getElementById('userChipName');
    const avatar      = document.getElementById('userAvatar');

    // Clear any previous role color class
    if (avatar) {
        avatar.classList.remove('avatar-admin', 'avatar-doctor', 'avatar-patient');
    }

    if (authToken) {
        if (loginBtn)    loginBtn.classList.add('hidden');
        if (registerBtn) registerBtn.classList.add('hidden');
        if (logoutBtn)   logoutBtn.classList.remove('hidden');
        const name = currentUser?.name || currentUser?.email || 'User';
        chip.textContent = name;
        avatar.textContent = name.charAt(0).toUpperCase();
        // Apply role-specific avatar color
        const role = currentUser?.role;
        if (role && ROLE_CONFIG[role]) {
            avatar.classList.add(ROLE_CONFIG[role].avatarClass);
        }
    } else {
        if (loginBtn)    loginBtn.classList.remove('hidden');
        if (registerBtn) registerBtn.classList.remove('hidden');
        if (logoutBtn)   logoutBtn.classList.add('hidden');
        chip.textContent = 'Not logged in';
        avatar.textContent = '?';
    }
}



// ── Dashboard ─────────────────────────────────────────────────
async function refreshDashboard() {
    checkConnection();
    await Promise.allSettled([
        loadStatPatients(),
        loadStatDoctors(),
        loadStatAppointments(),
        loadStatNotifications(),
        loadDashboardAppointments(),
    ]);
}

function resetDashboardStats() {
    ['statPatients','statDoctors','statAppointments','statNotifications'].forEach(id => {
        document.getElementById(id).textContent = '—';
    });
    ['patientsBadge','doctorsBadge','appointmentsBadge','notifBadge'].forEach(id => {
        document.getElementById(id).textContent = '—';
    });
    document.getElementById('dashboardAppointments').innerHTML = '<div class="empty-state"><p>Login and refresh to load appointment data</p></div>';
}

async function loadStatPatients() {
    try {
        const data = await apiRequest('GET', `${API_BASE}/patients`);
        const count = Array.isArray(data) ? data.length : (data.content?.length ?? '?');
        document.getElementById('statPatients').textContent = count;
        document.getElementById('patientsBadge').textContent = count;
    } catch { document.getElementById('statPatients').textContent = 'N/A'; }
}
async function loadStatDoctors() {
    try {
        const data = await apiRequest('GET', `${API_BASE}/doctors`);
        const count = Array.isArray(data) ? data.length : (data.content?.length ?? '?');
        document.getElementById('statDoctors').textContent = count;
        document.getElementById('doctorsBadge').textContent = count;
    } catch { document.getElementById('statDoctors').textContent = 'N/A'; }
}
async function loadStatAppointments() {
    try {
        const data = await apiRequest('GET', `${API_BASE}/appointments`);
        const count = Array.isArray(data) ? data.length : (data.content?.length ?? '?');
        document.getElementById('statAppointments').textContent = count;
        document.getElementById('appointmentsBadge').textContent = count;
    } catch { document.getElementById('statAppointments').textContent = 'N/A'; }
}
async function loadStatNotifications() {
    try {
        const data = await apiRequest('GET', `${API_BASE}/notifications`);
        const count = Array.isArray(data) ? data.length : (data.content?.length ?? '?');
        document.getElementById('statNotifications').textContent = count;
        document.getElementById('notifBadge').textContent = count;
    } catch { document.getElementById('statNotifications').textContent = 'N/A'; }
}

async function loadDashboardAppointments() {
    const el = document.getElementById('dashboardAppointments');
    try {
        const data = await apiRequest('GET', `${API_BASE}/appointments`);
        const list = Array.isArray(data) ? data : (data.content || []);
        const recent = list.slice(0, 8);
        el.innerHTML = buildTable([
            { key: 'patientName', label: 'Patient' },
            { key: 'doctorName',  label: 'Doctor' },
            { key: 'appointmentDate', label: 'Date', render: r => formatDate(r.appointmentDate) },
            { key: 'slotTime',    label: 'Time' },
            { key: 'status',      label: 'Status', render: r => statusBadge(r.status) },
        ], recent, 'No appointments found');
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><p>${e.message}</p></div>`;
    }
}

// ── Patients ──────────────────────────────────────────────────
async function loadAllPatients() {
    const el = document.getElementById('patientResult');
    el.innerHTML = '<div class="empty-state"><p>Loading...</p></div>';
    try {
        const data = await apiRequest('GET', `${API_BASE}/patients`);
        const list = Array.isArray(data) ? data : (data.content || []);
        el.innerHTML = buildTable([
            { key: 'name',        label: 'Name' },
            { key: 'email',       label: 'Email' },
            { key: 'age',         label: 'Age' },
            { key: 'gender',      label: 'Gender' },
            { key: 'bloodGroup',  label: 'Blood', render: r => `<span class="badge badge-blue">${(r.bloodGroup||'').replace('_','')}</span>` },
            { key: 'address',     label: 'Address' },
            { key: 'createdAt',   label: 'Registered', render: r => formatDate(r.createdAt) },
        ], list, 'No patients found. Add some!');
        document.getElementById('patientsBadge').textContent = list.length;
        document.getElementById('statPatients').textContent = list.length;
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><p>${e.message}</p></div>`;
        showToast(e.message, 'error');
    }
}

async function createPatient() {
    const body = {
        name:       document.getElementById('pName').value.trim(),
        email:      document.getElementById('pEmail').value.trim(),
        age:        parseInt(document.getElementById('pAge').value),
        gender:     document.getElementById('pGender').value,
        bloodGroup: document.getElementById('pBlood').value,
        address:    document.getElementById('pAddress').value.trim(),
    };
    if (!body.name || !body.email) { showToast('Name and email are required', 'error'); return; }
    try {
        await apiRequest('POST', `${API_BASE}/patients`, body);
        closeModal('createPatientModal');
        showToast('Patient registered!', 'success');
        loadAllPatients();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ── Doctors ───────────────────────────────────────────────────
async function loadAllDoctors() {
    const el = document.getElementById('doctorResult');
    el.innerHTML = '<div class="empty-state"><p>Loading...</p></div>';
    try {
        const data = await apiRequest('GET', `${API_BASE}/doctors`);
        const list = Array.isArray(data) ? data : (data.content || []);
        el.innerHTML = buildTable([
            { key: 'name',            label: 'Name' },
            { key: 'specialty',       label: 'Specialty' },
            { key: 'phone',           label: 'Phone' },
            { key: 'experienceYears', label: 'Exp (Yrs)' },
            { key: 'consultationFee', label: 'Fee (LKR)', render: r => `Rs. ${(r.consultationFee||0).toLocaleString()}` },
            { key: 'isAvailable',     label: 'Status', render: r => statusBadge(r.isAvailable ? 'ACTIVE' : 'INACTIVE') },
            { key: 'hospitalName',    label: 'Hospital' },
            { key: 'action',          label: 'Action', render: r => `
                <button class="btn-table-action" onclick="openBookForDoctor('${r.id}', '${(r.name||'').replace(/'/g, "\\'")}', ${r.consultationFee || 3000})">📅 Book</button>
            `},
        ], list, 'No doctors found. Add some!');
        document.getElementById('doctorsBadge').textContent = list.length;
        document.getElementById('statDoctors').textContent = list.length;
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><p>${e.message}</p></div>`;
        showToast(e.message, 'error');
    }
}

function openBookForDoctor(doctorId, doctorName, fee) {
    const docIdEl = document.getElementById('apptDoctorId');
    const docNameEl = document.getElementById('apptDoctorName');
    const feeEl = document.getElementById('apptFee');
    if (docIdEl) docIdEl.value = doctorId || '';
    if (docNameEl) docNameEl.value = doctorName || '';
    if (feeEl) feeEl.value = fee || 3000;

    if (currentUser && currentUser.role === 'PATIENT') {
        const pId = document.getElementById('apptPatientId');
        const pName = document.getElementById('apptPatientName');
        if (pId && (currentUser.patientId || currentUser.id)) pId.value = currentUser.patientId || currentUser.id;
        if (pName && (currentUser.patientName || currentUser.name)) pName.value = currentUser.patientName || currentUser.name;
    }

    openModal('bookAppointmentModal');
}

async function filterDoctorsBySpecialty() {
    const specialty = document.getElementById('specialtyFilter').value;
    const el = document.getElementById('doctorResult');
    el.innerHTML = '<div class="empty-state"><p>Loading...</p></div>';
    try {
        const url = specialty ? `${API_BASE}/doctors/specialty/${specialty}` : `${API_BASE}/doctors`;
        const data = await apiRequest('GET', url);
        const list = Array.isArray(data) ? data : (data.content || []);
        el.innerHTML = buildTable([
            { key: 'name',            label: 'Name' },
            { key: 'specialty',       label: 'Specialty' },
            { key: 'experienceYears', label: 'Exp (Yrs)' },
            { key: 'consultationFee', label: 'Fee (LKR)', render: r => `Rs. ${(r.consultationFee||0).toLocaleString()}` },
            { key: 'isAvailable',     label: 'Status', render: r => statusBadge(r.isAvailable ? 'ACTIVE' : 'INACTIVE') },
            { key: 'action',          label: 'Action', render: r => `
                <button class="btn-table-action" onclick="openBookForDoctor('${r.id}', '${(r.name||'').replace(/'/g, "\\'")}', ${r.consultationFee || 3000})">📅 Book</button>
            `},
        ], list, `No ${specialty || ''} doctors found`);
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function createDoctor() {
    const body = {
        name:            document.getElementById('dName').value.trim(),
        email:           document.getElementById('dEmail').value.trim(),
        phone:           document.getElementById('dPhone').value.trim(),
        specialty:       document.getElementById('dSpecialty').value.trim(),
        experienceYears: parseInt(document.getElementById('dExp').value),
        consultationFee: parseFloat(document.getElementById('dFee').value),
        hospitalName:    document.getElementById('dHospital').value.trim(),
        isAvailable:     true,
    };
    if (!body.name || !body.specialty) { showToast('Name and specialty are required', 'error'); return; }
    try {
        await apiRequest('POST', `${API_BASE}/doctors`, body);
        closeModal('createDoctorModal');
        showToast('Doctor added!', 'success');
        loadAllDoctors();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ── Appointments ──────────────────────────────────────────────
async function loadAllAppointments() {
    const el = document.getElementById('appointmentResult');
    el.innerHTML = '<div class="empty-state"><p>Loading...</p></div>';
    try {
        const data = await apiRequest('GET', `${API_BASE}/appointments`);
        let list = Array.isArray(data) ? data : (data.content || []);

        const isPatient = currentUser?.role === 'PATIENT';
        const isDoctor  = currentUser?.role === 'DOCTOR';

        el.innerHTML = buildTable([
            { key: 'patientName',     label: 'Patient' },
            { key: 'doctorName',      label: 'Doctor' },
            { key: 'appointmentDate', label: 'Date', render: r => formatDate(r.appointmentDate) },
            { key: 'slotTime',        label: 'Time' },
            { key: 'fee',             label: 'Fee (LKR)', render: r => `Rs. ${(r.fee||0).toLocaleString()}` },
            { key: 'status',          label: 'Status', render: r => statusBadge(r.status) },
            { key: 'notes',           label: 'Notes' },
            { key: 'action',          label: 'Actions', render: r => {
                const st = (r.status || '').toUpperCase();
                let btns = '';
                if (st === 'SCHEDULED' || st === 'BOOKED' || st === 'PENDING') {
                    if (isDoctor || currentUser?.role === 'ADMIN') {
                        btns += `<button class="btn-table-action btn-table-complete" onclick="updateAppointmentStatus('${r.id}', 'COMPLETED')">✓ Complete</button> `;
                    }
                    btns += `<button class="btn-table-action btn-table-cancel" onclick="cancelAppointmentRecord('${r.id}')">✕ Cancel</button>`;
                }
                return btns || '<span class="text-muted">—</span>';
            }},
        ], list, 'No appointments found');
        document.getElementById('appointmentsBadge').textContent = list.length;
        document.getElementById('statAppointments').textContent = list.length;
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><p>${e.message}</p></div>`;
        showToast(e.message, 'error');
    }
}

async function updateAppointmentStatus(id, newStatus) {
    try {
        await apiRequest('PUT', `${API_BASE}/appointments/${id}/status`, { status: newStatus });
        showToast(`Appointment status updated to ${newStatus}!`, 'success');
        loadAllAppointments();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function cancelAppointmentRecord(id) {
    if (!confirm('Are you sure you want to cancel this appointment?')) return;
    try {
        await apiRequest('PUT', `${API_BASE}/appointments/${id}/cancel`);
        showToast('Appointment cancelled successfully.', 'info');
        loadAllAppointments();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function bookAppointment() {
    const body = {
        patientId:       document.getElementById('apptPatientId').value.trim(),
        patientName:     document.getElementById('apptPatientName').value.trim(),
        doctorId:        document.getElementById('apptDoctorId').value.trim(),
        doctorName:      document.getElementById('apptDoctorName').value.trim(),
        appointmentDate: document.getElementById('apptDate').value,
        slotTime:        document.getElementById('apptTime').value.trim(),
        fee:             parseFloat(document.getElementById('apptFee').value),
        notes:           document.getElementById('apptNotes').value.trim(),
    };
    if (!body.patientId || !body.doctorId || !body.appointmentDate) {
        showToast('Patient ID, Doctor ID and Date are required', 'error'); return;
    }
    try {
        await apiRequest('POST', `${API_BASE}/appointments`, body);
        closeModal('bookAppointmentModal');
        showToast('Appointment booked!', 'success');
        loadAllAppointments();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ── Notifications ─────────────────────────────────────────────
async function loadAllNotifications() {
    const el = document.getElementById('notificationResult');
    el.innerHTML = '<div class="empty-state"><p>Loading...</p></div>';
    try {
        const data = await apiRequest('GET', `${API_BASE}/notifications`);
        const list = Array.isArray(data) ? data : (data.content || []);
        el.innerHTML = buildTable([
            { key: 'recipientEmail', label: 'Recipient Email' },
            { key: 'type',           label: 'Type',   render: r => statusBadge(r.type) },
            { key: 'subject',        label: 'Subject' },
            { key: 'message',        label: 'Message', render: r => `<span style="max-width:220px;display:inline-block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${r.message||'—'}</span>` },
            { key: 'status',         label: 'Status', render: r => statusBadge(r.status) },
            { key: 'createdAt',      label: 'Sent At', render: r => formatDate(r.createdAt) },
        ], list, 'No notifications found');
        document.getElementById('notifBadge').textContent = list.length;
        document.getElementById('statNotifications').textContent = list.length;
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><p>${e.message}</p></div>`;
        showToast(e.message, 'error');
    }
}

async function sendEmailNotification() {
    const body = {
        recipientId:    document.getElementById('notifRecipientId').value.trim(),
        recipientEmail: document.getElementById('notifEmail').value.trim(),
        subject:        document.getElementById('notifSubject').value.trim(),
        message:        document.getElementById('notifMessage').value.trim(),
        type:           'EMAIL',
    };
    if (!body.recipientEmail || !body.subject) { showToast('Email and subject are required', 'error'); return; }
    try {
        await apiRequest('POST', `${API_BASE}/notifications/email`, body);
        closeModal('sendEmailModal');
        showToast('Email notification sent!', 'success');
        loadAllNotifications();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function sendSmsNotification() {
    const body = {
        recipientId:    document.getElementById('smsRecipientId').value.trim(),
        recipientPhone: document.getElementById('smsPhone').value.trim(),
        message:        document.getElementById('smsMessage').value.trim(),
        type:           'SMS',
    };
    if (!body.recipientPhone || !body.message) { showToast('Phone and message are required', 'error'); return; }
    try {
        await apiRequest('POST', `${API_BASE}/notifications/sms`, body);
        closeModal('sendSmsModal');
        showToast('SMS notification sent!', 'success');
        loadAllNotifications();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// ── Rate Limit Test ───────────────────────────────────────────
async function testRateLimit() {
    const log = document.getElementById('rateLimitLog');
    const start = Date.now();
    try {
        const res = await fetch(`${API_BASE}/doctors`, {
            headers: authToken ? { 'Authorization': `Bearer ${authToken}` } : {}
        });
        const elapsed = Date.now() - start;
        const entry = document.createElement('div');
        entry.style.padding = '6px 22px';
        const color = res.status === 200 ? '#86efac' : res.status === 429 ? '#fca5a5' : '#fde68a';
        entry.style.color = color;
        entry.textContent = `[${new Date().toLocaleTimeString()}] HTTP ${res.status} — ${elapsed}ms`;
        log.appendChild(entry);
        log.scrollTop = log.scrollHeight;
        showToast(`HTTP ${res.status} received`, res.status === 429 ? 'error' : 'success');
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function spamRateLimit() {
    showToast('Sending 12 rapid requests...', 'info');
    for (let i = 0; i < 12; i++) {
        await testRateLimit();
        await new Promise(r => setTimeout(r, 100));
    }
}

// ── Modals ────────────────────────────────────────────────────
function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('open');
}
function closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('open');
}
function handleOverlayClick(e, id) {
    if (e.target === e.currentTarget) closeModal(id);
}

// Escape key closes modals
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.open').forEach(m => m.classList.remove('open'));
    }
});

// ── Clipboard ─────────────────────────────────────────────────
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showToast(`Copied: ${text}`, 'success');
    }).catch(() => {
        showToast('Copy failed', 'error');
    });
}

// ── Secure Navigation Override ────────────────────────────────
/**
 * Wraps navigateTo with a role-check gate.
 * Prevents accessing pages the current user's role can't access.
 * Called after all functions are defined.
 */
function safeNavigateTo(name) {
    if (name !== 'dashboard') {
        const navEl = document.getElementById(`nav-${name}`);
        if (navEl) {
            const allowed = (navEl.dataset.roles || '').split(',').map(r => r.trim().toUpperCase());
            const role = currentUser?.role || null;
            if (!role || !allowed.includes(role)) {
                showToast('Access denied — insufficient permissions.', 'error');
                navigateTo('dashboard');
                return;
            }
        }
    }
    navigateTo(name);
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    // Patch all nav-item onclick handlers to use safeNavigateTo
    document.querySelectorAll('.nav-item[data-roles]').forEach(btn => {
        const onclickAttr = btn.getAttribute('onclick') || '';
        const match = onclickAttr.match(/navigateTo\('([^']+)'\)/);
        if (match) {
            const pageName = match[1];
            btn.onclick = () => safeNavigateTo(pageName);
        }
    });

    // Set default appointment date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateInput = document.getElementById('apptDate');
    if (dateInput) dateInput.value = tomorrow.toISOString().split('T')[0];

    // Apply initial permissions (no user logged in)
    applyRolePermissions();

    // Initial connection check
    checkConnection();

    // Periodic re-check every 30s
    setInterval(checkConnection, 30000);
});
