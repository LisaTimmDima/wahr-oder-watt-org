<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { UserCircleIcon, ClockIcon } from '@heroicons/vue/24/outline';


// Spieler-Daten (könnten später vom Backend kommen)
const player1 = ref({ id: 1, name: 'Spieler 1' });
const player2 = ref({ id: 2, name: 'Spieler 2' });
const timer = ref(59);
const timerInterval = ref(null);

const currentQuestion = ref({
  item: { name: 'Monitor', icon: '🖥️' }, // Using an emoji as a placeholder
  answers: [
    { id: 'a1', icon: '🛜' },
    { id: 'a2', icon: '🔌' },
    { id: 'a3', icon: '📀' },
    { id: 'a4', icon: '⌨️' },
  ]
});

// An array to keep track of the selected answer IDs
const selectedAnswers = ref([]);

// --- LOGIC ---

// Function to handle clicking an answer
function toggleAnswer(answerId) {
  const index = selectedAnswers.value.indexOf(answerId);
  if (index === -1) {
    // If not selected, add it
    selectedAnswers.value.push(answerId);
  } else {
    // If already selected, remove it
    selectedAnswers.value.splice(index, 1);
  }
  console.log('Selected answers:', selectedAnswers.value);
}

// A computed property to easily check if an answer is selected
const isSelected = computed(() => {
  return (answerId) => selectedAnswers.value.includes(answerId);
});

// Function to start the countdown
function startTimer() {
  timerInterval.value = setInterval(() => {
    if (timer.value > 0) {
      timer.value--;
    } else {
      clearInterval(timerInterval.value);
      alert("Zeit abgelaufen!");
      // Handle end of round
    }
  }, 1000);
}

// --- LIFECYCLE HOOKS ---
onMounted(() => {
  // Start the timer when the component is loaded
  startTimer();
});

onUnmounted(() => {
  // Clean up the interval when the component is destroyed to prevent memory leaks
  clearInterval(timerInterval.value);
});
</script>

<template>
  <div class="bg-white min-h-screen flex flex-col p-4 sm:p-6 lg:p-8">
    <div class="w-full max-w-5xl mx-auto flex flex-col flex-grow">
      
      <header class="grid grid-cols-3 items-center gap-4 mb-6">
        <div class="flex items-center gap-3">
          <UserCircleIcon class="h-10 w-10 text-gray-400" />
          <span class="text-xl font-semibold text-gray-800">{{ player1.name }}</span>
        </div>

        <div class="flex flex-col items-center justify-center">
          <ClockIcon class="h-8 w-8 text-gray-500" />
          <span class="text-4xl font-mono font-bold text-gray-900 tracking-wider">
            00:{{ timer.toString().padStart(2, '0') }}
          </span>
        </div>

        <div class="flex items-center justify-end gap-3">
          <div class="text-right">
            <img src="../assets/logo.svg" alt="Logo" class="h-10 ml-auto mb-1">
            <span class="text-xl font-semibold text-gray-800">{{ player2.name }}</span>
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