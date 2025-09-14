<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { UserCircleIcon, ClockIcon, ArrowUturnLeftIcon, DevicePhoneMobileIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['show-lobby']);

const props = defineProps({
  gameDetails: { type: Object, required: true }
});

// State
const duel = computed(() => props.gameDetails?.duel);
const duelRounds = computed(() => duel.value?.rounds || []);
const currentRoundIndex = ref(0);
const currentRoundId = computed(() => duelRounds.value[currentRoundIndex.value]?.id);

const loggedInPlayer = ref({ id: 0, name: 'Spieler', score: 0 });
const opponentPlayer = computed(() => props.gameDetails.opponent || { name: 'Gegner', score: 0 });
const level = computed(() => props.gameDetails.level || 1);

const timer = ref(0);
let timerInterval = null;

const currentDevice = computed(() => duelRounds.value[currentRoundIndex.value]?.device || {
  name: 'Gerät',
  icon: '📱',
  attributes: [
    { id: 1, name: 'Leistung', icon: '⚡' },
    { id: 2, name: 'Gewicht', icon: '🏋️' },
    { id: 3, name: 'Preis', icon: '💰' },
    { id: 4, name: 'Größe', icon: '📏' }
  ],
  correctAttributes: [1, 3] // Beispiel: Leistung und Preis sind korrekt
});
const selectedAttributes = ref([]);
const isReady = ref(true);
const loading = ref(true);

const token = computed(() => localStorage.getItem('jwt'));
const maxRounds = computed(() => duelRounds.value.length || 5);

// Barrierefreiheit
const zoomLevel = ref(1);
const isHighContrast = ref(false);
const containerStyle = computed(() => ({ zoom: zoomLevel.value }));

function toggleAttribute(attrId) {
  const idx = selectedAttributes.value.indexOf(attrId);
  if (idx === -1) selectedAttributes.value.push(attrId);
  else selectedAttributes.value.splice(idx, 1);
}

function increaseZoom() { zoomLevel.value += 0.1; }
function decreaseZoom() { zoomLevel.value = Math.max(0.5, zoomLevel.value - 0.1); }
function toggleHighContrast() { isHighContrast.value = !isHighContrast.value; }

async function fetchCurrentUser() {
  const resp = await fetch('/api/users/me', { headers: { 'Authorization': `Bearer ${token.value}` } });
  if (!resp.ok) throw new Error('Fehler beim Laden des Benutzers');
  return await resp.json();
}

function startTimer() {
  clearInterval(timerInterval);
  timer.value = level.value === 1 ? 60 : 10;
  timerInterval = setInterval(() => {
    if (timer.value > 0) timer.value--;
    else submitAttributes(true);
  }, 1000);
}

async function submitAttributes(isTimeout = false) {
  clearInterval(timerInterval);

  let scoreForRound = 0;
  for (const attrId of selectedAttributes.value) {
    if (currentDevice.value.correctAttributes.includes(attrId)) scoreForRound++;
  }
  loggedInPlayer.value.score += scoreForRound;

  if (isTimeout) alert('Zeit abgelaufen!');
  else alert(`Du hast ${scoreForRound} Punkte erzielt!`);

  // API: Auswahl bestätigen
  await fetch(`/api/duel-rounds/${currentRoundId.value}/confirm-selection?playerId=${loggedInPlayer.value.id}`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token.value}` }
  });

  // API: Prüfen, ob beide bereit sind
  const resp = await fetch(`/api/duel-rounds/${currentRoundId.value}/ready-for-next?duelId=${props.gameDetails.id}`, {
    headers: { 'Authorization': `Bearer ${token.value}` }
  });
  const ready = await resp.json();

  if (ready) {
    if (currentRoundIndex.value < maxRounds.value - 1) {
      currentRoundIndex.value++;
      selectedAttributes.value = [];
      startTimer();
    } else {
      alert(`Spiel beendet! Endstand: ${loggedInPlayer.value.score}`);
      emit('show-lobby');
    }
  } else {
    isReady.value = false;
  }
}

async function goBackToLobby() {
  if (confirm('Möchtest du das Spiel wirklich verlassen?')) {
    try {
      await fetch(`/api/duels/${props.gameDetails.id}/leave?playerId=${loggedInPlayer.value.id}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token.value}` }
      });
    } catch {}
    emit('show-lobby');
  }
}

// Lifecycle
onMounted(async () => {
  loading.value = true;
  try {
    const me = await fetchCurrentUser();
    loggedInPlayer.value = { id: me.id, name: me.username, score: 0 };
    startTimer();
  } catch {}
  loading.value = false;
});

onUnmounted(() => clearInterval(timerInterval));
</script>

<template>
  <div :class="{ 'high-contrast': isHighContrast }" :style="containerStyle" class="min-h-screen bg-gray-100 flex flex-col">
    <header class="w-full max-w-4xl mx-auto flex justify-between items-center py-4">
      <button @click="goBackToLobby" class="flex items-center gap-2 text-red-600 hover:text-blue-600 font-semibold">
        <ArrowUturnLeftIcon class="h-6 w-6" />
        <span>Zurück zur Lobby</span>
      </button>
      <div class="flex items-center gap-2">
        <img src="../assets/gluehbirne.svg" alt="Logo" class="h-10 w-auto" />
        <span class="text-2xl font-bold text-gray-800">Wahr oder Watt?</span>
      </div>
      <div class="flex items-center gap-2">
        <span class="text-sm text-gray-600">Zoom:</span>
        <button @click="decreaseZoom" class="px-2 py-1 bg-gray-200 rounded">-</button>
        <button @click="increaseZoom" class="px-2 py-1 bg-gray-200 rounded">+</button>
        <button @click="toggleHighContrast" class="px-3 py-1 bg-gray-200 rounded">Kontrast</button>
      </div>
    </header>

    <main class="w-full max-w-4xl mx-auto flex-grow flex flex-col items-center justify-center">
      <div v-if="loading" class="text-center text-gray-500 py-16">Lädt...</div>
      <div v-else class="w-full">
        <!-- Fortschrittsbalken -->
        <div class="w-full bg-gray-300 rounded-full h-4 mb-6">
          <div :style="{ width: ((currentRoundIndex + 1) / maxRounds * 100) + '%' }"
               class="bg-blue-500 h-4 rounded-full transition-all"></div>
        </div>

        <!-- Spieler & Gegner -->
        <div class="grid grid-cols-2 gap-6 mb-6">
          <div class="flex items-center gap-3 bg-white rounded-xl shadow p-4">
            <UserCircleIcon class="h-8 w-8 text-blue-600" />
            <div>
              <div class="font-bold text-lg text-gray-800">{{ loggedInPlayer.name }}</div>
              <div class="text-blue-600 font-semibold">Score: {{ loggedInPlayer.score }}</div>
            </div>
          </div>
          <div class="flex items-center gap-3 bg-white rounded-xl shadow p-4 justify-end">
            <div>
              <div class="font-bold text-lg text-gray-800 text-right">{{ opponentPlayer.name }}</div>
              <div class="text-gray-500 font-semibold text-right">Score: 0</div>
            </div>
            <UserCircleIcon class="h-8 w-8 text-gray-500" />
          </div>
        </div>

        <!-- Timer & Rundenanzeige -->
        <div class="flex items-center justify-center gap-3 mb-6">
          <ClockIcon class="h-8 w-8 text-gray-500" />
          <span class="text-3xl font-mono font-bold text-gray-800">{{ timer.toString().padStart(2, '0') }}s</span>
          <span class="text-gray-600 font-semibold ml-4">
            <template v-if="level === 1">Speedrun</template>
            <template v-else>Runde {{ currentRoundIndex + 1 }} / {{ maxRounds }}</template>
          </span>
        </div>

        <!-- Gerät anzeigen -->
        <div class="bg-white rounded-2xl shadow-lg p-6 mb-6 text-center flex flex-col items-center">
          <div class="text-7xl mb-2">{{ currentDevice.icon }}</div>
          <div class="text-2xl font-bold text-gray-800 mb-2">{{ currentDevice.name }}</div>
          <div class="text-gray-500 text-base">Wähle die passenden Attribute:</div>
        </div>

        <!-- Attribute zur Auswahl -->
        <div class="grid grid-cols-2 gap-4 mb-6">
          <button
            v-for="attr in currentDevice.attributes"
            :key="attr.id"
            @click="toggleAttribute(attr.id)"
            :class="[
              'p-4 rounded-2xl border-2 transition-all flex flex-col items-center',
              selectedAttributes.includes(attr.id)
                ? 'bg-blue-100 border-blue-500 scale-105'
                : 'bg-gray-50 border-gray-200 hover:border-gray-300'
            ]"
          >
            <span class="text-4xl">{{ attr.icon }}</span>
            <span class="font-semibold text-base text-gray-700">{{ attr.name }}</span>
          </button>
        </div>

        <!-- Antwort abschicken -->
        <button
          @click="submitAttributes(false)"
          :disabled="selectedAttributes.length === 0"
          :class="[
            'w-full font-bold text-xl py-3 rounded-full shadow transition-transform',
            selectedAttributes.length === 0
              ? 'bg-gray-400 text-gray-200 cursor-not-allowed'
              : 'bg-blue-500 text-white hover:bg-blue-600 hover:scale-105'
          ]"
        >
          OK
        </button>

        <!-- Wartebildschirm -->
        <div v-if="!isReady" class="mt-8 text-center">
          <div class="text-xl text-gray-700 mb-4">Warte auf Gegner...</div>
          <button @click="goBackToLobby" class="px-6 py-3 bg-red-500 text-white rounded-lg font-bold">
            Zurück zur Lobby
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<style>
.high-contrast {
  background-color: #000 !important;
  color: #fff !important;
}
.high-contrast .bg-white, .high-contrast .bg-gray-50, .high-contrast .bg-blue-100 {
  background-color: #000 !important;
  border: 2px solid yellow !important;
}
.high-contrast .text-gray-800, .high-contrast .text-gray-700, .high-contrast .text-gray-600, .high-contrast .text-gray-500, .high-contrast .text-blue-600 {
  color: #fff !important;
}
.high-contrast .bg-gray-100, .high-contrast .bg-gray-200 {
    background-color: #333 !important;
}
.high-contrast button {
    border: 1px solid yellow !important;
}
.high-contrast .border-blue-500 {
    border-color: yellow !important;
}
</style>