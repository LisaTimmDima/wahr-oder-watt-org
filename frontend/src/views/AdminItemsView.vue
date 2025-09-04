<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Modal-Steuerung, clientseitige Formularlogik.
// - Dima: Implementierung der API-Aufrufe für die CRUD-Operationen (Create, Read, Update, Delete).
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted, computed } from 'vue';
import { XMarkIcon, PlusIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Steuerung der UI und zur Speicherung der Daten.
// ==================================================================================

const isEditModalOpen = ref(false);
const isAddModalOpen = ref(false);
const selectedItem = ref(null);
const newItem = ref({ name: '', properties: [] });
const newPropertyName = ref('');
const items = ref([]);

// BARRIEREFREIHEIT: Reaktive Variablen für Zoom und Kontrast.
const zoomLevel = ref(1);
const isHighContrast = ref(false);

// ==================================================================================
// Computed Properties
// ==================================================================================

const containerStyle = computed(() => ({ zoom: zoomLevel.value }));

// ==================================================================================
// UI-Methoden (Verantwortlich: Lisa)
// ==================================================================================

function openEditModal(item) {
  selectedItem.value = JSON.parse(JSON.stringify(item));
  isEditModalOpen.value = true;
}

function openAddModal() {
  newItem.value = { name: '', icon: '❓', properties: [] };
  isAddModalOpen.value = true;
}

function closeModal() {
  isEditModalOpen.value = false;
  isAddModalOpen.value = false;
  selectedItem.value = null;
  newPropertyName.value = '';
}

function addProperty(itemRef) {
  if (newPropertyName.value.trim() === '') return;
  itemRef.properties.push({ id: Date.now(), name: newPropertyName.value.trim() });
  newPropertyName.value = '';
}

function removeProperty(itemRef, propertyIndex) {
  itemRef.properties.splice(propertyIndex, 1);
}

// ==================================================================================
// API-Methoden (Verantwortlich: Dima)
// ==================================================================================

async function fetchItems() {
  console.log("Lade Geräte vom Server...");
  items.value = [
    { id: 1, name: 'Desktop PC', icon: '🖥️', properties: [{ id: 101, name: 'Strom' }, { id: 102, name: 'Tastatur' }]},
    { id: 2, name: 'Smartphone', icon: '📱', properties: [{ id: 201, name: 'Akku' }, { id: 202, name: 'Bluetooth' }]},
  ];
}

async function handleAddNewItem() {
  if (!newItem.value.name) {
    alert('Bitte geben Sie einen Gerätenamen ein.');
    return;
  }
  console.log("Sende neues Gerät zum Server:", newItem.value);
  await fetchItems();
  closeModal();
}

async function saveChanges() {
  if (!selectedItem.value) return;
  console.log("Sende Änderungen für Gerät", selectedItem.value.id, ":", selectedItem.value);
  await fetchItems();
  closeModal();
}

async function deleteItem() {
  if (!selectedItem.value) return;
  if (confirm(`Sind Sie sicher, dass Sie das Gerät "${selectedItem.value.name}" löschen möchten?`)) {
    console.log("Lösche Gerät mit ID:", selectedItem.value.id);
    await fetchItems();
    closeModal();
  }
}

// ==================================================================================
// BARRIEREFREIHEIT: Methoden
// ==================================================================================

function increaseZoom() {
  zoomLevel.value += 0.1;
}
function decreaseZoom() {
  zoomLevel.value -= 0.1;
}
function toggleHighContrast() {
  isHighContrast.value = !isHighContrast.value;
}

// ==================================================================================
// Lifecycle Hooks
// ==================================================================================

onMounted(() => {
  fetchItems();
});

</script>

<template>
  <div>
    <div :style="containerStyle" :class="{ 'high-contrast': isHighContrast }">
        <div class="bg-white p-4 sm:p-6 rounded-2xl shadow-lg">
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800 mb-4 sm:mb-0">Geräte & Eigenschaften</h2>
            <div class="flex items-center gap-4">
                <!-- BARRIEREFREIHEIT: Steuerelemente für Zoom und Kontrast. -->
                <div class="flex items-center gap-2">
                    <span class="text-sm text-gray-600">Zoom:</span>
                    <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
                    <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">+</button>
                </div>
                <button @click="toggleHighContrast" class="px-3 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">Kontrast</button>
                <button @click="openAddModal" class="inline-flex items-center gap-2 bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
                    <PlusIcon class="h-5 w-5"/>
                    <span>Neues Gerät</span>
                </button>
            </div>
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
    </div>

    <!-- Modals -->
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

<style>
.high-contrast {
  background-color: #000 !important;
  color: #fff !important;
}
.high-contrast .bg-white, .high-contrast .bg-gray-50 {
  background-color: #000 !important;
  border: 1px solid yellow;
}
.high-contrast .text-gray-800, .high-contrast .text-gray-600, .high-contrast .text-gray-500 {
  color: #fff !important;
}
.high-contrast .text-blue-600 {
  color: yellow !important;
}
.high-contrast .divide-y > :not([hidden]) ~ :not([hidden]) {
    border-color: yellow !important;
}
.high-contrast button {
    border: 1px solid yellow !important;
    background-color: #333 !important;
    color: #fff !important;
}
.high-contrast input { 
    background-color: #555 !important;
    border: 1px solid yellow !important;
    color: #fff !important;
}
</style>