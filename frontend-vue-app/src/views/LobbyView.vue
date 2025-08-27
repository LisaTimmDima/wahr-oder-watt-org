<script setup>
import { ref, onMounted } from 'vue';
// Um die Icons zu nutzen
import { UserCircleIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['start-game', 'show-help'])

// Mock-Daten für den eingeloggten User
const loggedInUser = ref({
  id: 1,
  name: 'Spieler 1',
});

// Ref für die Liste der verfügbaren Spieler (wird später vom Backend befüllt)
const availablePlayers = ref([]);

// Funktion, um einen Spieler herauszufordern
function challengePlayer(player) {
  alert(`Du hast ${player.name} zu einem Duell herausgefordert!`);
  // Sende das 'start-game'-Ereignis und gib das Spieler-Objekt mit.
  emit('start-game', player);
}

// Funktion, wenn der Hilfe-Button geklickt wird
function onHelpClick() {
    emit('show-help')
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
      
      <header class="flex justify-between items-center mb-10">
        <div class="flex items-center gap-3">
          <UserCircleIcon class="h-10 w-10 text-gray-500" />
          <span class="text-xl font-semibold text-gray-800">{{ loggedInUser.name }}</span>
        </div>
        <div class="w-24 h-16 bg-gray-100 rounded-lg flex items-center justify-center">
          <img src="../assets/logo.svg" alt="Logo" class="h-30 w-40">
        </div>
      </header>

      <main class="flex flex-col items-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-8">Verfügbare Spieler</h1>

        <div class="w-full max-w-lg border border-gray-200 rounded-lg p-6">
          
          <div v-if="availablePlayers.length === 0" class="text-center text-gray-500">
            Suche nach Spielern...
          </div>
          
          <ul v-else class="space-y-4">
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
        </div>
      </main>

      <footer class="flex justify-center items-center gap-6 mt-12 pt-6 border-t border-gray-200">
        <button class="bg-gray-200 text-gray-800 font-bold py-3 px-8 rounded-lg hover:bg-gray-300 transition-colors">
          Highscores
        </button>
        <button @click="onHelpClick" class="bg-gray-200 text-gray-800 font-bold py-3 px-8 rounded-lg hover:bg-gray-300 transition-colors">
          Hilfe
        </button>
      </footer>
      
    </div>
  </div>
</template>