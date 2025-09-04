<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Layout, Datenaufbereitung (Ranking) und Event-Handling.
// - Dima: Implementierung des API-Aufrufs zum Abrufen der Highscore-Daten.
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted, computed } from 'vue';
import { UserCircleIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Emits: Deklariert das 'show-lobby'-Event, um zur Lobby zurückzukehren.
// ==================================================================================
const emit = defineEmits(['show-lobby']);

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Steuerung der UI und zur Speicherung der Daten.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').Ref<boolean>}
 * @description Zeigt an, ob die Highscore-Daten gerade vom Server geladen werden.
 */
const loading = ref(false);

/**
 * @type {import('vue').Ref<string | null>}
 * @description Speichert eine Fehlermeldung, falls das Laden der Daten fehlschlägt.
 */
const loadError = ref(null);

/**
 * @type {import('vue').Ref<Array<object>>}
 * @description Speichert die verarbeitete und sortierte Liste der Highscores.
 */
const highscores = ref([]);
const loggedInUser = ref({ id: null, name: '' }); // Platzhalter für
const token = computed(() => localStorage.getItem('jwt'));

// den aktuell eingeloggten Benutzer


/**
 * @type {import('vue').Ref<object>}
 * @description Speichert den aktuell angemeldeten Benutzer, um ihn in der Liste hervorzuheben.
 * @todo Aktuell ein Platzhalter. Sollte aus einem globalen State (z.B. Pinia) oder per API-Aufruf gefüllt werden.
 */
const loggedInUser = ref({ id: null, name: '' });

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
// Methoden: Funktionen zur Datenverarbeitung und Handhabung von Benutzerinteraktionen.
// ==================================================================================

/**
 * @function applyRanking
 * @author Lisa
 * @description Nimmt eine Liste von Highscores, sortiert sie absteigend nach Punkten und fügt einen 'rank'-Schlüssel hinzu.
 * @param {Array<object>} list - Die unsortierte Liste von Highscore-Einträgen.
 * @returns {Array<object>} Die sortierte und mit Rängen versehene Liste.
 */
function applyRanking(list) {
  // Erstellt eine Kopie der Liste, um Seiteneffekte auf die Originaldaten zu vermeiden.
  return [...list]
      .sort((a, b) => b.score - a.score)
      .map((h, i) => ({ ...h, rank: i + 1 }));
}

/**
 * @function fetchHighscores
 * @author Dima
 * @description Ruft die Highscore-Daten vom Server ab, normalisiert die Datenstruktur und aktualisiert den reaktiven State.
 * @param {AbortController} [controller=new AbortController()] - Ein AbortController, um den Fetch-Request bei Bedarf abbrechen zu können.
 */
async function fetchHighscores(controller = new AbortController()) {
  loading.value = true;
  loadError.value = null;
  try {
    const resp = await fetch('/api/highscores', {
      headers: { 'Accept': 'application/json', 'Authorization': `Bearer ${token.value}` },
      credentials: 'include',
      signal: controller.signal
    });
    if (!resp.ok) throw new Error(`Daten konnten nicht geladen werden (HTTP ${resp.status})`);
    const data = await resp.json();
    // Normalisiert die Daten, da die API anscheinend unterschiedliche Schlüssel für den Benutzernamen liefert.
    // Dies sorgt für eine konsistente Datenstruktur in der Frontend-Logik.
    const normalized = (Array.isArray(data) ? data : []).map(h => ({
      name: h.username ?? h.userName ?? h.name ?? h.id ?? 'Unbekannt',
      score: h.score ?? 0
    }));

    highscores.value = applyRanking(normalized);

  } catch (e) {
    if (e.name !== 'AbortError') {
      loadError.value = e.message;
    }
    highscores.value = [];
  } finally {
    loading.value = false;
  }
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

// ==================================================================================
// Lifecycle Hooks: Funktionen, die Vue zu bestimmten Zeitpunkten im Lebenszyklus einer Komponente aufruft.
// Verantwortlich: Dima (da der Hook die API-Logik auslöst)
// ==================================================================================

/**
 * onMounted(): Wird ausgeführt, nachdem die Komponente in das DOM eingehängt wurde.
 * Ruft die Highscores ab, sobald die Ansicht sichtbar wird.
 */
onMounted(() => {
fetchHighscores();
});

</script>

<template>

  <!-- 
    Vue Template Grundlagen:
    - v-for:      Erstellt eine Schleife über die `highscores`-Liste.
    - :key:       Eindeutiger Schlüssel für jedes `v-for`-Element.
    - :class:     Bindet Klassen dynamisch, um z.B. den eingeloggten User hervorzuheben.
    - v-if/else:  Zeigt je nach Zustand (laden, Fehler, Erfolg) unterschiedliche Blöcke an.
    - @click:     Führt die `goBackToLobby`-Methode bei einem Klick aus.
    - {{ ... }}:    Gibt den Wert einer Variable als Text aus (Interpolation).
  -->
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-center p-4" :style="containerStyle" :class="{ 'high-contrast': isHighContrast }">
    <header class="w-full max-w-2xl mx-auto text-center mb-8">
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
      <h1 class="text-5xl font-extrabold text-gray-800 tracking-tight">Highscores</h1>
    </header>

    <main class="w-full max-w-2xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden">
      <div class="bg-gray-800 text-white grid grid-cols-3 gap-4 font-semibold text-lg p-4">
        <div class="text-center">Rank</div>
        <div>User</div>
        <div class="text-right">Score</div>
      </div>
      <!-- Ladezustand -->
      <div v-if="loading" class="p-8 text-center text-gray-500">
        <p>Lade Highscores...</p>
      </div>

      <!-- Fehlerzustand -->
      <div v-else-if="loadError" class="p-8 text-center text-red-600 bg-red-50">
        <p class="font-bold">Ein Fehler ist aufgetreten</p>
        <p class="text-sm">{{ loadError }}</p>
      </div>

      <!-- Erfolgszustand mit Daten -->
      <ul v-else-if="highscores.length > 0">
        <li 
          v-for="(entry, index) in highscores" 
          :key="entry.name"
          :class="[
            'grid grid-cols-3 gap-4 items-center p-4 transition-colors', 
            loggedInUser.name === entry.name ? 'bg-blue-100 font-bold' : 'hover:bg-gray-50',
            index < highscores.length - 1 ? 'border-b border-gray-200' : ''
          ]"
        >
          <div class="text-center text-2xl font-bold text-gray-500">#{{ entry.rank }}</div>
          <div class="flex items-center gap-3">
            <UserCircleIcon class="h-8 w-8 text-gray-400" />
            <span class="text-gray-800">{{ entry.name }}</span>
          </div>
          <div class="text-right text-xl font-semibold text-gray-700">{{ entry.score }}</div>
        </li>
      </ul>

      <!-- Zustand, wenn keine Daten vorhanden sind -->
      <div v-else class="p-8 text-center text-gray-500">
        <p>Noch keine Highscores vorhanden.</p>
      </div>
    </main>

    <footer class="w-full max-w-2xl mx-auto mt-8 text-center">
      <button
          @click="goBackToLobby"
          class="inline-flex items-center gap-2 bg-white text-gray-800 font-bold py-3 px-8 rounded-full shadow-md hover:bg-gray-200 transition-transform transform hover:-translate-y-1"
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

.high-contrast .bg-white, .high-contrast .bg-gray-50, .high-contrast .bg-red-50 {
  background-color: #000 !important;
  border: 2px solid yellow !important;
}

.high-contrast .text-gray-800,
.high-contrast .text-gray-700,
.high-contrast .text-gray-500,
.high-contrast .text-gray-400,
.high-contrast .text-red-600 {
  color: #fff !important;
}

.high-contrast .bg-gray-100 {
    background-color: #000 !important;
}

.high-contrast .bg-gray-200 {
    background-color: #333 !important;
}

.high-contrast .bg-gray-800 {
    background-color: #000 !important;
    border-bottom: 2px solid yellow;
}

.high-contrast button {
    border: 1px solid yellow !important;
}

.high-contrast .border-b {
    border-color: yellow !important;
}

.high-contrast .bg-blue-100 {
    background-color: #00008b !important; /* Dunkelblau */
    color: yellow !important;
}

</style>