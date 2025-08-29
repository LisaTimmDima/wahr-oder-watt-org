<script setup>
import { ref } from 'vue';
import { XMarkIcon, EnvelopeIcon } from '@heroicons/vue/24/solid';

// Ein Zustand, der steuert, welche FAQ-Box gerade geöffnet ist.
// `null` bedeutet, dass keine geöffnet ist.
const openAccordion = ref('spielregeln'); // Standardmäßig ist die erste Box geöffnet

// Funktion zum Umschalten der Akkordeons
function toggleAccordion(section) {
  if (openAccordion.value === section) {
    // Wenn die bereits geöffnete Box geklickt wird, schließe sie.
    openAccordion.value = null;
  } else {
    // Ansonsten öffne die geklickte Box.
    openAccordion.value = section;
  }
}
</script>

<template>
  <div class="bg-gray-50 min-h-screen">
    <div class="max-w-4xl mx-auto p-4 sm:p-8">

      <header class="flex justify-between items-center mb-10">
        <h1 class="text-4xl font-bold text-gray-900">Frequently Asked Questions</h1>
        <div class="bg-white p-2 rounded-lg shadow-sm">
          <img src="../assets/logo.svg" alt="Logo" class="h-12">
        </div>
      </header>

      <main class="space-y-4">
        
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
          <button @click="toggleAccordion('spielregeln')" class="w-full flex justify-between items-center p-6 text-left">
            <h2 class="text-xl font-semibold text-gray-800">Spielregeln</h2>
            <XMarkIcon :class="['h-6 w-6 text-gray-500 transition-transform', openAccordion !== 'spielregeln' ? 'rotate-45' : '']" />
          </button>
          <div v-if="openAccordion === 'spielregeln'" class="px-6 pb-6 text-gray-600">
            Bei „Wahr oder Watt?“ geht es um Wissen und Reaktionsgeschwindigkeit. In jeder Runde wird eine Aussage über einen IT-Gegenstand (Hardware, Software, Zubehör) und eine seiner möglichen Eigenschaften präsentiert. Die Spieler müssen blitzschnell entscheiden, ob diese Aussage der Wahrheit entspricht oder nicht. Der Name ist Programm: ist die Aussage **wahr** oder ist sie totaler Quatsch (**Watt**?!).
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
          <button @click="toggleAccordion('spielziel')" class="w-full flex justify-between items-center p-6 text-left">
            <h2 class="text-xl font-semibold text-gray-800">Spielziel</h2>
            <XMarkIcon :class="['h-6 w-6 text-gray-500 transition-transform', openAccordion !== 'spielziel' ? 'rotate-45' : '']" />
          </button>
          <div v-if="openAccordion === 'spielziel'" class="px-6 pb-6 text-gray-600">
            Das Ziel des Spiels ist es, über mehrere Runden hinweg die meisten Punkte zu sammeln. Punkte erhält man für korrekte und schnelle Antworten. Wer am Ende die höchste Punktzahl hat, gewinnt das Spiel.
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
          <button @click="toggleAccordion('spielablauf')" class="w-full flex justify-between items-center p-6 text-left">
            <h2 class="text-xl font-semibold text-gray-800">Spielablauf</h2>
            <XMarkIcon :class="['h-6 w-6 text-gray-500 transition-transform', openAccordion !== 'spielablauf' ? 'rotate-45' : '']" />
          </button>
          <div v-if="openAccordion === 'spielablauf'" class="px-6 pb-6 text-gray-600">
            **Lobby & Spielstart:** Alle Spieler loggen sich ein und versammeln sich in einer digitalen Lobby. Sobald die gewünschte Spieleranzahl erreicht ist, startet der Spielleiter (oder das System automatisch) das Spiel.
          </div>
        </div>

      </main>

      <footer class="mt-12 text-center">
        <div class="bg-white rounded-xl shadow-sm p-8 max-w-lg mx-auto">
          <EnvelopeIcon class="h-12 w-12 text-blue-500 mx-auto mb-4" />
          <h3 class="text-xl font-bold text-gray-800">Senden Sie uns eine E-Mail</h3>
          <p class="text-gray-600 mt-2">Haben Sie Fragen oder benötigen Sie Hilfe? Kontaktieren Sie uns per E-Mail. Wir helfen Ihnen gerne weiter!</p>
        </div>
        
        </footer>

    </div>
  </div>
</template>