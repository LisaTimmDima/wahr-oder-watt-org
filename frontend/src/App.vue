<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: Gesamtstruktur, Template, State-Management und clientseitige Logik.
// ==================================================================================

// import: Lädt andere Vue-Komponenten, um sie hier zu verwenden.
import { ref } from 'vue';
import Login from './views/LoginView.vue';
import LobbyView from './views/LobbyView.vue';
import HilfeView from './views/HilfeView.vue';
import SpielView from './views/SpielView.vue';
import HighscoreView from './views/HighscoreView.vue';
import AdminLayout from './layouts/AdminLayout.vue';

// ref(): Erstellt eine "reaktive" Variable. Ändert sich ihr Wert, aktualisiert Vue die UI automatisch.
// Dies ist die Grundlage des "Reactive State".
const currentView = ref('login');
const currentGameDetails = ref(null);

// ==================================================================================
// Methoden: Funktionen, die als Event-Handler dienen und die Ansicht wechseln.
// ==================================================================================

/**
 * @function onLoginSuccess
 * @author Lisa
 * @description Event-Handler, der auf das 'login-successful'-Event von LoginView reagiert.
 * Wechselt die Ansicht zur Lobby für normale Benutzer.
 */
function onLoginSuccess() {
  currentView.value = 'lobby';
}

/**
 * @function onAdminLoginSuccess
 * @author Lisa
 * @description Event-Handler, der auf das 'admin-login-successful'-Event von LoginView reagiert.
 * Wechselt die Ansicht zum Admin-Layout.
 */
function onAdminLoginSuccess() {
  currentView.value = 'admin';
}

/**
 * @function onGameStart
 * @author Lisa
 * @description Event-Handler, der auf das 'start-game'-Event von LobbyView reagiert.
 * Speichert die Spieldetails und wechselt zur Spielansicht.
 * @param {object} gameDetails - Das Objekt mit den Spieldetails (z.B. Level, Gegner).
 */
function onGameStart(gameDetails) {
  currentGameDetails.value = gameDetails;
  currentView.value = 'game';
}

/**
 * @function showHelp
 * @author Lisa
 * @description Zeigt die Hilfe-Ansicht an. Wird von der LobbyView aufgerufen.
 */
function showHelp() {
  currentView.value = 'hilfe';
}

/**
 * @function showHighscores
 * @author Lisa
 * @description Zeigt die Highscore-Ansicht an. Wird von der LobbyView aufgerufen.
 */
function showHighscores() {
  currentView.value = 'highscores';
}

/**
 * @function showLobby
 * @author Lisa
 * @description Zeigt die Lobby-Ansicht an. Dient als allgemeine "Zurück zur Lobby"-Funktion.
 */
function showLobby() {
  currentView.value = 'lobby';
}
</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - <Login ... />: So wird eine importierte Komponente im Template eingebunden.
    - v-if:        Eine Vue-Direktive. Zeigt das Element nur an, wenn die Bedingung wahr ist.
    - @...:         Kurz für v-on. "Hört" auf ein Event (emit) von der Kind-Komponente.
    - :...:         Kurz für v-bind. Übergibt Daten (Props) an eine Kind-Komponente.
  -->

  <Login 
    v-if="currentView === 'login'" 
    @login-successful="onLoginSuccess" 
    @admin-login-successful="onAdminLoginSuccess" 
  />
  
  <LobbyView 
    v-else-if="currentView === 'lobby'" 
    @start-game="onGameStart"
    @show-help="showHelp"
    @show-highscores="showHighscores"
  />

  <AdminLayout 
    v-else-if="currentView === 'admin'" 
    @go-to-lobby="showLobby" 
  />
  
  <SpielView 
    v-else-if="currentView === 'game'" 
    :game-details="currentGameDetails" 
    @show-lobby="showLobby" 
  />
  
  <HilfeView 
    v-else-if="currentView === 'hilfe'" 
    @show-lobby="showLobby" 
  />

  <HighscoreView 
    v-else-if="currentView === 'highscores'" 
    @show-lobby="showLobby" 
  />

</template>
