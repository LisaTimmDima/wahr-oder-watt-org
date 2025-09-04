<script setup>
import {ref, onMounted, computed} from 'vue';
import { UserCircleIcon, TrophyIcon, QuestionMarkCircleIcon, ArrowRightOnRectangleIcon, UsersIcon, ChevronRightIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['start-game', 'show-help', 'show-highscores'])
const loggedInUser = ref({ id: 0, name: 'Lädt...' });
const availablePlayers = ref([]);
const selectedLevel = ref(1);
const loading = ref(false);
const error = ref(null);
const token = computed(() => localStorage.getItem('jwt'));

async function fetchCurrentUser() {
  const resp = await fetch('/api/users/me', { headers: { 'Accept': 'application/json', 'Authorization': `Bearer ${token.value}` } });
  if (!resp.ok) throw new Error('Fehler beim Laden der Benutzer');
  return await resp.json();
}

function challengePlayer(player) {
  emit('start-game', { opponent: player, level: selectedLevel.value });
}

function onHelpClick() {
  emit('show-help');
}

function onHighscoresClick() {
  emit('show-highscores');
}

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

onMounted(async () => {
  loading.value = true;
  try {
    // Aktuellen User laden (Backend) mit Fallback auf localStorage
    try {
      const me = await fetchCurrentUser();
      loggedInUser.value = {
        id: me.id,
        name: me.username,
      };
    } catch (e) {
      loggedInUser.value = {
        id: Number(localStorage.getItem('currentUserId')),
        name: localStorage.getItem('currentUsername')
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
  <div class="bg-gray-100 min-h-screen font-sans">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

      <header class="flex flex-col sm:flex-row justify-between items-center mb-8">
        <div class="flex items-center gap-4 mb-4 sm:mb-0">
          <img src="../assets/logo.svg" alt="Logo" class="h-16 w-auto">
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
            <div v-else class="text-center text-gray-500 py-16">
              <p class="text-lg">Suche nach Spielern...</p>
            </div>
          </div>
        </div>

      </main>

    </div>
  </div>
</template>
