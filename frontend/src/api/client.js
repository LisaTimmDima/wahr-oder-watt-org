// frontend/src/api/client.js
// Zentraler API-Client für das Vue-Frontend.
// Enthält: Token-Verwaltung, generische request-Funktion, Shortcuts (GET/POST/PUT/DELETE), Login/Logout.

const API_BASE = import.meta.env?.VITE_API_BASE || '/api';

// ------- Token Handling -------
const TOKEN_KEY = 'jwt';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
  }
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
}

// ------- Interner Router-Lader (lazy) -------
async function navigateToLogin() {
  try {
    const { default: router } = await import('../router');
    router.replace('/login');
  } catch {
    // Fallback: Seite neu laden
    window.location.href = '/login';
  }
}

// ------- Kern: generische Request-Funktion -------
async function request(path, {
  method = 'GET',
  body,
  headers = {},
  expectJson = true,
  auth = true
} = {}) {

  const finalHeaders = {
    ...headers
  };

  if (auth) {
    const token = getToken();
    if (token) {
      finalHeaders['Authorization'] = 'Bearer ' + token;
    }
  }

  if (body !== undefined && !(body instanceof FormData)) {
    finalHeaders['Content-Type'] = 'application/json';
    body = JSON.stringify(body);
  }

  const res = await fetch(toAbsolute(path), {
    method,
    headers: finalHeaders,
    body
  });

  if (res.status === 401) {
    clearAuth();
    await navigateToLogin();
    throw new Error('Nicht autorisiert (401)');
  }

  if (!res.ok) {
    let detail = '';
    try {
      if (res.headers.get('Content-Type')?.includes('application/json')) {
        const errJson = await res.json();
        detail = errJson.message || JSON.stringify(errJson);
      } else {
        detail = await res.text();
      }
    } catch {
      // ignorieren
    }
    throw new Error(`Request fehlgeschlagen: ${res.status} ${res.statusText}${detail ? ' - ' + detail : ''}`);
  }

  if (!expectJson) {
    return res;
  }

  if (res.status === 204) return null;

  const ct = res.headers.get('Content-Type') || '';
  if (!ct.includes('application/json')) {
    throw new Error('Unerwartetes Format (erwartet JSON)');
  }
  return res.json();
}

// ------- Hilfsfunktion Pfad -> absolute URL -------
function toAbsolute(path) {
  if (path.startsWith('http://') || path.startsWith('https://')) return path;
  // Doppelte Slashes vermeiden
  return API_BASE.replace(/\/+$/, '') + '/' + path.replace(/^\/+/, '');
}

// ------- Öffentliche Shortcut-Funktionen -------
export function apiGet(path, options = {}) {
  return request(path, { ...options, method: 'GET' });
}

export function apiPost(path, data, options = {}) {
  return request(path, { ...options, method: 'POST', body: data });
}

export function apiPut(path, data, options = {}) {
  return request(path, { ...options, method: 'PUT', body: data });
}

export function apiPatch(path, data, options = {}) {
  return request(path, { ...options, method: 'PATCH', body: data });
}

export function apiDelete(path, options = {}) {
  return request(path, { ...options, method: 'DELETE' });
}

// ------- Auth spezifisch -------
export async function login({ username, password }) {
  const data = await request('/auth/login', {
    method: 'POST',
    body: { username, password },
    auth: false
  });
  if (!data?.token) {
    throw new Error('Login-Antwort enthält kein Token');
  }
  setToken(data.token);
  return data;
}

export function logout() {
  clearAuth();
  navigateToLogin();
}

// Optional: Health-Check ohne Auth
export function ping() {
  return request('/public/ping', { auth: false }).catch(() => null);
}

// ------- Batch Helper (parallele GETs) -------
export async function apiBatchGet(paths) {
  const results = await Promise.allSettled(paths.map(p => apiGet(p)));
  return results.map(r => r.status === 'fulfilled' ? r.value : { error: r.reason.message });
}
// ------- Direkte Navigation zu Login (ohne Router) -------
function navigateToLogin() {
  if (location.pathname !== '/login') {
    window.location.href = '/login';
  }
}

// ------- Upload (Multipart) -------
export function apiUpload(path, formData, options = {}) {
  return request(path, {
    ...options,
    method: 'POST',
    body: formData,
    // Content-Type NICHT setzen, Browser setzt Boundary
  });
}