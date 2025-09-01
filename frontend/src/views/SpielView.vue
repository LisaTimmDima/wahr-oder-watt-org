<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { UserCircleIcon, ClockIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['show-lobby']);

const props = defineProps({
  gameDetails: {
    type: Object,
    required: true
  }
});

const loggedInPlayer = ref({ name: 'Spieler 1', score: 0 });
const opponentPlayer = computed(() => props.gameDetails.opponent);
const level = computed(() => props.gameDetails.level);

const timer = ref(0);
const currentRound = ref(1);
const maxRounds = 5;
let timerInterval = null;

const currentQuestion = ref({
  item: { name: 'Monitor', icon: '🖥️' },
  answers: [
    { id: 'e1', icon: '🛜', text: 'WLAN' },
    { id: 'e2', icon: '🔌', text: 'Netzstecker' },
    { id: 'e3', icon: '📀', text: 'CD-Laufwerk' },
    { id: 'e4', icon: '⌨️', text: 'Tastatur' },
  ],
  correctAnswers: ['e2']
});

const selectedAnswers = ref([]);

function toggleAnswer(answerId) {
  const index = selectedAnswers.value.indexOf(answerId);
  if (index === -1) {
    selectedAnswers.value.push(answerId);
  } else {
    selectedAnswers.value.splice(index, 1);
  }
}

const isSelected = computed(() => {
  return (answerId) => selectedAnswers.value.includes(answerId);
});

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

  alert(`You scored ${scoreForRound} points in this round! Total score: ${loggedInPlayer.value.score}`);

  if (currentRound.value < maxRounds) {
    currentRound.value++;
    selectedAnswers.value = [];
    startTimer();
  } else {
    alert(`Game Over! Final Score: ${loggedInPlayer.value.score}`);
    // Handle end of game
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
  <div class="bg-gray-100 min-h-screen flex flex-col p-2 sm:p-4">
    
    <header class="w-full max-w-4xl mx-auto">
      <div class="flex justify-center items-center mb-2 relative">
        <button @click="goBackToLobby" data-test="back-to-lobby-button" class="absolute left-0 flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
          <ArrowUturnLeftIcon class="h-6 w-6" />
          <span class="hidden sm:inline">Zurück zur Lobby</span>
        </button>
        <img src="../assets/logo.svg" alt="Wahr oder Watt Logo" class="h-20 sm:h-24 w-auto">
      </div>
      <div class="bg-white rounded-xl shadow-md p-2 sm:p-4 grid grid-cols-3 items-center gap-2 sm:gap-4">
        
        <div class="flex items-center gap-2 sm:gap-3">
          <div class="bg-gray-200 p-1 sm:p-2 rounded-full">
            <UserCircleIcon class="h-6 w-6 sm:h-8 sm:w-8 text-blue-600" />
          </div>
          <div>
            <h2 class="text-base sm:text-xl font-bold text-gray-800">{{ loggedInPlayer.name }}</h2>
            <p class="text-sm sm:text-lg font-semibold text-blue-600">Score: {{ loggedInPlayer.score }}</p>
          </div>
        </div>

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
      
      <div class="bg-white rounded-2xl shadow-lg p-4 sm:p-6 mb-4 sm:mb-6 text-center w-full">
        <div class="text-6xl sm:text-7xl mb-2">{{ currentQuestion.item.icon }}</div>
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-800">{{ currentQuestion.item.name }}</h1>
      </div>

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

      <button @click="submitAnswers(false)" data-test="submit-button" class="bg-blue-500 hover:bg-blue-600 text-white font-bold text-xl sm:text-2xl py-3 px-12 sm:py-4 sm:px-16 rounded-full shadow-md transition-transform transform hover:scale-105">
        OK
      </button>

    </main>

  </div>
</template>
