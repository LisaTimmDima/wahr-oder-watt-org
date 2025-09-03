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
  /**
   * @property {object} gameDetails - Enthält die Details zum gestarteten Spiel.
   * @property {object} gameDetails.opponent - Der Gegner, der herausgefordert wurde.
   * @property {number} gameDetails.level - Das ausgewählte Spiellevel.
   */
  gameDetails: {
    type: Object,
    required: true
  }
});

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen für den Spielzustand.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').Ref<object>}
 * @description Speichert Zustand und Punktestand des angemeldeten Spielers.
 * @todo Sollte aus einem globalen State oder per API-Aufruf initialisiert werden.
 */
const loggedInPlayer = ref({ name: 'Spieler 1', score: 0 });

/**
 * @type {import('vue').Ref<number>}
 * @description Der Countdown-Timer für die aktuelle Runde oder das gesamte Spiel.
 */
const timer = ref(0);

/**
 * @type {import('vue').Ref<number>}
 * @description Zählt die aktuelle Spielrunde.
 */
const currentRound = ref(1);

/**
 * @type {number}
 * @description Maximale Anzahl der Runden im Runden-Duell Modus.
 */
const maxRounds = 5;

/**
 * @type {number | null}
 * @description Hält die ID des Intervalls für den Timer, um ihn später stoppen zu können.
 */
let timerInterval = null;

/**
 * @type {import('vue').Ref<object>}
 * @description Speichert die aktuell angezeigte Frage inklusive der Antwortmöglichkeiten.
 * @todo Fragen sollten von einer API geladen werden, anstatt hartkodiert zu sein.
 */
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

/**
 * @type {import('vue').Ref<Array<string>>}
 * @description Speichert die IDs der vom Benutzer ausgewählten Antworten.
 */
const selectedAnswers = ref([]);

// ==================================================================================
// Computed Properties: Abgeleitete, reaktive Werte.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').ComputedRef<object>}
 * @description Greift auf den Gegner aus den übergebenen Props zu.
 */
const opponentPlayer = computed(() => props.gameDetails.opponent);

/**
 * @type {import('vue').ComputedRef<number>}
 * @description Greift auf das Level aus den übergebenen Props zu.
 */
const level = computed(() => props.gameDetails.level);

/**
 * @type {import('vue').ComputedRef<Function>}
 * @description Gibt eine Funktion zurück, die prüft, ob eine Antwort-ID ausgewählt wurde.
 * Nützlich, um im Template dynamisch Klassen zu binden.
 * @returns {Function} Eine Funktion, die eine `answerId` entgegennimmt und `true` oder `false` zurückgibt.
 */
const isSelected = computed(() => {
  return (answerId) => selectedAnswers.value.includes(answerId);
});

// ==================================================================================
// Methoden: Funktionen zur Steuerung der Spiellogik.
// ==================================================================================

/**
 * @function fetchQuestion
 * @author Dima
 * @description (Zukünftige Funktion) Lädt eine neue Frage vom Server.
 * @todo Diese Funktion muss implementiert werden, um `currentQuestion` dynamisch zu füllen.
 */
// async function fetchQuestion() { ... }

/**
 * @function toggleAnswer
 * @author Lisa
 * @description Fügt eine Antwort zur Auswahl hinzu oder entfernt sie, wenn sie bereits ausgewählt wurde.
 * @param {string} answerId - Die ID der ausgewählten Antwort.
 */
function toggleAnswer(answerId) {
  const index = selectedAnswers.value.indexOf(answerId);
  if (index === -1) {
    selectedAnswers.value.push(answerId);
  } else {
    selectedAnswers.value.splice(index, 1);
  }
}

/**
 * @function submitAnswers
 * @author Lisa
 * @description Wertet die ausgewählten Antworten aus, aktualisiert den Punktestand und startet die nächste Runde oder beendet das Spiel.
 * @param {boolean} [isTimeout=false] - Gibt an, ob die Antworten aufgrund eines Timeouts automatisch abgeschickt wurden.
 * @todo Die `alert()`-Nachrichten sollten durch benutzerfreundlichere UI-Elemente ersetzt werden.
 */
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
    // Hier sollte eine neue Frage geladen werden, z.B. mit fetchQuestion()
    startTimer();
  } else {
    alert(`Spiel beendet! Endstand: ${loggedInPlayer.value.score}`);
    // Logik für das Spielende, z.B. zum Highscore senden.
  }
}

/**
 * @function startTimer
 * @author Lisa
 * @description Startet den Countdown-Timer für eine Runde. Die Dauer hängt vom Spiellevel ab.
 */
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

/**
 * @function goBackToLobby
 * @author Lisa
 * @description Löst ein Event aus, um zur Lobby zurückzukehren.
 */
function goBackToLobby() {
  emit('show-lobby');
}

// ==================================================================================
// Lifecycle Hooks: Funktionen, die Vue zu bestimmten Zeitpunkten im Lebenszyklus einer Komponente aufruft.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * onMounted(): Wird ausgeführt, nachdem die Komponente in das DOM eingehängt wurde.
 * Startet hier den Timer für das Spiel.
 */
onMounted(() => {
  if (props.gameDetails) {
    startTimer();
  }
});

/**
 * onUnmounted(): Wird ausgeführt, bevor die Komponente aus dem DOM entfernt wird.
 * Stoppt den Timer, um Memory-Leaks und Fehler zu vermeiden.
 */
onUnmounted(() => {
  clearInterval(timerInterval);
});
</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - @click:      Führt eine Methode aus, wenn auf das Element geklickt wird.
    - :class:       Bindet Klassen dynamisch, z.B. um das Aussehen basierend auf dem Zustand zu ändern.
    - v-for:        Erstellt eine Schleife über eine Liste und rendert für jeden Eintrag ein Element.
    - :key:         Ein eindeutiger Schlüssel für jedes v-for-Element, wichtig für die Performance.
    - {{ ... }}:     Gibt den Wert einer Variable als Text aus (Interpolation).
  -->
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