<script setup>
import { ref } from 'vue'
import Login from './views/LoginView.vue'
import LobbyView from './views/LobbyView.vue'
import HilfeView from './views/HilfeView.vue'
import SpielView from './views/SpielView.vue' 

// --- ÄNDERUNG 1: Der Zustand, der die Ansicht steuert ---
// Statt eines einfachen true/false, speichern wir den Namen der aktuellen Ansicht.
const currentView = ref('login') // Startansicht ist 'login'

// --- ÄNDERUNG 2: Funktionen zum Wechseln der Ansicht ---
// Wird vom Login aufgerufen
function onLoginSuccess() {
  currentView.value = 'lobby'
}

// Wird von der Lobby aufgerufen, um ein Spiel zu starten
function onGameStart() {
  currentView.value = 'game'
}

// Wird von der Lobby aufgerufen, um die Hilfe anzuzeigen
function showHelp() {
  currentView.value = 'hilfe'
}

// Wird von der Hilfe-Seite aufgerufen, um zurück zur Lobby zu kommen
function showLobby() {
  currentView.value = 'lobby'
}
</script>

<template>
  <Login v-if="currentView === 'login'" @login-successful="onLoginSuccess" />
  
  <LobbyView 
    v-else-if="currentView === 'lobby'" 
    @start-game="onGameStart" 
    @show-help="showHelp" 
  />
  
  <SpielView v-else-if="currentView === 'game'" />

  <HilfeView v-else-if="currentView === 'hilfe'" @show-lobby="showLobby" />

</template>

