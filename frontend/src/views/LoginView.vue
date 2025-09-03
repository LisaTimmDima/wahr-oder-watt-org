<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: Gesamtstruktur, Template, State-Management und clientseitige Logik.
// - Dima: Implementierung der API-Aufrufe (Login, Registrierung).
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, computed) und andere Komponenten (Icons).
import { ref, computed } from 'vue';
import { EnvelopeIcon, LockClosedIcon, UserIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Konstanten & Konfiguration
// ==================================================================================
const VIEW_MODES = {
  USER_LOGIN: 'user-login',
  ADMIN_LOGIN: 'admin-login',
  REGISTER: 'register',
  FORGOT_PASSWORD: 'forgot-password',
  FORGOT_PASSWORD_EMAIL_SENT: 'forgot-password-email-sent',
  RESET_PASSWORD: 'reset-password',
};
const API_URL_LOGIN = '/api/auth/login';
const API_URL_ADMIN_LOGIN = '/api/auth/login_admin';
const API_URL_REGISTER = '/api/auth/register';

// ==================================================================================
// Emits: Deklariert Events, die diese Komponente aussenden kann, um mit Eltern-Komponenten zu kommunizieren.
// ==================================================================================
const emit = defineEmits(['login-successful', 'admin-login-successful']);

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen, deren Änderungen die UI automatisch aktualisieren.
// ==================================================================================
const mode = ref(VIEW_MODES.USER_LOGIN);
const email = ref('');
const playerUsername = ref('');
const password = ref('');
const adminLoginName = ref('');
const adminPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const notificationMessage = ref('');
const errorMessage = ref('');

// ==================================================================================
// Computed Properties: Leiten Werte von reaktiven Variablen ab und aktualisieren sich effizient selbst.
// ==================================================================================
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const isEmailValid = computed(() => emailRegex.test(email.value.trim()));
const currentTitle = computed(() => {
  switch(mode.value) {
    case VIEW_MODES.FORGOT_PASSWORD: return 'Passwort vergessen?';
    case VIEW_MODES.FORGOT_PASSWORD_EMAIL_SENT: return 'Überprüfen Sie Ihre E-Mail.';
    case VIEW_MODES.RESET_PASSWORD: return 'Neues Passwort';
    case VIEW_MODES.REGISTER: return 'Erstellen Sie Ihr Konto';
    default: return 'Wahr oder Watt?';
  }
});

// ==================================================================================
// Methoden: Funktionen zur Handhabung von Benutzerinteraktionen und Geschäftslogik.
// ==================================================================================

/**
 * @function clearMessages
 * @author Lisa
 * @description Setzt Benachrichtigungs- und Fehlermeldungen zurück, um die UI sauber zu halten.
 */
function clearMessages() {
  notificationMessage.value = '';
  errorMessage.value = '';
}

/**
 * @function switchMode
 * @author Lisa
 * @description Wechselt den Anzeigemodus der Komponente und setzt dabei alle Formularfelder und Nachrichten zurück.
 * @param {string} newMode - Der neue Anzeigemodus, der aktiviert werden soll (ein Wert aus VIEW_MODES).
 */
function switchMode(newMode) {
  mode.value = newMode;
  email.value = '';
  playerUsername.value = '';
  password.value = '';
  adminLoginName.value = '';
  adminPassword.value = '';
  newPassword.value = '';
  confirmPassword.value = '';
  clearMessages();
}

/**
 * @function handleForgotPasswordRequest
 * @author Lisa
 * @description Behandelt die Anforderung zum Zurücksetzen des Passworts. Simuliert das Senden einer E-Mail und wechselt die Ansicht.
 * @todo Die tatsächliche API-Integration für das Senden der E-Mail implementieren.
 */
function handleForgotPasswordRequest() {
  clearMessages();
  console.log('Passwort-Reset angefordert für:', email.value);
  mode.value = VIEW_MODES.FORGOT_PASSWORD_EMAIL_SENT;
}

/**
 * @function handlePasswordReset
 * @author Lisa
 * @description Behandelt das Zurücksetzen des Passworts mit dem neuen Passwort. Führt Validierungen durch, bevor der Vorgang (simuliert) abgeschlossen wird.
 * @todo Die tatsächliche API-Integration für das Speichern des neuen Passworts implementieren.
 */
function handlePasswordReset() {
  clearMessages();
  if (newPassword.value !== confirmPassword.value) {
    errorMessage.value = "Die Passwörter stimmen nicht überein!";
    return;
  }
  if (newPassword.value.length < 8) {
    errorMessage.value = "Das Passwort muss mindestens 8 Zeichen lang sein.";
    return;
  }
  console.log('Neues Passwort wird gesetzt:', newPassword.value);
  notificationMessage.value = "Ihr Passwort wurde erfolgreich zurückgesetzt!";
  mode.value = VIEW_MODES.USER_LOGIN;
}

/**
 * @function performLogin
 * @author Dima
 * @description Führt eine Anmeldeanfrage an den Server aus.
 * @param {string} url - Die API-URL für die Anmeldung.
 * @param {object} payload - Die Anmeldedaten (Body), die an den Server gesendet werden.
 * @returns {Promise<string>} - Ein Promise, das bei Erfolg den JWT-Token als Text zurückgibt.
 * @throws {Error} - Wirft einen Fehler mit der Server-Nachricht, wenn die Anmeldung fehlschlägt.
 */
async function performLogin(url, payload) {
  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({ message: 'Anmeldung fehlgeschlagen. Überprüfen Sie Ihre Eingaben.' }));
    throw new Error(errorData.message || 'Anmeldung fehlgeschlagen.');
  }
  return response.text();
}

/**
 * @function handleLogin
 * @author Dima
 * @description Behandelt den Anmeldevorgang für Benutzer und Admins. Ruft die Login-API auf und sendet bei Erfolg ein Event.
 */
async function handleLogin() {
  clearMessages();
  let url, payload, successEvent;
  if (mode.value === VIEW_MODES.USER_LOGIN) {
    url = API_URL_LOGIN;
    payload = { email: email.value, password: password.value };
    successEvent = 'login-successful';
  } else if (mode.value === VIEW_MODES.ADMIN_LOGIN) {
    url = API_URL_ADMIN_LOGIN;
    payload = { username: adminLoginName.value, password: adminPassword.value };
    successEvent = 'admin-login-successful';
  } else {
    return;
  }
  try {
    const token = await performLogin(url, payload);
    localStorage.setItem('jwt', token);
    emit(successEvent);
  } catch (error) {
    errorMessage.value = error.message;
    console.error('Fehler bei der Anmeldung:', error);
  }
}

/**
 * @function handleRegister
 * @author Dima
 * @description Behandelt den Registrierungsvorgang für neue Benutzer. Validiert die Eingaben und ruft die Registrierungs-API auf.
 */
async function handleRegister() {
  clearMessages();
  if (!isEmailValid.value) {
    errorMessage.value = "Bitte geben Sie eine gültige E-Mail-Adresse ein.";
    return;
  }
  if (password.value.length < 8) {
    errorMessage.value = "Das Passwort muss mindestens 8 Zeichen lang sein.";
    return;
  }
  const payload = {
    username: playerUsername.value,
    password: password.value,
    email: email.value
  };
  try {
    const response = await fetch(API_URL_REGISTER, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Registrierung fehlgeschlagen.');
    }
    notificationMessage.value = "Dein Konto wurde erfolgreich erstellt. Du kannst dich jetzt anmelden!";
    switchMode(VIEW_MODES.USER_LOGIN);
    email.value = payload.email;
  } catch (error) {
    errorMessage.value = error.message;
    console.error('Fehler bei der Registrierung:', error);
  }
}
</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - v-if:        Zeigt ein Element nur an, wenn die Bedingung wahr ist.
    - @submit.prevent: Fängt das 'submit'-Event ab und verhindert das Neuladen der Seite.
    - v-model:       Verbindet ein Formularfeld mit einer reaktiven Variable (Zwei-Wege-Datenbindung).
    - :class:        Bindet Klassen dynamisch basierend auf reaktiven Daten.
    - {{ ... }}:      Gibt den Wert einer Variable als Text aus (Interpolation).
  -->
  <div class="bg-gray-100 min-h-screen flex items-center justify-center p-4">
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">

      <div class="text-center mb-8">
        <img src="../assets/logo.svg" alt="Wahr oder Watt?" class="mx-auto h-40 mb-4">
        <h1 class="text-2xl font-bold text-gray-800">{{ currentTitle }}</h1>
      </div>

      <!-- Benachrichtigungs- und Fehlermeldungen -->
      <div v-if="notificationMessage" class="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-6 rounded-md" role="alert">
        <p>{{ notificationMessage }}</p>
      </div>
      <div v-if="errorMessage" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded-md" role="alert">
        <p>{{ errorMessage }}</p>
      </div>

      <!-- Modus: Passwort vergessen -->
      <form v-if="mode === VIEW_MODES.FORGOT_PASSWORD" @submit.prevent="handleForgotPasswordRequest">
        <p class="text-center text-gray-600 mb-6">Geben Sie Ihre E-Mail-Adresse ein und wir senden Ihnen einen Link zum Zurücksetzen Ihres Passworts.</p>
        <div class="mb-6">
          <label for="forgot-email" class="block text-sm font-medium text-gray-700">Email</label>
          <div class="mt-1 relative">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3"><EnvelopeIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="email" type="email" id="forgot-email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
          </div>
        </div>
        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Passwort zurücksetzen</button>
        <p class="text-sm text-center text-gray-600 mt-8">
          <button type="button" @click="switchMode(VIEW_MODES.USER_LOGIN)" class="font-medium text-blue-600 hover:underline">Zurück zu "Anmelden"</button>
        </p>
      </form>

      <!-- Modus: E-Mail zum Passwort-Reset gesendet -->
      <div v-else-if="mode === VIEW_MODES.FORGOT_PASSWORD_EMAIL_SENT" class="text-center">
         <p class="text-gray-600 mb-6">Wir haben Ihnen eine Anleitung zur Passwortwiederherstellung an Ihre E-Mail-Adresse gesendet.</p>
         <p class="text-sm text-gray-500 mb-6">Haben Sie die E-Mail nicht erhalten? Überprüfen Sie Ihren Spam-Ordner oder <button @click="handleForgotPasswordRequest" class="underline hover:text-blue-600">senden Sie die E-Mail erneut</button>.</p>
         <button @click="switchMode(VIEW_MODES.USER_LOGIN)" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Zurück zur Anmeldung</button>
         <button @click="mode = VIEW_MODES.RESET_PASSWORD" class="mt-4 text-xs text-gray-400 underline">(Entwickler-Shortcut: Passwort jetzt zurücksetzen)</button>
      </div>
      
      <!-- Modus: Passwort zurücksetzen -->
      <form v-else-if="mode === VIEW_MODES.RESET_PASSWORD" @submit.prevent="handlePasswordReset">
        <p class="text-center text-gray-600 mb-6">Ihr neues Passwort muss sich von Ihrem bisherigen Passwort unterscheiden und mindestens 8 Zeichen lang sein.</p>
        <div class="mb-4">
          <label for="new-password" class="block text-sm font-medium text-gray-700">Neues Passwort</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><LockClosedIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="newPassword" type="password" id="new-password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
          </div>
        </div>
        <div class="mb-6">
          <label for="confirm-password" class="block text-sm font-medium text-gray-700">Passwort bestätigen</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><LockClosedIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="confirmPassword" type="password" id="confirm-password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
          </div>
        </div>
        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Passwort zurücksetzen</button>
         <p class="text-sm text-center text-gray-600 mt-8">
          <button type="button" @click="switchMode(VIEW_MODES.USER_LOGIN)" class="font-medium text-blue-600 hover:underline">Zurück zu "Anmelden"</button>
        </p>
      </form>

      <!-- Modus: Registrierung -->
      <form v-else-if="mode === VIEW_MODES.REGISTER" @submit.prevent="handleRegister">
        <p class="text-center text-gray-600 mb-6">Erstellen Sie Ihr Konto mit Ihrer E-Mail-Adresse.</p>
        <div class="mb-4">
          <label for="reg-email" class="block text-sm font-medium text-gray-700">Email</label>
          <div class="mt-1 relative">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3"><EnvelopeIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="email" type="email" id="reg-email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
          </div>
        </div>
        <div class="mb-4">
          <label for="reg-username" class="block text-sm font-medium text-gray-700">Benutzername</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><UserIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="playerUsername" type="text" id="reg-username" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="Wähle einen Namen" required>
          </div>
        </div>
        <div class="mb-6">
          <label for="reg-password" class="block text-sm font-medium text-gray-700">Passwort</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><LockClosedIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="password" type="password" id="reg-password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
          </div>
        </div>
        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Konto erstellen</button>
        <p class="text-xs text-gray-500 mt-4 text-center">Mit der Erstellung eines Kontos erklären Sie sich mit den <a href="#" class="underline">Nutzungsbedingungen</a> und der <a href="#" class="underline">Datenschutzerklärung</a> einverstanden.</p>
        <p class="text-sm text-center text-gray-600 mt-8">
          Haben Sie bereits ein Konto? <button type="button" @click="switchMode(VIEW_MODES.USER_LOGIN)" class="font-medium text-blue-600 hover:underline">Hier anmelden</button>
        </p>
      </form>

      <!-- Modus: User- & Admin-Login (Standard) -->
      <form v-else @submit.prevent="handleLogin">
        <div class="mb-6 flex rounded-lg p-1 bg-gray-200">
          <button type="button" @click="switchMode(VIEW_MODES.USER_LOGIN)" :class="['w-1/2 p-2 rounded-md font-semibold transition-colors', mode === VIEW_MODES.USER_LOGIN ? 'bg-white shadow' : 'text-gray-600']">User-Login</button>
          <button type="button" @click="switchMode(VIEW_MODES.ADMIN_LOGIN)" :class="['w-1/2 p-2 rounded-md font-semibold transition-colors', mode === VIEW_MODES.ADMIN_LOGIN ? 'bg-white shadow' : 'text-gray-600']">Admin-Login</button>
        </div>
        
        <p v-if="mode === VIEW_MODES.ADMIN_LOGIN" class="text-center text-gray-600 mb-6">Admin-Login</p>
        <p v-else class="text-center text-gray-600 mb-6">Melden Sie sich mit Ihrer E-Mail-Adresse und Ihrem Passwort an.</p>
        
        <!-- User-Login Felder -->
        <div v-if="mode === VIEW_MODES.USER_LOGIN">
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
            <div class="mt-1 relative">
              <span class="absolute inset-y-0 left-0 flex items-center pl-3"><EnvelopeIcon class="h-5 w-5 text-gray-400" /></span>
              <input v-model="email" type="email" id="email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
            </div>
          </div>
          <div class="mb-2">
            <label for="password" class="block text-sm font-medium text-gray-700">Passwort</label>
            <div class="mt-1 relative">
              <span class="absolute inset-y-0 left-0 flex items-center pl-3"><LockClosedIcon class="h-5 w-5 text-gray-400" /></span>
              <input v-model="password" type="password" id="password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
            </div>
          </div>
        </div>

        <!-- Admin-Login Felder -->
        <div v-if="mode === VIEW_MODES.ADMIN_LOGIN">
          <div class="mb-4">
            <label for="username" class="block text-sm font-medium text-gray-700">Login</label>
            <div class="mt-1 relative">
              <span class="absolute inset-y-0 left-0 flex items-center pl-3"><UserIcon class="h-5 w-5 text-gray-400" /></span>
              <input v-model="adminLoginName" type="text" id="username" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="Username" required>
            </div>
          </div>
          <div class="mb-2">
            <label for="admin-password" class="block text-sm font-medium text-gray-700">Passwort</label>
            <div class="mt-1 relative">
              <span class="absolute inset-y-0 left-0 flex items-center pl-3"><LockClosedIcon class="h-5 w-5 text-gray-400" /></span>
              <input v-model="adminPassword" type="password" id="admin-password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
            </div>
          </div>
        </div>

        <div class="text-right mb-6 h-6">
          <button v-if="mode === VIEW_MODES.USER_LOGIN" type="button" @click="switchMode(VIEW_MODES.FORGOT_PASSWORD)" class="text-sm font-medium text-blue-600 hover:underline">Passwort vergessen?</button>
        </div>
        
        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Anmelden</button>
        
        <p v-if="mode === VIEW_MODES.USER_LOGIN" class="text-sm text-center text-gray-600 mt-8">
          Sie haben noch kein Konto? <button type="button" @click="switchMode(VIEW_MODES.REGISTER)" class="font-medium text-blue-600 hover:underline">Hier registrieren</button>
        </p>
      </form>

    </div>
  </div>
</template>
