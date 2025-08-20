
<script setup>
import { ref } from 'vue'

const username = ref('')
const password = ref('')
const error = ref('')

function login() {
  if (!username.value || !password.value) {
    error.value = 'Bitte Benutzername und Passwort eingeben.'
    return
  }
  // Hier könnte ein echter Login-Request stehen
  error.value = ''
  alert(`Login erfolgreich!\nBenutzername: ${username.value}`)
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-100">
    <div class="w-full max-w-sm bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-bold mb-6 text-center">Login</h2>
      <form @submit.prevent="login">
        <div class="mb-4">
          <label class="block mb-1 font-medium" for="username">Benutzername</label>
          <input v-model="username" id="username" type="text" class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-blue-300" />
        </div>
        <div class="mb-4">
          <label class="block mb-1 font-medium" for="password">Passwort</label>
          <input v-model="password" id="password" type="password" class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-blue-300" />
        </div>
        <div v-if="error" class="mb-4 text-red-500 text-sm">{{ error }}</div>
        <button type="submit" class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition">Login</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
body {
  background: #f3f4f6;
}
</style>
