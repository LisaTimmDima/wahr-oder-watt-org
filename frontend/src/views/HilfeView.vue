<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: Komplette UI-Struktur, State-Management und Logik für die Hilfe-Seite.
// ==================================================================================

// import: Lädt Vue-Funktionen (ref) und Icon-Komponenten.
import { ref, computed } from 'vue';
import { ChevronDownIcon, EnvelopeIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Emits: Deklariert das 'show-lobby'-Event, um zur Lobby zurückzukehren.
// ==================================================================================
const emit = defineEmits(['show-lobby']);

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Steuerung der UI.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').Ref<string | null>}
 * @description Speichert die ID des aktuell geöffneten Akkordeon-Elements. `null`, wenn alle geschlossen sind.
 */
const openAccordion = ref('spielregeln');

/**
 * @type {import('vue').Ref<Array<object>>}
 * @description Enthält die statischen Inhalte für die FAQ-Sektion.
 * Jedes Objekt hat eine `id`, eine `question` (Frage) und eine `answer` (Antwort).
 */
const faqs = ref([
  {
    id: 'spielregeln',
    question: 'Spielregeln',
    answer: 'Bei <strong>„Wahr oder Watt?“</strong> geht es um Wissen und Reaktionsgeschwindigkeit. In jeder Runde wird eine Aussage über einen IT-Gegenstand (Hardware, Software, Zubehör) und eine seiner möglichen Eigenschaften präsentiert. Die Spieler müssen blitzschnell entscheiden, ob diese Aussage der Wahrheit entspricht oder nicht. <strong>Ist die Aussage Wahr oder ist sie totaler Quatsch (Watt?!).</strong>'
  },
  {
    id: 'spielziel',
    question: 'Spielziel',
    answer: 'Das Ziel des Spiels ist es, über mehrere Runden hinweg die meisten Punkte zu sammeln. Punkte erhält man für korrekte und schnelle Antworten. Wer am Ende die höchste Punktzahl hat, gewinnt das Spiel.'
  },
  {
    id: 'spielablauf',
    question: 'Spielablauf',
    answer: '<strong>Lobby & Spielstart:</strong> Alle Spieler loggen sich ein und versammeln sich in einer digitalen Lobby. <strong>Spielansicht:</strong> Auf dem Bildschirm aller Teilnehmer erscheint ein Bild eines Gegenstands (z. B. Desktop-PC). Jeder Gegenstand hat mehrere passende Eigenschaften. <strong>Beispiel 1:</strong> „Ein Desktop-PC könnte eine Tastatur haben. <strong>Beispiel 2:</strong> „Ein Smartphone hat einen Akku.“ <strong>Reaktion ist alles:</strong> Ein Countdown (z. B. 10 Sekunden) läuft ab. Innerhalb dieser Zeit müssen alle Spieler eine Entscheidung treffen und einen der vier möglichen Buttons drücken. Wer nicht rechtzeitig antwortet, erhält für diese Runde keine Punkte. <strong>Die Auflösung:</strong> Sobald die Zeit abgelaufen ist oder alle Spieler geantwortet haben, wird die Runde sofort ausgewertet: Das System zeigt die korrekte Antwort an (z. B. „FALSCH! Ein Desktop-PC hat keine beweglichen Teile.“). Die Spieler sehen, wer richtig und wer falsch lag. Die Punkte werden live vergeben und der aktuelle Spielstand wird angezeigt.'
  },
  {
    id: 'spielmodi',
    question: 'Spielmodi',
    answer: '<strong>Level 1:</strong> Speedrun 60 Sekunden Gesamtzeit, so viele Fragen wie möglich.<strong>Level 2:</strong> Runden-Duell 5 Runden, 10 Sekunden pro Runde.'
  },
  {
    id: 'geräteundeigenschaften',
    question: 'Erklärung von Symbole',
    answer: '<strong>Geräte:</strong>  🖥️ Desktop-PC,📱 Smartphone, ⌚️ Smartwatch.<strong>Eigenschaften:</strong> ⌨️ Tastatur , 🖱️ Maus, 🎧 Kopfhörer, 🔊 Lautsprecher , 🛜 Wi-Fi, 🔌 Netzteil, 📀 CD-Laufwerk, 💾 Floppy Disk, 📷 Kamera, 🕹️ Game-Controller 🎙️ Mikrofon, 🔩 Schrauben, 📁 Verzeichnis, 🔋 Akku, 🎵 Musiksteuerung,  💳 Mobiles Bezahlen (NFC),  😴 Schlaftracking, ❤️‍🩹 Herzerzfrequenzmessung, 🚶 Schrittzähler, 📞 Anrufe tätigen, 🌐 Internet (LAN/5G), ⏰Timer, 💧Wasserdichtigkeit, 🗣️Sprachassistent '
  }
]);

// BARRIEREFREIHEIT: Reaktive Variable für die Zoom-Stufe.
const zoomLevel = ref(1);

// BARRIEREFREIHEIT: Reaktive Variable zur Steuerung des Hochkontrastmodus.
const isHighContrast = ref(false);

// ==================================================================================
// Computed Properties
// ==================================================================================

// BARRIEREFREIHEIT: Berechnete Eigenschaft, die ein Style-Objekt für die dynamische Skalierung (Zoom) zurückgibt.
const containerStyle = computed(() => ({
  zoom: zoomLevel.value
}));

// ==================================================================================
// Methoden: Funktionen zur Handhabung von Benutzerinteraktionen.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @function toggleAccordion
 * @author Lisa
 * @description Öffnet oder schließt ein Akkordeon-Element. Wenn das geklickte Element bereits offen ist, wird es geschlossen.
 * @param {string} section - Die ID der Sektion, die umgeschaltet werden soll.
 */
function toggleAccordion(section) {
  openAccordion.value = openAccordion.value === section ? null : section;
}

/**
 * @function goBackToLobby
 * @author Lisa
 * @description Löst ein Event aus, um zur Lobby-Ansicht zurückzukehren.
 */
function goBackToLobby() {
  emit('show-lobby');
}

// BARRIEREFREIHEIT: Methoden zur Anpassung der Zoom-Stufe.
function increaseZoom() {
  zoomLevel.value += 0.1;
}
function decreaseZoom() {
  zoomLevel.value -= 0.1;
}

// BARRIEREFREIHEIT: Methode zum Umschalten des Hochkontrastmodus.
function toggleHighContrast() {
  isHighContrast.value = !isHighContrast.value;
}

</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - @click:      Führt eine Methode aus, wenn auf das Element geklickt wird.
    - v-for:        Erstellt eine Schleife über eine Liste (hier `faqs`).
    - :key:         Eindeutiger Schlüssel für jedes `v-for`-Element, wichtig für die Performance.
    - :class:       Bindet Klassen dynamisch, z.B. für die Pfeil-Rotation im Akkordeon.
    - v-show:       Ändert die Sichtbarkeit eines Elements (ähnlich v-if, aber schaltet nur CSS `display` um).
    - v-html:       Rendert rohes HTML. Nur für vertrauenswürdigen Inhalt verwenden, um XSS-Angriffe zu vermeiden.
  -->
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-center p-4" :style="containerStyle" :class="{ 'high-contrast': isHighContrast }">
    <header class="w-full max-w-4xl mx-auto text-center mb-8">
      <!-- BARRIEREFREIHEIT: Steuerelemente für Zoom und Kontrast. -->
      <div class="text-right mb-4 flex justify-end items-center gap-4">
        <div>
            <span class="text-sm text-gray-600 mr-2">Zoom:</span>
            <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
            <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300 ml-1">+</button>
        </div>
        <button @click="toggleHighContrast" class="px-3 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">Kontrast</button>
      </div>
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
        <!-- v-show wird hier verwendet, da das häufige Ein- und Ausblenden performanter ist als mit v-if, das die Elemente komplett neu erstellt. -->
        <div v-show="openAccordion === faq.id" class="px-6 pb-6 text-gray-700 text-lg prose max-w-none">
          <!-- ACHTUNG: v-html wird hier verwendet, um HTML-Tags wie <strong> zu rendern. -->
          <!-- Dies sollte nur mit absolut vertrauenswürdigem Inhalt geschehen (wie hier, da er hartkodiert ist), -->
          <!-- da es sonst ein Sicherheitsrisiko (XSS) darstellen kann. -->
          <p v-html="faq.answer"></p>
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

<style>
/* BARRIEREFREIHEIT: Stile für den Hochkontrastmodus */
.high-contrast {
  background-color: #000 !important;
  color: #fff !important;
}

.high-contrast .bg-white, .high-contrast .bg-gray-50, .high-contrast .bg-blue-600 {
  background-color: #000 !important;
  border: 2px solid yellow !important;
}

.high-contrast .text-gray-800,
.high-contrast .text-gray-700,
.high-contrast .text-gray-600,
.high-contrast .text-gray-500,
.high-contrast .text-xl,
.high-contrast .text-white {
  color: #fff !important;
}

.high-contrast .bg-gray-100 {
    background-color: #000 !important;
}

.high-contrast .bg-gray-200 {
    background-color: #333 !important;
}

.high-contrast button, .high-contrast a {
    border: 1px solid yellow !important;
}

.high-contrast .text-blue-600 {
    color: yellow !important;
}

.high-contrast .divide-y > :not([hidden]) ~ :not([hidden]) {
    border-color: yellow !important;
}

.high-contrast .prose strong {
    color: yellow !important;
}

/* Stellt sicher, dass die mit v-html gerenderten Inhalte die Textformatierung erben */
.prose p {
  margin: 0;
}
</style>