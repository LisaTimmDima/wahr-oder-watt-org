<script setup>
import { ref } from 'vue';
import { ChevronDownIcon, EnvelopeIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/solid';

const emit = defineEmits(['show-lobby']);

const openAccordion = ref('spielregeln');

const faqs = ref([
  {
    id: 'spielregeln',
    question: 'Spielregeln',
    answer: 'Bei „Wahr oder Watt?“ geht es um Wissen und Reaktionsgeschwindigkeit. In jeder Runde wird eine Aussage über einen IT-Gegenstand (Hardware, Software, Zubehör) und eine seiner möglichen Eigenschaften präsentiert. Die Spieler müssen blitzschnell entscheiden, ob diese Aussage der Wahrheit entspricht oder nicht. Der Name ist Programm: ist die Aussage wahr oder ist sie totaler Quatsch (Watt?!).'
  },
  {
    id: 'spielziel',
    question: 'Spielziel',
    answer: 'Das Ziel des Spiels ist es, über mehrere Runden hinweg die meisten Punkte zu sammeln. Punkte erhält man für korrekte und schnelle Antworten. Wer am Ende die höchste Punktzahl hat, gewinnt das Spiel.'
  },
  {
    id: 'spielablauf',
    question: 'Spielablauf',
    answer: 'Lobby & Spielstart: Alle Spieler loggen sich ein und versammeln sich in einer digitalen Lobby. Sobald die gewünschte Spieleranzahl erreicht ist, startet der Spielleiter (oder das System automatisch) das Spiel.'
  }
]);

function toggleAccordion(section) {
  openAccordion.value = openAccordion.value === section ? null : section;
}

function goBackToLobby() {
  emit('show-lobby');
}
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-center p-4">
    <header class="w-full max-w-4xl mx-auto text-center mb-8">
      <img src="../assets/logo.svg" alt="Wahr oder Watt Logo" class="mx-auto h-32 w-auto mb-6">
      <h1 class="text-5xl font-extrabold text-gray-800 tracking-tight">Hilfe & FAQ</h1>
    </header>

    <main class="w-full max-w-4xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden divide-y divide-gray-200">
      <div v-for="faq in faqs" :key="faq.id">
        <button 
          @click="toggleAccordion(faq.id)" 
          class="w-full flex justify-between items-center p-6 text-left hover:bg-gray-50 focus:outline-none"
        >
          <h2 class="text-2xl font-semibold text-gray-800">{{ faq.question }}</h2>
          <ChevronDownIcon :class="['h-8 w-8 text-gray-500 transition-transform transform', openAccordion === faq.id ? '-rotate-180' : '']" />
        </button>
        <div v-show="openAccordion === faq.id" class="px-6 pb-6 text-gray-700 text-lg">
          <p>{{ faq.answer }}</p>
        </div>
      </div>
    </main>

    <footer class="w-full max-w-4xl mx-auto mt-12 text-center">
      <div class="bg-blue-600 text-white rounded-2xl shadow-xl p-4 max-w-2xl mx-auto">
        <div class="flex flex-col md:flex-row items-center text-center md:text-left">
          <div class="flex-shrink-0 mb-4 md:mb-0">
            <EnvelopeIcon class="h-12 w-12" />
          </div>
          <div class="md:ml-6">
            <h3 class="text-xl font-bold">Noch Fragen?</h3>
            <p class="mt-1">Senden Sie uns eine E-Mail. Wir helfen Ihnen gerne weiter!</p>
          </div>
          <div class="mt-6 md:mt-0 md:ml-auto md:pl-6">
            <a href="mailto:support@wahr-oder-watt.com" class="bg-white text-blue-600 font-bold py-2 px-6 rounded-full hover:bg-blue-100 transition-colors whitespace-nowrap">
              Kontakt aufnehmen
            </a>
          </div>
        </div>
      </div>

      <button 
        @click="goBackToLobby" 
        class="inline-flex items-center gap-2 mt-8 bg-white text-gray-800 font-bold py-3 px-8 rounded-full shadow-md hover:bg-gray-200 transition-transform transform hover:-translate-y-1"
      >
        <ArrowUturnLeftIcon class="h-5 w-5" />
        <span>Zurück zur Lobby</span>
      </button>
    </footer>
  </div>
</template>
