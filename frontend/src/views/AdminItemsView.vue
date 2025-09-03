<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Modal-Steuerung, clientseitige Formularlogik.
// - Dima: Implementierung der API-Aufrufe für die CRUD-Operationen (Create, Read, Update, Delete).
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted } from 'vue';
import { XMarkIcon, PlusIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Steuerung der UI und zur Speicherung der Daten.
// ==================================================================================

/**
 * @type {import('vue').Ref<boolean>}
 * @description Steuert die Sichtbarkeit des Modals zum Bearbeiten eines Geräts.
 * @author Lisa
 */
const isEditModalOpen = ref(false);

/**
 * @type {import('vue').Ref<boolean>}
 * @description Steuert die Sichtbarkeit des Modals zum Hinzufügen eines neuen Geräts.
 * @author Lisa
 */
const isAddModalOpen = ref(false);

/**
 * @type {import('vue').Ref<object | null>}
 * @description Speichert eine tiefe Kopie des Geräts, das gerade im Modal bearbeitet wird.
 * @author Lisa
 */
const selectedItem = ref(null);

/**
 * @type {import('vue').Ref<object>}
 * @description Dient als Datenmodell für das Formular zum Hinzufügen eines neuen Geräts.
 * @author Lisa
 */
const newItem = ref({ name: '', properties: [] });

/**
 * @type {import('vue').Ref<string>}
 * @description Bindet den Wert des Textfeldes für neue Eigenschaften in den Modals.
 * @author Lisa
 */
const newPropertyName = ref('');

/**
 * @type {import('vue').Ref<Array<object>>}
 * @description Speichert die Liste aller Geräte, die vom Server geladen wurden.
 * @author Dima
 */
const items = ref([]);

// ==================================================================================
// UI-Methoden (Verantwortlich: Lisa)
// ==================================================================================

/**
 * @function openEditModal
 * @author Lisa
 * @description Öffnet das Bearbeiten-Modal und erstellt eine tiefe Kopie des ausgewählten Geräts, um Änderungen isoliert zu halten.
 * @param {object} item - Das Gerät, das bearbeitet werden soll.
 */
function openEditModal(item) {
  // JSON.parse(JSON.stringify(...)) ist ein einfacher Weg für eine tiefe Kopie, hat aber Nachteile (verwirft z.B. Dates, Functions).
  // Für komplexe Objekte sollte eine robustere Klon-Bibliothek verwendet werden.
  selectedItem.value = JSON.parse(JSON.stringify(item));
  isEditModalOpen.value = true;
}

/**
 * @function openAddModal
 * @author Lisa
 * @description Öffnet das Hinzufügen-Modal und setzt das Formular zurück.
 */
function openAddModal() {
  newItem.value = { name: '', icon: '❓', properties: [] };
  isAddModalOpen.value = true;
}

/**
 * @function closeModal
 * @author Lisa
 * @description Schließt alle Modals und setzt die zugehörigen Zustände zurück.
 */
function closeModal() {
  isEditModalOpen.value = false;
  isAddModalOpen.value = false;
  selectedItem.value = null;
  newPropertyName.value = '';
}

/**
 * @function addProperty
 * @author Lisa
 * @description Fügt eine neue Eigenschaft zum `newItem` oder `selectedItem` im Modal hinzu.
 * @param {object} itemRef - Die reaktive Referenz (`newItem` oder `selectedItem`), zu der die Eigenschaft hinzugefügt wird.
 * @todo Die ID sollte serverseitig generiert werden.
 */
function addProperty(itemRef) {
  if (newPropertyName.value.trim() === '') return;
  itemRef.properties.push({ id: Date.now(), name: newPropertyName.value.trim() });
  newPropertyName.value = '';
}

/**
 * @function removeProperty
 * @author Lisa
 * @description Entfernt eine Eigenschaft aus der Liste im Modal.
 * @param {object} itemRef - Die reaktive Referenz (`newItem` oder `selectedItem`).
 * @param {number} propertyIndex - Der Index der Eigenschaft, die entfernt werden soll.
 */
function removeProperty(itemRef, propertyIndex) {
  itemRef.properties.splice(propertyIndex, 1);
}

// ==================================================================================
// API-Methoden (Verantwortlich: Dima)
// ==================================================================================

/**
 * @function fetchItems
 * @author Dima
 * @description Lädt die Liste aller Geräte vom Server.
 * @todo Diese Funktion muss implementiert werden, um echte Daten zu laden.
 */
async function fetchItems() {
  console.log("Lade Geräte vom Server...");
  // Simuliert einen API-Aufruf
  items.value = [
    { id: 1, name: 'Desktop PC', icon: '🖥️', properties: [{ id: 101, name: 'Strom' }, { id: 102, name: 'Tastatur' }]},
    { id: 2, name: 'Smartphone', icon: '📱', properties: [{ id: 201, name: 'Akku' }, { id: 202, name: 'Bluetooth' }]},
  ];
}

/**
 * @function handleAddNewItem
 * @author Dima
 * @description Sendet das neue Gerät an den Server und aktualisiert bei Erfolg die lokale Liste.
 * @todo Echte API-Anbindung implementieren. Bessere Nutzerführung statt `alert()`.
 */
async function handleAddNewItem() {
  if (!newItem.value.name) {
    alert('Bitte geben Sie einen Gerätenamen ein.');
    return;
  }
  console.log("Sende neues Gerät zum Server:", newItem.value);
  // Hier käme der API-Aufruf, z.B. await fetch('/api/items', { method: 'POST', ... });
  await fetchItems(); // Liste neu laden, um die neue ID vom Server zu erhalten
  closeModal();
}

/**
 * @function saveChanges
 * @author Dima
 * @description Sendet die Änderungen eines Geräts an den Server und aktualisiert bei Erfolg die lokale Liste.
 * @todo Echte API-Anbindung implementieren.
 */
async function saveChanges() {
  if (!selectedItem.value) return;
  console.log("Sende Änderungen für Gerät", selectedItem.value.id, ":", selectedItem.value);
  // Hier käme der API-Aufruf, z.B. await fetch(`/api/items/${selectedItem.value.id}`, { method: 'PUT', ... });
  await fetchItems();
  closeModal();
}

/**
 * @function deleteItem
 * @author Dima
 * @description Löscht ein Gerät auf dem Server und entfernt es bei Erfolg aus der lokalen Liste.
 * @todo Echte API-Anbindung implementieren. `confirm()` durch ein UI-Element ersetzen.
 */
async function deleteItem() {
  if (!selectedItem.value) return;
  if (confirm(`Sind Sie sicher, dass Sie das Gerät "${selectedItem.value.name}" löschen möchten?`)) {
    console.log("Lösche Gerät mit ID:", selectedItem.value.id);
    // Hier käme der API-Aufruf, z.B. await fetch(`/api/items/${selectedItem.value.id}`, { method: 'DELETE', ... });
    await fetchItems();
    closeModal();
  }
}

// ==================================================================================
// Lifecycle Hooks
// ==================================================================================

/**
 * onMounted(): Wird ausgeführt, nachdem die Komponente in das DOM eingehängt wurde.
 * @author Dima
 */
onMounted(() => {
  fetchItems();
});

</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - v-for:      Schleife über eine Liste, um Elemente (Tabellenzeilen, Karten) zu rendern.
    - @click:      Führt eine Methode bei einem Klick aus.
    - v-if:       Bedingtes Rendern, hier zur Anzeige der Modals.
    - v-model:    Zwei-Wege-Datenbindung für Formularfelder.
    - @click.self: Event-Handler, der nur auslöst, wenn auf das Element selbst geklickt wird (nicht auf Kinder).
                  Wird hier für das Schließen des Modals bei Klick auf den Hintergrund genutzt.
  -->
  <div>
    <div class="bg-white p-4 sm:p-6 rounded-2xl shadow-lg">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800 mb-4 sm:mb-0">Geräte & Eigenschaften</h2>
        <button @click="openAddModal" class="inline-flex items-center gap-2 bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
          <PlusIcon class="h-5 w-5"/>
          <span>Neues Gerät</span>
        </button>
      </div>

      <!-- Desktop-Tabelle -->
      <div class="hidden sm:block">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Gerät</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Eigenschaften</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Aktion</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="item in items" :key="item.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center gap-3">
                  <span class="text-2xl">{{ item.icon }}</span>
                  <span class="font-semibold">{{ item.name }}</span>
                </div>
              </td>
              <td class="px-6 py-4">
                <ul class="list-disc list-inside text-sm text-gray-600">
                  <li v-for="prop in item.properties" :key="prop.id">{{ prop.name }}</li>
                </ul>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right">
                <button @click="openEditModal(item)" class="text-blue-600 hover:text-blue-900 font-semibold">Verwalten</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Mobile Ansicht als Karten -->
      <div class="block sm:hidden space-y-4">
        <div v-for="item in items" :key="item.id" class="bg-gray-50 p-4 rounded-lg shadow-sm">
          <div class="flex justify-between items-start">
            <div class="flex items-center gap-3">
              <span class="text-2xl">{{ item.icon }}</span>
              <span class="font-bold text-gray-800">{{ item.name }}</span>
            </div>
            <button @click="openEditModal(item)" class="text-blue-600 hover:text-blue-900 font-semibold">Verwalten</button>
          </div>
          <div class="mt-4 pl-2">
            <h4 class="font-semibold text-sm text-gray-600 mb-1">Eigenschaften:</h4>
            <ul class="list-disc list-inside text-sm text-gray-600">
              <li v-for="prop in item.properties" :key="prop.id">{{ prop.name }}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals (Hintergrund & Container) -->
    <div v-if="isEditModalOpen || isAddModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" @click.self="closeModal">
      
      <!-- Bearbeiten-Modal -->
      <div v-if="isEditModalOpen && selectedItem" class="bg-white rounded-2xl shadow-xl w-full max-w-2xl p-6 sm:p-8">
        <div class="flex justify-between items-center pb-4 border-b border-gray-200">
          <h3 class="text-2xl font-bold text-gray-800">Gerät Verwalten</h3>
          <button @click="closeModal" class="p-1 rounded-full text-gray-400 hover:bg-gray-200"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        <div class="mt-6 space-y-6">
          <div>
            <label for="item-name" class="block text-sm font-medium text-gray-700">Gerätename</label>
            <input type="text" id="item-name" v-model="selectedItem.name" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
          </div>
          <div>
            <h4 class="text-sm font-medium text-gray-700 mb-2">Eigenschaften</h4>
            <ul v-if="selectedItem.properties.length > 0" class="space-y-2 mb-4">
              <li v-for="(prop, index) in selectedItem.properties" :key="prop.id" class="flex items-center justify-between bg-gray-100 p-2 rounded-lg">
                <span class="text-gray-800">{{ prop.name }}</span>
                <button @click="removeProperty(selectedItem, index)" class="text-red-500 hover:text-red-700"><XMarkIcon class="h-5 w-5" /></button>
              </li>
            </ul>
            <div class="flex gap-2">
              <input type="text" v-model="newPropertyName" @keyup.enter="addProperty(selectedItem)" placeholder="Neue Eigenschaft..." class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
              <button @click="addProperty(selectedItem)" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">Add</button>
            </div>
          </div>
        </div>
        <div class="mt-8 pt-6 border-t border-gray-200 flex flex-col-reverse sm:flex-row sm:justify-between gap-3">
          <button @click="deleteItem" class="w-full sm:w-auto inline-flex justify-center px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-red-700 bg-white hover:bg-red-50">Gerät Löschen</button>
          <div class="flex flex-col-reverse sm:flex-row gap-3 w-full sm:w-auto">
            <button @click="closeModal" class="w-full sm:w-auto inline-flex justify-center px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50">Abbrechen</button>
            <button @click="saveChanges" class="w-full sm:w-auto inline-flex justify-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">Änderungen Speichern</button>
          </div>
        </div>
      </div>

      <!-- Hinzufügen-Modal -->
      <div v-if="isAddModalOpen" class="bg-white rounded-2xl shadow-xl w-full max-w-2xl p-6 sm:p-8">
         <div class="flex justify-between items-center pb-4 border-b border-gray-200">
          <h3 class="text-2xl font-bold text-gray-800">Neues Gerät Eintragen</h3>
          <button @click="closeModal" class="p-1 rounded-full text-gray-400 hover:bg-gray-200"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        <div class="mt-6 space-y-6">
          <div>
            <label for="new-item-name" class="block text-sm font-medium text-gray-700">Gerätename</label>
            <input type="text" id="new-item-name" v-model="newItem.name" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
          </div>
          <div>
            <h4 class="text-sm font-medium text-gray-700 mb-2">Eigenschaften</h4>
            <ul v-if="newItem.properties.length > 0" class="space-y-2 mb-4">
              <li v-for="(prop, index) in newItem.properties" :key="prop.id" class="flex items-center justify-between bg-gray-100 p-2 rounded-lg">
                <span class="text-gray-800">{{ prop.name }}</span>
                <button @click="removeProperty(newItem, index)" class="text-red-500 hover:text-red-700"><XMarkIcon class="h-5 w-5" /></button>
              </li>
            </ul>
            <div class="flex gap-2">
              <input type="text" v-model="newPropertyName" @keyup.enter="addProperty(newItem)" placeholder="Neue Eigenschaft..." class="block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
              <button @click="addProperty(newItem)" type="button" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">Add</button>
            </div>
          </div>
        </div>
        <div class="mt-8 pt-6 border-t border-gray-200 flex justify-end gap-3">
          <button @click="closeModal" type="button" class="px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50">Abbrechen</button>
          <button @click="handleAddNewItem" type="button" class="inline-flex justify-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">Gerät Erstellen</button>
        </div>
      </div>
    </div>

  </div>
</template>