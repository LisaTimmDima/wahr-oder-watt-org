<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { UserCircleIcon, ClockIcon } from '@heroicons/vue/24/solid';

// Empfängt die Spieldetails (Gegner & Level) von App.vue
const props = defineProps({
  gameDetails: {
    type: Object,
    required: true
  }
});

// Spieler-Daten werden jetzt aus den props abgeleitet
const loggedInPlayer = ref({ name: 'Spieler 1' }); 
const opponentPlayer = computed(() => props.gameDetails.opponent);
const level = computed(() => props.gameDetails.level);

// Zustand für die Spiel-Logik
const timer = ref(0);
const currentRound = ref(1);
const maxRounds = 5;
let timerInterval = null;

// Mock-Frage
const currentQuestion = ref({
  item: { name: 'Monitor', icon: '🖥️' },
  answers: [
    { id: 'a1', icon: '🛜' },
    { id: 'a2', icon: '🔌' },
    { id: 'a3', icon: '📀' },
    { id: 'a4', icon: '⌨️' },
  ]
});

// Logik für das Auswählen von Antworten
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

// Timer-Logik
function startTimer() {
  clearInterval(timerInterval);
  timer.value = level.value === 1 ? 60 : 10;
  timerInterval = setInterval(() => {
    if (timer.value > 0) {
      timer.value--;
    } else {
      clearInterval(timerInterval);
      alert("Zeit abgelaufen!");
    }
  }, 1000);
}

// Lifecycle Hooks
onMounted(() => {
  // Wir prüfen, ob die gameDetails vorhanden sind, bevor wir den Timer starten
  if (props.gameDetails) {
    startTimer();
  }
});

onUnmounted(() => {
  clearInterval(timerInterval);
});
</script>

<template>
  <div class="bg-white min-h-screen flex flex-col p-4 sm:p-6 lg:p-8">
    <div class="w-full max-w-5xl mx-auto flex flex-col flex-grow">
      
      <header class="grid grid-cols-3 items-center gap-4 mb-6">
        <div class="flex items-center gap-3">
          <UserCircleIcon class="h-10 w-10 text-gray-400" />
          <span class="text-xl font-semibold text-gray-800">{{ loggedInPlayer.name }}</span>
        </div>

        <div class="flex flex-col items-center justify-center">
          <ClockIcon class="h-8 w-8 text-gray-500" />
          <span class="text-4xl font-mono font-bold text-gray-900 tracking-wider">
            00:{{ timer.toString().padStart(2, '0') }}
          </span>
          <div v-if="level === 1" class="text-sm font-semibold text-gray-500">Level 1: Gesamtzeit</div>
          <div v-if="level === 2" class="text-sm font-semibold text-gray-500">Level 2: Runde {{ currentRound }} / {{ maxRounds }}</div>
        </div>

        <div class="flex items-center justify-end gap-3">
           <div class="text-right">
             <img src="../assets/logo.svg" alt="Logo" class="h-10 ml-auto mb-1">
             <span class="text-xl font-semibold text-gray-800">{{ opponentPlayer.name }}</span>
           </div>
          <UserCircleIcon class="h-10 w-10 text-gray-400" />
        </div>
      </header>
      
      <main class="flex-grow flex flex-col items-center justify-center">
        <div class="mb-10 text-9xl">
          {{ currentQuestion.item.icon }}
        </div>
        <div class="w-full max-w-2xl grid grid-cols-2 gap-4 sm:gap-6">
          <button
            v-for="answer in currentQuestion.answers"
            :key="answer.id"
            @click="toggleAnswer(answer.id)"
            :class="[
              'p-6 rounded-xl border-2 transition-all duration-200',
              'flex flex-col items-center justify-center gap-2',
              isSelected(answer.id)
                ? 'bg-blue-100 border-blue-500 shadow-lg scale-105'
                : 'bg-gray-100 border-gray-200 hover:border-gray-400'
            ]"
          >
            <span class="text-3xl font-bold">{{ answer.icon }}</span>
            <span class="font-semibold">{{ answer.text }}</span>
          </button>
        </div>
      </main>
    </div>
  </div>
</template>