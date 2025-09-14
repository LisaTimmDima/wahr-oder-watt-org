<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: Komplette UI- und Spiellogik, Timer, State-Management, Event-Handling.
// - Dima:  Implementierung der API-Aufrufe zum Abrufen der Fragen.
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, computed, onMounted, onUnmounted) und Icon-Komponenten.
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { UserCircleIcon, ClockIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Emits: Deklariert Events, um mit der Eltern-Komponente (App.vue) zu kommunizieren.
// ==================================================================================
const emit = defineEmits(['show-lobby']);

// ==================================================================================
// Props: Deklariert die Daten, die von der Eltern-Komponente (App.vue) an diese Komponente übergeben werden.
// ==================================================================================
const props = defineProps({
  gameDetails: {
    type: Object,
    required: true
  }
});

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen für den Spielzustand.
// ==================================================================================

const duel = computed(() => props.gameDetails?.duel);
const duelRounds = computed(() => props.gameDetails?.duel?.rounds || []);
const currentRoundId = computed(() => duelRounds.value[currentRoundIndex.value]?.id);

const isReady = ref(true);
const currentRoundIndex = ref(0);

//onMounted(async () => {
//  if (currentRoundId.value && props.gameDetails.id) {
//      isReady.value = await checkReadyForNextRound(currentRoundId.value, props.gameDetails.id);
//    } else {
//      isReady.value = false; // oder ein Ladezustand
//    }
//});

const loggedInPlayer = ref({ name: 'Spieler 1', score: 0 });
const timer = ref(0);
const currentRound = ref(1);
const maxRounds = 5;
let timerInterval = null;
const currentQuestion = ref({
  item: { name: 'Desktop-PC', icon: '🖥️' },
  answers: [
    { id: 'e1', icon: '🛜'},
    { id: 'e2', icon: '🔌'},
    { id: 'e3', icon: '📀'},
    { id: 'e4', icon: '⌨️'},
  ],
  correctAnswers: ['e2', 'e4']
});
const selectedAnswers = ref([]);
const loading = ref(true);
const token = computed(() => localStorage.getItem('jwt'));


// BARRIEREFREIHEIT: Reaktive Variablen für Zoom und Kontrast.
const zoomLevel = ref(1);
const isHighContrast = ref(false);

// ==================================================================================
// Computed Properties: Abgeleitete, reaktive Werte.
// ==================================================================================

const opponentPlayer = computed(() => props.gameDetails.opponent);
const level = computed(() => props.gameDetails.level);
const isSelected = computed(() => {
  return (answerId) => selectedAnswers.value.includes(answerId);
});
const containerStyle = computed(() => ({ zoom: zoomLevel.value }));

// ==================================================================================
// Methoden: Funktionen zur Steuerung der Spiellogik.
// ==================================================================================

function toggleAnswer(answerId) {
  const index = selectedAnswers.value.indexOf(answerId);
  if (index === -1) {
    selectedAnswers.value.push(answerId);
  } else {
    selectedAnswers.value.splice(index, 1);
  }
}

async function checkGameStatus() {
  if (!props.gameDetails.id || isNaN(Number(props.gameDetails.id))) {
    alert("Fehler: Duell-ID ist nicht gesetzt!");
    emit('show-lobby');
    return true;
  }
  const resp = await fetch(`/api/duels/${props.gameDetails.id}/check-status`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token.value}` }
  });
  const status = await resp.json();
  if (status === 'FINISHED') {
    alert("Das Spiel ist beendet!");
    emit('show-lobby');
    return true;
  }
  return false;
}

async function submitAnswers(isTimeout = false) {
  clearInterval(timerInterval);

  // Speedrun-Modus: Zeit serverseitig prüfen
  if (level.value === 1) {
    const resp = await fetch(`/api/duels/${props.gameDetails.id}/check-time`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token.value}` }
    });
    const isFinished = await resp.json();
    if (isFinished) {
      alert("Zeit abgelaufen! Das Duell ist beendet.");
      emit('show-lobby');
      return;
    }
  }

  if (isTimeout) {
    alert("Zeit abgelaufen!");
  }

  let scoreForRound = 0;
  for (const answerId of selectedAnswers.value) {
    if (currentQuestion.value.correctAnswers.includes(answerId)) {
      scoreForRound++;
    }
  }

  loggedInPlayer.value.score += scoreForRound;

  alert(`Du hast in dieser Runde ${scoreForRound} Punkte erzielt! Gesamt: ${loggedInPlayer.value.score}`);

  // Status nach jedem Schritt prüfen
  const finished = await checkGameStatus();
  if (finished) return;

  // Prüfe, ob beide Spieler bereit sind
  const roundId = currentRoundId.value; // oder die tatsächliche roundId aus deinen Daten
  const duelId = props.gameDetails.id;
  const isReady = await checkReadyForNextRound(roundId, duelId);

  if (isReady) {
    // Nächste Runde starten
    if (ccurrentRoundId.value < maxRounds) {
      currentRoundId.value++;
      selectedAnswers.value = [];
      startTimer();
    } else {
      alert(`Spiel beendet! Endstand: ${loggedInPlayer.value.score}`);
      emit('show-lobby');
    }
  } else {
    // Optional: Warte auf den anderen Spieler (z.B. Polling starten)
    // Zeige eine Nachricht: "Warte auf den anderen Spieler..."
  }
}

async function fetchCurrentUser() {
  const resp = await fetch('/api/users/me', { headers: { 'Accept': 'application/json', 'Authorization': `Bearer ${token.value}` } });
  if (!resp.ok) throw new Error('Fehler beim Laden der Benutzer');
  return await resp.json();
}

function startTimer() {
  clearInterval(timerInterval);
  timer.value = level.value === 1 ? 60 : 10;
  timerInterval = setInterval(() => {
    if (timer.value > 0) {
      timer.value--;
    } else {
      submitAnswers(true);
    }
  }, 1000);
}

async function goBackToLobby() {
  if (confirm("Möchtest du das Spiel wirklich verlassen? Dein aktueller Punktestand geht verloren.")) {
    try {
          await fetch(`/api/duels/${props.gameDetails.id}/leave?playerId=${loggedInPlayer.value.id}`, {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token.value}`,
              'Content-Type': 'application/json'
            }
          });
        } catch (e) {
          alert("Fehler beim Verlassen des Spiels.");
        }
    emit('show-lobby');
  }
}

async function checkReadyForNextRound(roundId, duelId) {
  const token = localStorage.getItem('jwt');
  const resp = await fetch(`/api/duel-rounds/${roundId}/ready-for-next?duelId=${duelId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Accept': 'application/json'
    }
  });
  if (!resp.ok) throw new Error('Fehler beim Prüfen des Rundenstatus');
  return await resp.json(); // true oder false
}

async function confirmSelection() {
  try {
    // API-Call zur Bestätigung der Auswahl
    await fetch(`/api/duel-rounds/${currentRoundId.value}/confirm-selection?playerId=${loggedInPlayer.value.id}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token.value}`
      }
    });
    // Status neu abfragen
    isReady.value = await checkReadyForNextRound(currentRoundId.value, props.gameDetails.id);
  } catch (e) {
    alert('Fehler beim Bestätigen der Auswahl: ' + e.message);
  }
}

// BARRIEREFREIHEIT: Methoden
function increaseZoom() {
  zoomLevel.value += 0.1;
}
function decreaseZoom() {
  zoomLevel.value -= 0.1;
}
function toggleHighContrast() {
  isHighContrast.value = !isHighContrast.value;
}

// ==================================================================================
// Lifecycle Hooks
// ==================================================================================

onMounted(async () => {
  if (props.gameDetails && isReady) {
    startTimer();
  }
  loading.value = true;
  // Aktuellen User laden (Backend) mit Fallback auf localStorage
  try {
    const me = await fetchCurrentUser();
    loggedInPlayer.value = {
      id: me.id,
      name: me.username,
    };
  } catch {
    loggedInPlayer.value = {
      id: Number(localStorage.getItem('currentUserId')),
      name: localStorage.getItem('currentUsername')
    };
  }
});

onUnmounted( () => {
  clearInterval(timerInterval);
});
</script>

<template>
  <div v-if="!isReady" class="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <div class="text-2xl text-gray-700 mb-4">Warte auf Gegner...</div>
      <button @click="confirmSelection" class="px-6 py-3 bg-blue-500 text-white rounded-lg font-bold">
        Auswahl bestätigen
      </button>
      <button @click="goBackToLobby" class="mt-4 px-6 py-3 bg-red-500 text-white rounded-lg font-bold">
        Zurück zur Lobby
      </button>
    </div>
  <div v-else class="bg-gray-100 min-h-screen flex flex-col p-2 sm:p-4" :style="containerStyle" :class="{ 'high-contrast': isHighContrast }">

        <header class="w-full max-w-4xl mx-auto">
      <div class="flex justify-between items-center mb-4">
        <button @click="goBackToLobby" data-test="back-to-lobby-button" class="flex items-center gap-2 text-red-600 hover:text-blue-600 font-semibold transition-colors">
          <ArrowUturnLeftIcon class="h-6 w-6" />
          <span class="hidden sm:inline">Zurück zur Lobby</span>
        </button>

        <div class="flex items-center gap-2">
          <img src="../assets/gluehbirne.svg" alt="Wahr oder Watt Logo" class="h-12 w-auto" style="transform: scale(0.8);">
          <h1 class="text-2xl font-bold text-gray-800">Wahr oder Watt?</h1>
        </div>

        <div class="flex items-center gap-4">
            <!-- BARRIEREFREIHEIT: Steuerelemente für Zoom und Kontrast. -->
            <div class="flex items-center gap-2">
                <span class="text-sm text-gray-600">Zoom:</span>
                <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
                <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">+</button>
            </div>
            <button @click="toggleHighContrast" class="px-3 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">Kontrast</button>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-md p-2 sm:p-4 grid grid-cols-3 items-center gap-2 sm:gap-4">

        <!-- Angemeldeter Spieler -->
        <div class="flex items-center gap-2 sm:gap-3">
          <div class="bg-gray-200 p-1 sm:p-2 rounded-full">
            <UserCircleIcon class="h-6 w-6 sm:h-8 sm:w-8 text-blue-600" />
          </div>
          <div>
            <h2 class="text-base sm:text-xl font-bold text-gray-800">{{ loggedInPlayer.name }}</h2>
            <p class="text-sm sm:text-lg font-semibold text-blue-600">Score: {{ loggedInPlayer.score }}</p>
          </div>
        </div>

        <!-- Timer und Rundenanzeige -->
        <div class="text-center">
          <div class="flex items-center justify-center gap-1 sm:gap-2">
            <ClockIcon class="h-6 w-6 sm:h-8 sm:w-8 text-gray-500" />
            <span class="text-2xl sm:text-4xl font-mono font-bold text-gray-800">{{ timer.toString().padStart(2, '0') }}s</span>
          </div>
          <div class="text-xs sm:text-sm font-semibold text-gray-600">
              <div v-if="level === 1">Gesamtzeit</div>
              <div v-if="level === 2">Runde {{ currentRound }} / {{ maxRounds }}</div>
          </div>
        </div>

        <!-- Gegner -->
        <div class="flex items-center justify-end gap-2 sm:gap-3">
          <div class="text-right">
            <h2 class="text-base sm:text-xl font-bold text-gray-800">{{ opponentPlayer.name }}</h2>
            <p class="text-sm sm:text-lg font-semibold text-gray-500">Score: 0</p>
          </div>
          <div class="bg-gray-200 p-1 sm:p-2 rounded-full">
            <UserCircleIcon class="h-6 w-6 sm:h-8 sm:w-8 text-gray-500" />
          </div>
        </div>

      </div>
    </header>

    <main class="w-full max-w-4xl mx-auto flex-grow flex flex-col items-center justify-center mt-4">

      <!-- Aktuelle Frage -->
      <div class="bg-white rounded-2xl shadow-lg p-4 sm:p-6 mb-4 sm:mb-6 text-center w-full">
        <div class="text-6xl sm:text-7xl mb-2">{{ currentQuestion.item.icon }}</div>
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-800">{{ currentQuestion.item.name }}</h1>
      </div>

      <!-- Antwortmöglichkeiten -->
      <div class="grid grid-cols-2 gap-3 sm:gap-4 w-full mb-4 sm:mb-6">
        <button
          v-for="answer in currentQuestion.answers"
          :key="answer.id"
          @click="toggleAnswer(answer.id)"
          :data-test="`answer-button-${answer.id}`"
          :class="[
            'p-4 rounded-2xl border-2 sm:border-4 transition-all duration-150',
            'flex flex-col items-center justify-center gap-2',
            isSelected(answer.id)
              ? 'bg-blue-100 border-blue-500 shadow-md scale-105'
              : 'bg-gray-50 border-gray-200 hover:border-gray-300'
          ]"
        >
          <span class="text-4xl sm:text-5xl">{{ answer.icon }}</span>
          <span class="font-semibold text-sm sm:text-base text-gray-700">{{ answer.text }}</span>
        </button>
      </div>

      <!-- Antwort abschicken Button -->
      <button @click="submitAnswers(false)"
              data-test="submit-button"
              :disabled="selectedAnswers.length === 0"
              :class="[
                'font-bold text-xl sm:text-2xl py-3 px-12 sm:py-4 sm:px-16 rounded-full shadow-md transition-transform transform',
                selectedAnswers.length === 0
                  ? 'bg-gray-400 text-gray-200 cursor-not-allowed'
                  : 'bg-blue-500 text-white hover:bg-blue-600 hover:scale-105'
              ]">
        OK
      </button>

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
  border: 2px solid yellow;
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