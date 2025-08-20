<script setup>
import { ref } from 'vue';
// Um die Icons zu nutzen, installiere: npm install @heroicons/vue
import { EnvelopeIcon, LockClosedIcon, UserIcon } from '@heroicons/vue/24/solid';

// Der Zustand, der die Ansicht steuert: 'user-login', 'admin-login', oder 'register'
const mode = ref('user-login');

// Formulardaten
const email = ref('');
const username = ref('');
const password = ref('');

// Funktionen, die beim Absenden der Formulare aufgerufen werden
function handleLogin() {
  if (mode.value === 'user-login') {
    console.log('User Login Versuch:', email.value, password.value);
    // Hier API-Aufruf für User-Login
  } else {
    console.log('Admin Login Versuch:', username.value, password.value);
    // Hier API-Aufruf für Admin-Login
  }
}

function handleRegister() {
  console.log('Registrierungs-Versuch:', email.value, password.value);
  // Hier API-Aufruf für Registrierung
}
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex items-center justify-center">
    
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">

      <div class="text-center mb-8">
        <img src="../assets/logo.svg" alt="Wahr oder Watt?" class="mx-auto h-40 mb-4">
        <h1 v-if="mode !== 'register'" class="text-2xl font-bold text-gray-800">Wahr oder Watt?</h1>
        <h1 v-else class="text-2xl font-bold text-gray-800">Erstellen Sie Ihr Konto</h1>
      </div>

      <form v-if="mode === 'register'" @submit.prevent="handleRegister">
        <p class="text-center text-gray-600 mb-6">Erstellen Sie Ihr Konto mit Ihrer E-Mail-Adresse.</p>
        
        <div class="mb-4">
          <label for="reg-email" class="block text-sm font-medium text-gray-700">Email</label>
          <div class="mt-1 relative">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3">
              <EnvelopeIcon class="h-5 w-5 text-gray-400" />
            </span>
            <input v-model="email" type="email" id="reg-email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
          </div>
        </div>
        
        <div class="mb-6">
          <label for="reg-password" class="block text-sm font-medium text-gray-700">Passwort</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3">
              <LockClosedIcon class="h-5 w-5 text-gray-400" />
            </span>
            <input v-model="password" type="password" id="reg-password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
          </div>
        </div>

        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Konto erstellen</button>

        <p class="text-xs text-gray-500 mt-4 text-center">Mit der Erstellung eines Kontos erklären Sie sich mit den <a href="#" class="underline">Nutzungsbedingungen</a> und der <a href="#" class="underline">Datenschutzerklärung</a> einverstanden.</p>

        <p class="text-sm text-center text-gray-600 mt-8">
          Haben Sie bereits ein Konto? <button @click="mode = 'user-login'" class="font-medium text-blue-600 hover:underline">Hier anmelden</button>
        </p>
      </form>

      <form v-else @submit.prevent="handleLogin">
        
        <div class="mb-6 flex rounded-lg p-1 bg-gray-200">
          <button 
            type="button" 
            @click="mode = 'user-login'"
            :class="['w-1/2 p-2 rounded-md font-semibold transition-colors', mode === 'user-login' ? 'bg-white shadow' : 'text-gray-600']">
            User-Login
          </button>
          <button 
            type="button" 
            @click="mode = 'admin-login'"
            :class="['w-1/2 p-2 rounded-md font-semibold transition-colors', mode === 'admin-login' ? 'bg-white shadow' : 'text-gray-600']">
            Admin-Login
          </button>
        </div>

        <p v-if="mode === 'admin-login'" class="text-center text-gray-600 mb-6">Admin-Login</p>
        <p v-else class="text-center text-gray-600 mb-6">Melden Sie sich mit Ihrer E-Mail-Adresse und Ihrem Passwort an.</p>
        
        <div v-if="mode === 'user-login'" class="mb-4">
          <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
           <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3">
              <EnvelopeIcon class="h-5 w-5 text-gray-400" />
            </span>
            <input v-model="email" type="email" id="email" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="email@address.com" required>
          </div>
        </div>

        <div v-if="mode === 'admin-login'" class="mb-4">
          <label for="username" class="block text-sm font-medium text-gray-700">Login</label>
           <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3">
              <UserIcon class="h-5 w-5 text-gray-400" />
            </span>
            <input v-model="username" type="text" id="username" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="Username" required>
          </div>
        </div>
        
        <div class="mb-2">
          <label for="password" class="block text-sm font-medium text-gray-700">Passwort</label>
          <div class="mt-1 relative">
             <span class="absolute inset-y-0 left-0 flex items-center pl-3">
              <LockClosedIcon class="h-5 w-5 text-gray-400" />
            </span>
            <input v-model="password" type="password" id="password" class="block w-full rounded-md border-gray-300 pl-10 focus:border-blue-500 focus:ring-blue-500" placeholder="••••••••••" required>
          </div>
        </div>
        
        <div class="text-right mb-6">
          <a href="#" class="text-sm font-medium text-blue-600 hover:underline">Passwort vergessen?</a>
        </div>

        <button type="submit" class="w-full bg-gray-800 text-white font-bold py-3 px-4 rounded-lg hover:bg-gray-700 transition-colors">Anmelden</button>

        <p class="text-sm text-center text-gray-600 mt-8">
          Sie haben noch kein Konto? <button @click="mode = 'register'" class="font-medium text-blue-600 hover:underline">Hier registrieren</button>
        </p>
      </form>

    </div>
  </div>
</template>