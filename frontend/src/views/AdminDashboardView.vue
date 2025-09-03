<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Layout und Anzeige der statischen Daten.
// - Dima: (Zukünftig) Implementierung der API-Aufrufe zum Abrufen der Dashboard-Statistiken.
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted, computed } from 'vue';
import { UsersIcon, CubeIcon, BellIcon } from '@heroicons/vue/24/outline';

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Speicherung der Dashboard-Daten.
// Verantwortlich: Lisa
// ==================================================================================

/**
 * @type {import('vue').Ref<Array<object>>}
 * @description Speichert die Statistik-Daten, die auf dem Dashboard angezeigt werden.
 * @todo Aktuell hartkodiert. Sollte durch einen API-Aufruf in `fetchStats` dynamisch geladen werden.
 */
const stats = ref([
  { name: 'Benutzer zur Freischaltung', value: 1, icon: BellIcon, color: 'bg-red-500' },
  { name: 'Gesamte Benutzer', value: 12, icon: UsersIcon, color: 'bg-blue-500' },
  { name: 'Gesamte Geräte', value: 2, icon: CubeIcon, color: 'bg-yellow-500' },
]);

// BARRIEREFREIHEIT: Reaktive Variable für die Zoom-Stufe.
const zoomLevel = ref(1);

// ==================================================================================
// Computed Properties
// ==================================================================================

// BARRIEREFREIHEIT: Berechnete Eigenschaft, die ein Style-Objekt für die dynamische Skalierung (Zoom) zurückgibt.
const containerStyle = computed(() => ({
  zoom: zoomLevel.value
}));

// ==================================================================================
// Methoden: Zukünftige Funktionen zum Laden der Daten.
// ==================================================================================

/**
 * @function fetchStats
 * @author Dima
 * @description (Zukünftige Funktion) Lädt die Dashboard-Statistiken vom Server.
 * @todo Diese Funktion muss implementiert werden, um die `stats`-Variable dynamisch zu füllen.
 */
// async function fetchStats() { ... }


// ==================================================================================
// Lifecycle Hooks: Code, der zu bestimmten Zeitpunkten im Lebenszyklus der Komponente ausgeführt wird.
// ==================================================================================

/**
 * onMounted(): Wird ausgeführt, nachdem die Komponente in das DOM eingehängt wurde.
 * @todo Sobald `fetchStats` implementiert ist, sollte es hier aufgerufen werden.
 */
// onMounted(fetchStats);

// BARRIEREFREIHEIT: Methoden zur Anpassung der Zoom-Stufe.
function increaseZoom() {
  zoomLevel.value += 0.1;
}
function decreaseZoom() {
  zoomLevel.value -= 0.1;
}

</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - v-for:      Erstellt eine Schleife über die `stats`-Liste.
    - :key:       Eindeutiger Schlüssel für jedes `v-for`-Element.
    - :class:     Bindet Klassen dynamisch, hier für die Farbe der Stat-Kachel.
    - <component :is="...">: Eine dynamische Komponente. Der Tag-Name wird zur Laufzeit bestimmt,
      hier wird die Icon-Komponente aus dem `stat`-Objekt geladen.
    - {{ ... }}:    Gibt den Wert einer Variable als Text aus (Interpolation).
  -->
  <div class="p-4 sm:p-6 bg-gray-100 min-h-full" :style="containerStyle">
    <!-- BARRIEREFREIHEIT: Steuerelemente zur Anpassung der Zoom-Stufe. -->
    <div class="text-right mb-4">
      <span class="text-sm text-gray-600 mr-2">Zoom:</span>
      <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
      <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300 ml-1">+</button>
    </div>

    <h1 class="text-3xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>

    <!-- Übersicht der Statistiken -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
      <div v-for="stat in stats" :key="stat.name" :class="['text-white p-6 rounded-2xl shadow-lg', stat.color]">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-4xl font-extrabold">{{ stat.value }}</p>
            <p class="text-lg font-semibold">{{ stat.name }}</p>
          </div>
          <!-- Die `:is`-Direktive rendert die Komponente, die in der `stat.icon`-Variable gespeichert ist. -->
          <component :is="stat.icon" class="h-10 w-10 opacity-80" />
        </div>
      </div>
    </div>

  </div>
</template>