<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Layout, State-Management und Event-Handling.
// - Dima: Implementierung der API-Aufrufe zum Abrufen von Daten.
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted } from 'vue';
import { UserCircleIcon, TrophyIcon, QuestionMarkCircleIcon, ArrowRightOnRectangleIcon, UsersIcon, ChevronRightIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Emits: Deklariert Events, die diese Komponente aussenden kann, um mit der Eltern-Komponente (App.vue) zu kommunizieren.
// ==================================================================================
const emit = defineEmits(['start-game', 'show-help', 'show-highscores']);

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen, deren Änderungen die UI automatisch aktualisieren.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').Ref<object>}
 * @description Speichert die Informationen des aktuell angemeldeten Benutzers.
 * @todo Aktuell hartkodiert. Sollte durch einen API-Aufruf ersetzt werden.
 */
const loggedInUser = ref({ id: 1, name: 'Spieler 1' });

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

// ==================================================================================
// Methoden: Funktionen zur Handhabung von Benutzerinteraktionen und Geschäftslogik.
// ==================================================================================

/**
 * @function challengePlayer
 * @author Lisa
 * @description Löst das 'start-game'-Event aus und übergibt die Details zum Gegner und zum Level an die Eltern-Komponente.
 * @param {object} player - Das Spieler-Objekt des Gegners, der herausgefordert wird.
 */
function challengePlayer(player) {
  emit('start-game', { opponent: player, level: selectedLevel.value });
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
  localStorage.removeItem('token');
  window.location.href = '/login'; // Leitet die Seite neu, um den Zustand zurückzusetzen.
}

/**
 * @function fetchAvailablePlayers
 * @author Dima
 * @description Ruft die Liste der verfügbaren Spieler vom Server ab.
 * @returns {Promise<Array<object>>} - Ein Promise, das eine Liste von Spieler-Objekten zurückgibt.
 * @todo Aktuell nur eine Simulation. Muss durch einen echten fetch-API-Aufruf ersetzt werden.
 */
async function fetchAvailablePlayers() {
  console.log("Rufe verfügbare Spieler ab...");
  return new Promise(resolve => {
    setTimeout(() => {
      resolve([
        { id: 2, name: 'Spieler 2' },
        { id: 3, name: 'Spieler 3' },
        { id: 4, name: 'Spieler 4' },
        { id: 5, name: 'Spieler 5' },
        { id: 6, name: 'Spieler 6' },
        { id: 7, name: 'Spieler 7' },
      ]);
    }, 500);
  });
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
  availablePlayers.value = await fetchAvailablePlayers();
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
  <div class="bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

      <header class="flex flex-col sm:flex-row justify-between items-center mb-8">
        <div class="flex items-center gap-4 mb-4 sm:mb-0">
          <img src="../assets/logo.svg" alt="Logo" class="h-24 w-auto">
          <div class="text-2xl font-bold text-gray-800">Lobby</div>
        </div>
        <div class="flex items-center gap-4">
          <button @click="onHighscoresClick" class="flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
            <TrophyIcon class="h-6 w-6" />
            <span>Highscores</span>
          </button>
          <button @click="onHelpClick" class="flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
            <QuestionMarkCircleIcon class="h-6 w-6" />
            <span>Hilfe</span>
          </button>
          <button @click="logout" class="flex items-center gap-2 text-red-500 hover:text-red-700 font-semibold transition-colors">
            <ArrowRightOnRectangleIcon class="h-6 w-6" />
            <span>Abmelden</span>
          </button>
        </div>
      </header>

      <main class="grid grid-cols-1 lg:grid-cols-3 gap-8">

        <!-- Spalte für Spieleinstellungen -->
        <div class="lg:col-span-1 bg-white rounded-2xl shadow-lg p-6">
          <h2 class="text-2xl font-bold text-gray-800 mb-6">Spieleinstellungen</h2>
          
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

        </div>

        <!-- Spalte für verfügbare Spieler -->
        <div class="lg:col-span-2 bg-white rounded-2xl shadow-lg p-6">
          <div class="flex items-center gap-3 mb-6">
            <UsersIcon class="h-8 w-8 text-gray-500"/>
            <h2 class="text-2xl font-bold text-gray-800">Verfügbare Spieler</h2>
          </div>
          <div class="overflow-y-auto h-96 pr-2">
            <ul v-if="availablePlayers.length > 0" class="space-y-3">
              <li
                v-for="player in availablePlayers"
                :key="player.id"
                class="flex justify-between items-center p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors group"
              >
                <div class="flex items-center gap-4">
                    <div class="bg-gray-200 p-2 rounded-full">
                        <UserCircleIcon class="h-8 w-8 text-gray-600" />
                    </div>
                    <span class="text-lg font-medium text-gray-800">{{ player.name }}</span>
                </div>
                <button
                  @click="challengePlayer(player)"
                  class="bg-blue-500 text-white font-bold py-2 px-6 rounded-full hover:bg-blue-600 transition-all duration-200 transform opacity-0 group-hover:opacity-100 group-hover:scale-105 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 flex items-center gap-2"
                >
                  <span>Spielen</span>
                  <ChevronRightIcon class="h-5 w-5"/>
                </button>
              </li>
            </ul>
            <!-- Wird angezeigt, während die Spielerliste lädt -->
            <div v-else class="text-center text-gray-500 py-16">
              <p class="text-lg">Suche nach Spielern...</p>
            </div>
          </div>
        </div>

      </main>

    </div>
  </div>
</template>