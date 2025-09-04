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

function submitAnswers(isTimeout = false) {
  clearInterval(timerInterval);

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

  if (currentRound.value < maxRounds) {
    currentRound.value++;
    selectedAnswers.value = [];
    startTimer();
  } else {
    alert(`Spiel beendet! Endstand: ${loggedInPlayer.value.score}`);
  }
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

function goBackToLobby() {
  emit('show-lobby');
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

onMounted(() => {
  if (props.gameDetails) {
    startTimer();
  }
});

onUnmounted(() => {
  clearInterval(timerInterval);
});
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col p-2 sm:p-4" :style="containerStyle" :class="{ 'high-contrast': isHighContrast }">
    
    <header class="w-full max-w-4xl mx-auto">
        <div class="flex justify-end items-center gap-4 mb-2">
            <!-- BARRIEREFREIHEIT: Steuerelemente für Zoom und Kontrast. -->
            <div class="flex items-center gap-2">
                <span class="text-sm text-gray-600">Zoom:</span>
                <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
                <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">+</button>
            </div>
            <button @click="toggleHighContrast" class="px-3 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">Kontrast</button>
        </div>
      <div class="flex justify-center items-center mb-2 relative">
        <button @click="goBackToLobby" data-test="back-to-lobby-button" class="absolute left-0 flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
          <ArrowUturnLeftIcon class="h-6 w-6" />
          <span class="hidden sm:inline">Zurück zur Lobby</span>
        </button>
        <img src="../assets/logo.svg" alt="Wahr oder Watt Logo" class="h-20 sm:h-24 w-auto">
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
      <button @click="submitAnswers(false)" data-test="submit-button" class="bg-blue-500 hover:bg-blue-600 text-white font-bold text-xl sm:text-2xl py-3 px-12 sm:py-4 sm:px-16 rounded-full shadow-md transition-transform transform hover:scale-105">
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