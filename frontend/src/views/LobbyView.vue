<script setup>
import { ref, onMounted } from 'vue';
import { UserCircleIcon, TrophyIcon, QuestionMarkCircleIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['start-game', 'show-help', 'show-highscores'])

// Mock-Daten für den eingeloggten User
const loggedInUser = ref({
  id: 1,
  name: 'Spieler 1',
});

// Ref für die Liste der verfügbaren Spieler (wird später vom Backend befüllt)
const availablePlayers = ref([]);

// speichern das ausgewählte Level (1 oder 2). Standard ist 1.
const selectedLevel = ref(1);

// Funktion, um einen Spieler herauszufordern
function challengePlayer(player) {
  console.log('LobbyView: Sende "start-game" Signal...');
  emit('start-game', {
    opponent: player,
    level: selectedLevel.value
  });
}

// Funktion, wenn der Hilfe-Button geklickt wird
function onHelpClick() {
    emit('show-help')
}

//  function fuer  highscores button
function onHighscoresClick() {
  emit('show-highscores');
}

function logout() {
  localStorage.removeItem('token');
  window.location.href = '/login';
}

// Mock-Funktion, die eine Backend-Anfrage simuliert
function fetchAvailablePlayers() {
  console.log("Lade verfügbare Spieler...");
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
    }, 500); //  Verzögerung
  });
}

// Lifecycle Hook: Wird ausgeführt, wenn die Komponente geladen wird
onMounted(async () => {
  availablePlayers.value = await fetchAvailablePlayers();
});
</script>

<template>
  <div class="bg-gray-100 min-h-screen">
    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-12">

      <!-- Header -->
      <header class="flex justify-between items-center mb-10">
        <div class="flex items-center gap-4">
          <UserCircleIcon class="h-12 w-12 text-gray-500" />
          <div>
            <p class="text-sm text-gray-600">Angemeldet als</p>
            <span class="text-xl font-bold text-gray-900">{{ loggedInUser.name }}</span>
          </div>
          <button @click="logout" class="ml-4 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
            Abmelden
          </button>
        </div>
        <img src="../assets/logo.svg" alt="Logo" class="h-32">
      </header>

      <main class="bg-white rounded-2xl shadow-lg p-8">

        <!-- Footer -->
        <footer class="flex justify-center items-center gap-16 mb-10 pb-8 border-b border-gray-200">
          <button @click="onHighscoresClick" class="flex items-center gap-2 text-gray-600 hover:text-gray-900 font-semibold transition-colors">
            <TrophyIcon class="h-6 w-6" />
            <span>Highscores</span>
          </button>
          <button @click="onHelpClick" class="flex items-center gap-2 text-gray-600 hover:text-gray-900 font-semibold transition-colors">
            <QuestionMarkCircleIcon class="h-6 w-6" />
            <span>Hilfe</span>
          </button>
        </footer>

        <!-- Level Selector -->
        <div class="flex items-center justify-center gap-6 mb-10">
          <div class="relative group text-center">
            <span :class="['font-semibold text-lg cursor-pointer transition-colors', selectedLevel === 1 ? 'text-blue-600' : 'text-gray-500 hover:text-gray-800']">
              Level 1
            </span>
            <div class="absolute bottom-full mb-2 w-72 p-3 bg-gray-800 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none left-1/2 -translate-x-1/2 z-10">
              Gegeneinander auf Gesamtzeit (60 Sekunden Gesamtzeit, beliebig viele Fragen).
              <div class="absolute top-full left-1/2 -translate-x-1/2 w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-gray-800"></div>
            </div>
          </div>
          <button
            @click="selectedLevel = selectedLevel === 1 ? 2 : 1"
            :class="[
              'relative inline-flex h-7 w-14 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
              selectedLevel === 2 ? 'bg-blue-600' : 'bg-gray-300'
            ]"
          >
            <span
              :class="[
                'pointer-events-none inline-block h-6 w-6 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                selectedLevel === 2 ? 'translate-x-7' : 'translate-x-0'
              ]"
            />
          </button>
          <div class="relative group text-center">
            <span :class="['font-semibold text-lg cursor-pointer transition-colors', selectedLevel === 2 ? 'text-blue-600' : 'text-gray-500 hover:text-gray-800']">
              Level 2
            </span>
             <div class="absolute bottom-full mb-2 w-72 p-3 bg-gray-800 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none left-1/2 -translate-x-1/2 z-10">
              Gegeneinander auf Rundenzeit (5 Runden, jede Runde 10 Sekunden).
              <div class="absolute top-full left-1/2 -translate-x-1/2 w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-gray-800"></div>
            </div>
          </div>
        </div>

        <!-- Player List -->
        <div class="w-full">
          <h2 class="text-2xl font-bold text-center text-gray-800 mb-6">Verfügbare Spieler</h2>
          <div class="w-full max-w-xl mx-auto border border-gray-200 rounded-xl p-4">
            <ul v-if="availablePlayers.length > 0" class="space-y-3">
              <li
                v-for="player in availablePlayers"
                :key="player.id"
                class="flex justify-between items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
              >
                <span class="text-lg font-medium text-gray-800">{{ player.name }}</span>
                <button
                  @click="challengePlayer(player)"
                  class="bg-blue-500 text-white font-bold py-2 px-6 rounded-lg hover:bg-blue-600 transition-all duration-200 transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                >
                  Spielen
                </button>
              </li>
            </ul>
            <div v-else class="text-center text-gray-500 py-8">
              <p>Suche nach Spielern...</p>
            </div>
          </div>
        </div>

      </main>

    </div>
  </div>
</template>