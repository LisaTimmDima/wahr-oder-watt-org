<script setup>
import { ref, onMounted } from 'vue';
// Um die Icons zu nutzen
import { UserCircleIcon } from '@heroicons/vue/24/solid';

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

// const showHighscores = ref(false); // Steuert die Sichtbarkeit des Dashboards
// const highscores = ref([
//   { rank: 1, name: 'CyberChampion', score: 15400 },
//   { rank: 2, name: 'LogicLord', score: 12100 },
//   { rank: 3, name: 'Spieler 4', score: 11800 },
//   { rank: 4, name: 'Spieler 2', score: 9500 },
//   { rank: 5, name: 'Spieler 1', score: 8200 },
// ]);

// Funktion, um einen Spieler herauszufordern
function challengePlayer(player) {
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

// Mock-Funktion, die eine Backend-Anfrage simuliert
function fetchAvailablePlayers() {
  console.log("Lade verfügbare Spieler...");
  // Simuliert eine Netzwerk-Antwort
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
  <div class="bg-gray-50 min-h-screen p-4 sm:p-8">
    <div class="max-w-4xl mx-auto bg-white p-6 rounded-2xl shadow-sm">
      
      <header class="flex justify-between items-center mb-6">
        <div class="flex items-center gap-3">
          <UserCircleIcon class="h-10 w-10 text-gray-500" />
          <span class="text-xl font-semibold text-gray-800">{{ loggedInUser.name }}</span>
        </div>
        <div class="w-24 h-16 bg-gray-100 rounded-lg flex items-center justify-center">
          <img src="../assets/logo.svg" alt="Logo" class="h-10">
        </div>
      </header>

      <main class="flex flex-col items-center">
        
        <div class="flex items-center justify-center gap-4 my-6">
          
          <div class="relative group">
            <span :class="['font-semibold cursor-pointer', selectedLevel === 1 ? 'text-blue-600' : 'text-gray-500']">
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
              'relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
              selectedLevel === 2 ? 'bg-blue-600' : 'bg-gray-200'
            ]"
          >
            <span
              :class="[
                'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                selectedLevel === 2 ? 'translate-x-5' : 'translate-x-0'
              ]"
            />
          </button>

          <div class="relative group">
            <span :class="['font-semibold cursor-pointer', selectedLevel === 2 ? 'text-blue-600' : 'text-gray-500']">
              Level 2
            </span>
             <div class="absolute bottom-full mb-2 w-72 p-3 bg-gray-800 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none left-1/2 -translate-x-1/2 z-10">
              Gegeneinander auf Rundenzeit (5 Runden, jede Runde 10 Sekunden).
              <div class="absolute top-full left-1/2 -translate-x-1/2 w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-gray-800"></div>
            </div>
          </div>
          </div>
        <h1 class="text-4xl font-bold text-gray-900 mb-8">Verfügbare Spieler</h1>

        <div class="w-full max-w-lg border border-gray-200 rounded-lg p-6">
          <ul v-if="availablePlayers.length > 0" class="space-y-4">
            <li 
              v-for="player in availablePlayers" 
              :key="player.id"
              class="flex justify-between items-center p-3 bg-gray-50 rounded-md"
            >
              <span class="text-lg text-gray-700">{{ player.name }}</span>
              <button 
                @click="challengePlayer(player)"
                class="bg-white border border-gray-300 text-gray-800 font-semibold py-2 px-6 rounded-lg hover:bg-gray-100 transition-colors"
              >
                Spielen
              </button>
            </li>
          </ul>
           <div v-else class="text-center text-gray-500">
            Suche nach Spielern...
          </div>
        </div>
      </main>

      <footer class="flex justify-center items-center gap-6 mt-12 pt-6 border-t border-gray-200">
        <button @click="onHighscoresClick" class="bg-gray-200 text-gray-800 font-bold py-3 px-8 rounded-lg hover:bg-gray-300">
          Highscores
        </button>
        <button @click="onHelpClick" class="bg-gray-200 text-gray-800 font-bold py-3 px-8 rounded-lg hover:bg-gray-300">
          Hilfe
        </button>
      </footer>
      
    </div>
  </div>
</template>