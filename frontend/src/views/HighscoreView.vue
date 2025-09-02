<script setup>
import { ref, onMounted } from 'vue';
import { UserCircleIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['show-lobby']);
const loading = ref(false);
const loadError = ref(null);
const highscores = ref([]);
const loggedInUser = ref({ id: null, name: '' }); // Platzhalter für
// den aktuell eingeloggten Benutzer

function applyRanking(list) {
  return [...list] // Kopie, um Seiteneffekte zu vermeiden
      .sort((a, b) => b.score - a.score)
      .map((h, i) => ({ ...h, rank: i + 1 }));
}

async function fetchHighscores(controller = new AbortController()) {
  loading.value = true;
  loadError.value = null;
  try {
    const resp = await fetch('/api/auth/highscores', {
      headers: { Accept: 'application/json' },
      credentials: 'include',
      signal: controller.signal
    });
    if (!resp.ok) throw new Error('HTTP ' + resp.status);
    const data = await resp.json();
    console.log('Highscores raw:', data);
    const normalized = (Array.isArray(data) ? data : []).map(h => ({
      name: h.username ?? h.userName ?? h.name ?? h.id ?? 'Unbekannt',
      score: h.score ?? 0
    }));
    highscores.value = applyRanking(normalized);
  } catch (e) {
    loadError.value = e.message;
    highscores.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchHighscores();
});

function goBackToLobby() {
  emit('show-lobby');
}
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-center p-4">
    <header class="w-full max-w-2xl mx-auto text-center mb-8">
      <img src="../assets/logo.svg" alt="Wahr oder Watt Logo" class="mx-auto h-32 w-auto mb-6">
      <h1 class="text-5xl font-extrabold text-gray-800 tracking-tight">Highscores</h1>
    </header>

    <main class="w-full max-w-2xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden">
      <div class="bg-gray-800 text-white grid grid-cols-3 gap-4 font-semibold text-lg p-4">
        <div class="text-center">Rank</div>
        <div>User</div>
        <div class="text-right">Score</div>
      </div>
      
      <ul>
        <li 
          v-for="(entry, index) in highscores" 
          :key="entry.name"
          :class="['grid grid-cols-3 gap-4 items-center p-4 transition-colors', 
                   loggedInUser.name === entry.name ? 'bg-blue-100 font-bold' : 'hover:bg-gray-50',
                   index < highscores.length - 1 ? 'border-b border-gray-200' : '']"
        >
          <div class="text-center text-2xl font-bold text-gray-500">#{{ entry.rank }}</div>
          <div class="flex items-center gap-3">
            <UserCircleIcon class="h-8 w-8 text-gray-400" />
            <span class="text-gray-800">{{ entry.name }}</span>
          </div>
          <div class="text-right text-xl font-semibold text-gray-700">{{ entry.score }}</div>
        </li>
      </ul>
    </main>

    <footer class="w-full max-w-2xl mx-auto mt-8 text-center">
      <button 
        @click="goBackToLobby" 
        class="inline-flex items-center gap-2 bg-white text-gray-800 font-bold py-3 px-8 rounded-full shadow-md hover:bg-gray-200 transition-transform transform hover:-translate-y-1"
      >
        <ArrowUturnLeftIcon class="h-5 w-5" />
        <span>Zurück zur Lobby</span>
      </button>
    </footer>
  </div>
</template>
