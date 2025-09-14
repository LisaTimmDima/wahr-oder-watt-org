<script setup>

// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Layout, State-Management und Event-Handling.
// - Dima: Implementierung der API-Aufrufe zum Abrufen von Daten.
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted, computed, onBeforeUnmount } from 'vue';
import { UserCircleIcon, TrophyIcon, QuestionMarkCircleIcon, ArrowRightOnRectangleIcon, UsersIcon, ChevronRightIcon, ArrowUturnLeftIcon, Cog6ToothIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Emits: Deklariert Events, die diese Komponente aussenden kann, um mit der Eltern-Komponente (App.vue) zu kommunizieren.
// ==================================================================================
const emit = defineEmits(['start-game', 'show-help', 'show-highscores', 'show-admin']);

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen, deren Änderungen die UI automatisch aktualisieren.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').Ref<object>}
 * @description Speichert die Informationen des aktuell angemeldeten Benutzers.
 * @todo Aktuell hartkodiert. Sollte durch einen API-Aufruf ersetzt werden.
 */
const loggedInUser = ref({ id: 0, name: 'Lädt...' });

/**
 * @type {import('vue').Ref<Array<object>>}
 * @description Speichert die Liste der verfügbaren Spieler, die herausgefordert werden können.
 */
const availablePlayers = ref([]);

/**
 * @type {import('vue').Ref<number>}
 * @description Speichert das vom Benutzer ausgewählte Spiellevel (1 oder 2).
 */
const selectedLevel = ref(1);
const selectedPlayer = ref(null);
const invitationSent = ref(false);
const loading = ref(false);
const error = ref(null);
const token = computed(() => localStorage.getItem('jwt'));

async function fetchCurrentUser() {
  const resp = await fetch('/api/users/me', { headers: { 'Accept': 'application/json', 'Authorization': `Bearer ${token.value}` } });
  if (!resp.ok) throw new Error('Fehler beim Laden der Benutzer');
  return await resp.json();
}

// BARRIEREFREIHEIT: Reaktive Variable für die Zoom-Stufe.
const zoomLevel = ref(1);

// BARRIEREFREIHEIT: Reaktive Variable zur Steuerung des Hochkontrastmodus.
const isHighContrast = ref(false);

// ==================================================================================
// Computed Properties
// ==================================================================================

// BARRIEREFREIHEIT: Berechnete Eigenschaft, die ein Style-Objekt für die dynamische Skalierung (Zoom) zurückgibt.
const containerStyle = computed(() => ({
  zoom: zoomLevel.value
}));

// ==================================================================================
// Methoden: Funktionen zur Handhabung von Benutzerinteraktionen und Geschäftslogik.
// ==================================================================================

/**
 * @function startGame
 * @author Lisa
 * @description Löst das 'start-game'-Event aus und übergibt die Details zum Gegner und zum Level an die Eltern-Komponente.
 */
async function startGame() {
  if (selectedPlayer.value && selectedLevel.value) {
    invitationSent.value = true;

    // Duell im Backend erstellen
    const resp = await fetch('/api/duels', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify({
        challengerId: loggedInUser.value.id,
        opponentId: selectedPlayer.value.id,
        level: selectedLevel.value,
        currentTime: Date.now()
      })
    });
    const duel = await resp.json();

    // Spielansicht mit Duell-ID öffnen
    emit('start-game', {
      id: duel.id,
      opponent: { id: selectedPlayer.value.id, name: selectedPlayer.value.name },
      level: selectedLevel.value
    });

    invitationSent.value = false;
    selectedPlayer.value = null;
  }
}


let challengePolling = null;

function startChallengePolling() {
  challengePolling = setInterval(async () => {
    if (!loggedInUser.value.name) return;
    const res = await fetch(`/api/challenge/pending/${loggedInUser.value.name}`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token.value}`
      }
    });
    if (res.status === 200) {
      const data = await res.json();
      emit('start-game', { opponent: { name: data.challenger }, level: data.level }); // Level aus Antwort
      clearInterval(challengePolling);
    }
  }, 3000);
}

onMounted(() => {
  startChallengePolling();
});

onBeforeUnmount(() => {
  if (challengePolling) clearInterval(challengePolling);
});


/**
 * @function togglePlayerSelection
 * @author Lisa
 * @description Wählt einen Spieler aus oder ab.
 * @param {object} player - Das Spieler-Objekt.
 */
function togglePlayerSelection(player) {
  if (selectedPlayer.value && selectedPlayer.value.id === player.id) {
    selectedPlayer.value = null;
  } else {
    selectedPlayer.value = player;
  }
}

/**
 * @function onHelpClick
 * @author Lisa
 * @description Löst das 'show-help'-Event aus, um die Hilfe-Ansicht anzuzeigen.
 */
function onHelpClick() {
  emit('show-help');
}

/**
 * @function onAdminClick
 * @author Lisa
 * @description Löst das 'show-admin'-Event aus, um die Admin-Ansicht anzuzeigen.
 */
function onAdminClick() {
  emit('show-admin');
}

/**
 * @function onHighscoresClick
 * @author Lisa
 * @description Löst das 'show-highscores'-Event aus, um die Highscore-Ansicht anzuzeigen.
 */
function onHighscoresClick() {
  emit('show-highscores');
}

/**
 * @function logout
 * @author Lisa
 * @description Meldet den Benutzer ab, indem der Token aus dem Local Storage entfernt und zur Login-Seite weitergeleitet wird.
 */
function logout() {
  localStorage.removeItem(token);
  localStorage.removeItem('currentUserId');
  localStorage.removeItem('currentUsername');
  window.location.href = '/login';
}

  async function fetchUsers() {
  const resp = await fetch('/api/users', { headers: { 'Accept': 'application/json', 'Authorization': `Bearer ${token.value}` } });
  if (!resp.ok) throw new Error('Fehler beim Laden der Benutzer');
  return await resp.json();
}

// BARRIEREFREIHEIT: Methoden zur Anpassung der Zoom-Stufe.
function increaseZoom() {
  zoomLevel.value += 0.1;
}
function decreaseZoom() {
  zoomLevel.value -= 0.1;
}

// BARRIEREFREIHEIT: Methode zum Umschalten des Hochkontrastmodus.
function toggleHighContrast() {
  isHighContrast.value = !isHighContrast.value;
}

// ==================================================================================
// Lifecycle Hooks: Funktionen, die Vue zu bestimmten Zeitpunkten im Lebenszyklus einer Komponente automatisch aufruft.
// Verantwortlich: Dima (da der Hook die API-Logik auslöst)
// ==================================================================================

/**
 * onMounted(): Wird ausgeführt, nachdem die Komponente in das DOM eingehängt wurde.
 * Perfekt, um initiale Daten vom Server zu laden.
 */
onMounted(async () => {
  loading.value = true;
  try {
    // Aktuellen User laden (Backend) mit Fallback auf localStorage
    try {
      const me = await fetchCurrentUser();
      loggedInUser.value = {
        id: me.id,
        name: me.username,
        admin: me.admin,
      };
    } catch {
      loggedInUser.value = {
        id: Number(localStorage.getItem('currentUserId')),
        name: localStorage.getItem('currentUsername'),
        admin: localStorage.getItem('isAdmin') === 'true'
      };
    }
    const all = await fetchUsers();
    availablePlayers.value = all
        .filter(u => u.id !== loggedInUser.value.id)
        .map(u => ({
          id: u.id,
          name: u.username,
        }));
  } catch (e) {
    error.value = e.message;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - @click:      Führt eine Methode aus, wenn auf das Element geklickt wird.
    - :class:       Bindet Klassen dynamisch, z.B. um das Aussehen basierend auf dem Zustand zu ändern.
    - v-for:        Erstellt eine Schleife über eine Liste (hier availablePlayers) und rendert für jeden Eintrag ein Element.
    - :key:         Ein eindeutiger Schlüssel für jedes v-for-Element, wichtig für die Performance.
    - v-if/v-else:  Zeigt Elemente nur an, wenn eine bestimmte Bedingung erfüllt (oder nicht erfüllt) ist.
    - {{ ... }}:     Gibt den Wert einer Variable als Text aus (Interpolation).
  -->
  <div class="bg-gray-100 min-h-screen" :class="{ 'high-contrast': isHighContrast }">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

      <header class="flex flex-col sm:flex-row justify-between items-center mb-8">
        <div class="flex items-center gap-4 mb-4 sm:mb-0">
          <img src="../assets/gluehbirne.svg" alt="Logo" class="h-24 w-auto" style="transform: scale(0.8);">
          <div class="text-4xl font-bold text-gray-800">Wahr oder Watt?</div>
        </div>
        <div class="flex items-center">
          <!-- Group 1: Accessibility -->
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-600">Zoom:</span>
              <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
              <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">+</button>
            </div>
            <button @click="toggleHighContrast" class="px-3 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">Kontrast</button>
          </div>

          <div class="border-l border-gray-300 h-6 mx-4"></div>

          <!-- Group 2: User Actions -->
          <div class="flex items-center gap-4">
            <button @click="onHighscoresClick" class="flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
              <TrophyIcon class="h-6 w-6" />
              <span>Highscores</span>
            </button>
            <button @click="onHelpClick" class="flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
              <QuestionMarkCircleIcon class="h-6 w-6" />
              <span>Hilfe</span>
            </button>
          </div>

          <div class="border-l border-gray-300 h-6 mx-4"></div>

          <!-- Group 3: Logout -->
          <div class="flex items-center">
            <button @click="logout" class="flex items-center gap-2 text-red-500 hover:text-red-700 font-semibold transition-colors">
              <ArrowRightOnRectangleIcon class="h-6 w-6" />
              <span>Abmelden</span>
            </button>
          </div>
        </div>
      </header>

      <main class="grid grid-cols-1 lg:grid-cols-3 gap-8" :style="containerStyle">

        <!-- Spalte für Spieleinstellungen -->
        <div class="lg:col-span-1 bg-white rounded-2xl shadow-lg p-6">
          <div class="flex items-center gap-3 mb-6">
            <Cog6ToothIcon class="h-8 w-8 text-gray-500"/>
            <h2 class="text-2xl font-bold text-gray-800">Spieleinstellungen</h2>
          </div>

          <div class="space-y-4">
            <div
              @click="selectedLevel = 1"
              :class="['p-4 rounded-xl border-2 cursor-pointer transition-all', selectedLevel === 1 ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300']"
            >
              <h3 class="font-bold text-lg text-gray-800">Level 1: Speedrun</h3>
              <p class="text-gray-600 text-sm">60 Sekunden Gesamtzeit, so viele Fragen wie möglich.</p>
            </div>
            <div
              @click="selectedLevel = 2"
              :class="['p-4 rounded-xl border-2 cursor-pointer transition-all', selectedLevel === 2 ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300']"
            >
              <h3 class="font-bold text-lg text-gray-800">Level 2: Runden-Duell</h3>
              <p class="text-gray-600 text-sm">5 Runden, 10 Sekunden pro Runde.</p>
            </div>
          </div>

          <!-- Angemeldeter Benutzer -->
          <div class="mt-8 pt-6 border-t border-gray-200">
             <div class="flex items-center gap-4">
                <div class="bg-gray-200 p-2 rounded-full">
                    <UserCircleIcon class="h-10 w-10 text-gray-600" />
                </div>
                <div>
                    <p class="text-sm text-gray-600">Angemeldet als</p>
                    <span class="text-lg font-bold text-gray-900">{{ loggedInUser.name }}</span>
                </div>
            </div>
          </div>

          <!-- Admin Button -->
          <div v-if="loggedInUser.admin" class="mt-8 pt-6 border-t border-gray-200">
            <button
                @click="onAdminClick" class="flex w-full justify-center items-center gap-2 px-4 py-2 text-sm bg-transparent border-2 border-blue-500 text-blue-500 rounded-lg hover:bg-blue-500 hover:text-white font-semibold transition-colors" aria-label="Zum Admin-Dashboard" title="Zum Admin-Dashboard">
              <ArrowUturnLeftIcon class="h-5 w-5" />
              <span>Zum Admin-Dashboard</span>
            </button>
          </div>

        </div>

        <!-- Spalte für verfügbare Spieler -->
        <div class="lg:col-span-2 bg-white rounded-2xl shadow-lg p-6 flex flex-col">
          <div class="flex items-center gap-3 mb-6">
            <UsersIcon class="h-8 w-8 text-gray-500"/>
            <h2 class="text-2xl font-bold text-gray-800">Verfügbare Spieler</h2>
          </div>
          <div class="overflow-y-auto h-96 pr-2 flex-grow">
            <ul v-if="availablePlayers.length > 0" class="space-y-3">
              <li
                v-for="player in availablePlayers"
                :key="player.id"
                @click="togglePlayerSelection(player)"
                :class="['flex justify-between items-center p-4 rounded-xl transition-colors cursor-pointer', selectedPlayer && selectedPlayer.id === player.id ? 'bg-blue-100 border-2 border-blue-500' : 'bg-gray-50 hover:bg-gray-100']"
              >
                <div class="flex items-center gap-4">
                    <div class="bg-gray-200 p-2 rounded-full">
                        <UserCircleIcon class="h-8 w-8 text-gray-600" />
                    </div>
                    <span class="text-lg font-medium text-gray-800">{{ player.name }}</span>
                </div>
              </li>
            </ul>
            <!-- Wird angezeigt, während die Spielerliste lädt -->
            <div v-else class="text-center text-gray-500 py-16">
              <p class="text-lg">Suche nach Spielern...</p>
            </div>
          </div>
          <div class="mt-6">
            <button
              @click="startGame"
              :disabled="!selectedPlayer || !selectedLevel || invitationSent"
              :class="['w-full text-white font-bold py-3 px-6 rounded-lg transition-colors text-lg flex items-center justify-center', !selectedPlayer || !selectedLevel || invitationSent ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-600']"
            >
              <svg v-if="invitationSent" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ invitationSent ? 'Einladung wird gesendet...' : 'Spiel starten' }}</span>
            </button>
            <div v-if="invitationSent" class="mt-4 text-center p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
              <p>Einladung an <strong>{{ selectedPlayer.name }}</strong> gesendet. Warte auf seine Antwort...</p>
            </div>
          </div>
        </div>

      </main>

    </div>
  </div>
</template>

<style>
/* BARRIEREFREIHEIT: Stile für den Hochkontrastmodus */
.high-contrast {
  background-color: #000 !important;
  color: #fff !important;
}

.high-contrast .bg-white, .high-contrast .bg-gray-50 {
  background-color: #000 !important;
  border: 2px solid yellow !important;
}

.high-contrast .text-gray-800,
.high-contrast .text-gray-900,
.high-contrast .text-gray-700,
.high-contrast .text-gray-600,
.high-contrast .text-gray-500,
.high-contrast .text-gray-400 {
  color: #fff !important;
}

.high-contrast .bg-gray-100 {
    background-color: #000 !important;
}

.high-contrast .bg-gray-200 {
    background-color: #333 !important;
}

.high-contrast button {
    border: 1px solid yellow !important;
}

.high-contrast .text-blue-600 {
    color: yellow !important;
}

.high-contrast .border-gray-200, .high-contrast .border-t {
    border-color: yellow !important;
}

.high-contrast .bg-blue-50 {
    background-color: #00008b !important; /* Dunkelblau */
}

.high-contrast .border-blue-500 {
    border-color: yellow !important;
}

</style>