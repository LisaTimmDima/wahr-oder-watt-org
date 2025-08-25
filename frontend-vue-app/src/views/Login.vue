<script setup>
import { ref, computed } from 'vue'; // 'computed' importiert für dynamische Titel
// Um die Icons zu nutzen
import { EnvelopeIcon, LockClosedIcon, UserIcon } from '@heroicons/vue/24/solid';

// Definieren das "Signal" (emit), das wir an App.vue senden.
const emit = defineEmits(['login-successful'])

// Der Zustand, der die Ansicht steuert, wurde erweitert
const mode = ref('user-login');

// Bestehende Formulardaten
const email = ref('');
const username = ref('');
const password = ref('');

// Formulardaten für PASSWORT-RESET
const newPassword = ref('');
const confirmPassword = ref('');


// --- COMPUTED PROPERTY FÜR DYNAMISCHE TITEL ---
const currentTitle = computed(() => {
  switch(mode.value) {
    case 'forgot-password': return 'Passwort vergessen?';
    case 'forgot-password-email-sent': return 'Überprüfen Sie Ihre E-Mail.';
    case 'reset-password': return 'Neues Passwort';
    case 'register': return 'Erstellen Sie Ihr Konto';
    default: return 'Wahr oder Watt?';
  }
});

// Passwort Reset
function handleForgotPasswordRequest() {
  console.log('Passwort-Reset angefordert für:', email.value);
  // HIER: API-Aufruf an das Backend, um die E-Mail zu senden.
  mode.value = 'forgot-password-email-sent';
}

function handlePasswordReset() {
  if (newPassword.value !== confirmPassword.value) {
    alert("Die Passwörter stimmen nicht überein!");
    return;
  }
  if (newPassword.value.length < 8) {
    alert("Das Passwort muss mindestens 8 Zeichen lang sein.");
    return;
  }
  console.log('Neues Passwort wird gesetzt:', newPassword.value);
  // HIER: API-Aufruf, um das Passwort zurückzusetzen.
  alert("Ihr Passwort wurde erfolgreich zurückgesetzt!");
  mode.value = 'user-login';
}

// Login Funktion
function handleLogin() {
  if (mode.value === 'user-login') {
    console.log('User Login Versuch:', email.value, password.value);
    // Hier würde dein API-Aufruf stattfinden.
    
    // Wenn der Login erfolgreich ist, sende das Signal nach oben.
    emit('login-successful'); 
    
  } else {
    console.log('Admin Login Versuch:', username.value, password.value);
    // Hier würde die Admin-Anmeldung geprüft.
    emit('login-successful'); 
  }
}


function handleRegister() {
  console.log('Registrierungs-Versuch:', email.value, password.value);
}
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex items-center justify-center p-4">
    
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">

      <div class="text-center mb-8">
        <img src="../assets/logo.svg" alt="Wahr oder Watt?" class="mx-auto h-40 mb-4">
        <h1 class="text-2xl font-bold text-gray-800">{{ currentTitle }}</h1>
      </div>

      <form v-if="mode === 'forgot-password'" @submit.prevent="handleForgotPasswordRequest">
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
          <button type="button" @click="mode = 'user-login'" class="font-medium text-blue-600 hover:underline">Zurück zu "Anmelden"</button>
        </p>
      </form>

      <div v-else-if="mode === 'forgot-password-email-sent'" class="text-center">
         <p class="text-gray-600 mb-6">Wir haben Ihnen eine Anleitung zur Passwortwiederherstellung an Ihre E-Mail-Adresse gesendet.</p>
         <p class="text-sm text-gray-500 mb-6">Haben Sie die E-Mail nicht erhalten? Überprüfen Sie Ihren Spam-Ordner oder versuchen Sie, die <button @click="handleForgotPasswordRequest" class="underline hover:text-blue-600">Nachricht erneut zu senden.</button></p>
         <button @click="mode = 'user-login'" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Zurück zur Anmeldung</button>
         <button @click="mode = 'reset-password'" class="mt-4 text-xs text-gray-400 underline">(Entwickler-Shortcut: Passwort jetzt zurücksetzen)</button>
      </div>
      
      <form v-else-if="mode === 'reset-password'" @submit.prevent="handlePasswordReset">
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
          <button type="button" @click="mode = 'user-login'" class="font-medium text-blue-600 hover:underline">Zurück zu "Anmelden"</button>
        </p>
      </form>

      <form v-else-if="mode === 'register'" @submit.prevent="handleRegister">
        <p class="text-center text-gray-600 mb-6">Erstellen Sie Ihr Konto mit Ihrer E-Mail-Adresse.</p>
        <div class="mb-4">
          <label for="reg-email" class="block text-sm font-medium text-gray-700">Email</label>
          <div class="mt-1 relative">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3"><EnvelopeIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="email" type="email" id="reg-email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
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
          Haben Sie bereits ein Konto? <button type="button" @click="mode = 'user-login'" class="font-medium text-blue-600 hover:underline">Hier anmelden</button>
        </p>
      </form>

      <form v-else @submit.prevent="handleLogin">
        <div class="mb-6 flex rounded-lg p-1 bg-gray-200">
          <button type="button" @click="mode = 'user-login'" :class="['w-1/2 p-2 rounded-md font-semibold transition-colors', mode === 'user-login' ? 'bg-white shadow' : 'text-gray-600']">User-Login</button>
          <button type="button" @click="mode = 'admin-login'" :class="['w-1/2 p-2 rounded-md font-semibold transition-colors', mode === 'admin-login' ? 'bg-white shadow' : 'text-gray-600']">Admin-Login</button>
        </div>
        <p v-if="mode === 'admin-login'" class="text-center text-gray-600 mb-6">Admin-Login</p>
        <p v-else class="text-center text-gray-600 mb-6">Melden Sie sich mit Ihrer E-Mail-Adresse und Ihrem Passwort an.</p>
        <div v-if="mode === 'user-login'" class="mb-4">
          <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
           <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><EnvelopeIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="email" type="email" id="email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
          </div>
        </div>
        <div v-if="mode === 'admin-login'" class="mb-4">
          <label for="username" class="block text-sm font-medium text-gray-700">Login</label>
           <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><UserIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="username" type="text" id="username" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="Username" required>
          </div>
        </div>
        <div class="mb-2">
          <label for="password" class="block text-sm font-medium text-gray-700">Passwort</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3"><LockClosedIcon class="h-5 w-5 text-gray-400" /></span>
            <input v-model="password" type="password" id="password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
          </div>
        </div>
        <div class="text-right mb-6">
          <button type="button" @click="mode = 'forgot-password'" class="text-sm font-medium text-blue-600 hover:underline">Passwort vergessen?</button>
        </div>
        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Anmelden</button>
        <p class="text-sm text-center text-gray-600 mt-8">
          Sie haben noch kein Konto? <button type="button" @click="mode = 'register'" class="font-medium text-blue-600 hover:underline">Hier registrieren</button>
        </p>
      </form>

    </div>
  </div>
</template>