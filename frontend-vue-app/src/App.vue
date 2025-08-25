// In App.vue

<script setup>
import { ref } from 'vue'
import Login from './views/Login.vue'
import LobbyView from './views/LobbyView.vue'

// Ein Zustand, der steuert, welche Seite wir sehen.
// Am Anfang ist der User nicht eingeloggt.
const isLoggedIn = ref(false)

// Diese Funktion wird von der Login-Komponente aufgerufen,
// wenn der Login erfolgreich war.
function onLoginSuccess() {
  isLoggedIn.value = true
}
</script>

<template>
  <Login v-if="!isLoggedIn" @login-successful="onLoginSuccess" />
  
  <LobbyView v-else />
</template>

<style>
/* Du kannst deine globalen Styles hier behalten oder entfernen */
.logo {
  height: 6em;
  padding: 1.5em;
}
</style>