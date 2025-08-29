<script setup>
import { ref } from 'vue';
import { XMarkIcon } from '@heroicons/vue/24/solid';

// Zustände für die Pop-up-Fenster
const isEditModalOpen = ref(false);
const isAddModalOpen = ref(false);
const selectedItem = ref(null);

// --- GEÄNDERT: `icon` entfernt, `properties` ist jetzt wichtig ---
const newItem = ref({ name: '', properties: [] });
const newPropertyName = ref(''); // Für das Hinzufügen neuer Eigenschaften

// Mock-Daten
const items = ref([
  { 
    id: 1, 
    name: 'Desktop PC', 
    icon: '💻', 
    properties: [
      { id: 101, name: 'Strom' },
      { id: 102, name: 'Tastatur' },
      { id: 103, name: 'Wi-Fi' },
    ]
  },
  { 
    id: 2, 
    name: 'Smartphone', 
    icon: '📱', 
    properties: [
      { id: 201, name: 'Akku' },
      { id: 202, name: 'Bluetooth' },
      { id: 203, name: 'Touchscreen' },
    ]
  },
]);

// --- Modal-Steuerung ---
function openEditModal(item) {
  selectedItem.value = JSON.parse(JSON.stringify(item));
  isEditModalOpen.value = true;
}
function openAddModal() {
  // Setzt das Formular für ein neues Gerät zurück
  newItem.value = { name: '', properties: [] };
  isAddModalOpen.value = true;
}
function closeModal() {
  isEditModalOpen.value = false;
  isAddModalOpen.value = false;
  selectedItem.value = null;
  newPropertyName.value = '';
}

// --- Aktionen ---
function handleAddNewItem() {
  if (!newItem.value.name) {
    alert('Bitte geben Sie einen Gerätenamen ein.');
    return;
  }
  // Fügt das neue Gerät mit einem Emoji als Standard-Icon hinzu
  items.value.push({ ...newItem.value, id: Date.now(), icon: '❓' });
  closeModal();
}

function saveChanges() {
  if (!selectedItem.value) return;
  const itemIndex = items.value.findIndex(i => i.id === selectedItem.value.id);
  if (itemIndex !== -1) {
    items.value[itemIndex] = selectedItem.value;
  }
  closeModal();
}

function deleteItem() {
  if (!selectedItem.value) return;
  if (confirm('Sind Sie sicher, dass Sie dieses Gerät löschen möchten?')) {
    items.value = items.value.filter(i => i.id !== selectedItem.value.id);
    closeModal();
  }
}

// --- Eigenschaften-Verwaltung ---
// `itemRef` kann entweder `selectedItem` oder `newItem` sein
function addProperty(itemRef) {
  if (newPropertyName.value.trim() === '') return;
  itemRef.properties.push({ id: Date.now(), name: newPropertyName.value.trim() });
  newPropertyName.value = ''; // Eingabefeld leeren
}

function removeProperty(itemRef, propertyIndex) {
  itemRef.properties.splice(propertyIndex, 1);
}
</script>

<template>
  <div>
    <div class="bg-white p-6 rounded-xl shadow-sm">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Geräte und Eigenschaften</h2>
        <button @click="openAddModal" class="bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700">
          Neues Gerät Eintragen
        </button>
      </div>

      <div class="min-w-full border border-gray-200 rounded-lg">
        <div class="grid grid-cols-3 bg-gray-50 font-semibold p-3">
          <span>Gerät</span>
          <span>Eigenschaften</span>
          <span class="text-right">Aktion</span>
        </div>
        <div v-for="item in items" :key="item.id" class="grid grid-cols-3 p-3 border-t border-gray-200 items-start">
          <span class="flex items-center gap-3 font-semibold">
            <span class="text-2xl">{{ item.icon }}</span>
            {{ item.name }}
          </span>
          <ul class="list-decimal list-inside text-sm text-gray-600">
            <li v-for="prop in item.properties" :key="prop.id">{{ prop.name }}</li>
          </ul>
          <div class="text-right">
            <button @click="openEditModal(item)" class="bg-gray-200 text-gray-800 font-semibold py-1 px-4 rounded hover:bg-gray-300">
              Verwalten
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isEditModalOpen" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center p-4 z-50">
      <div v-if="selectedItem" class="bg-white rounded-lg shadow-xl w-full max-w-2xl p-8">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-gray-800">Gerät Verwalten</h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        <div class="space-y-4">
          <div>
            <label for="item-name" class="block text-sm font-medium text-gray-600">Gerätename *</label>
            <input type="text" id="item-name" v-model="selectedItem.name" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm">
          </div>
          <div>
            <h4 class="font-semibold text-gray-700 mb-2">Eigenschaften</h4>
            <ul v-if="selectedItem.properties.length > 0" class="space-y-2 mb-4">
              <li v-for="(prop, index) in selectedItem.properties" :key="prop.id" class="flex items-center justify-between bg-gray-100 p-2 rounded">
                <span>{{ prop.name }}</span>
                <button @click="removeProperty(selectedItem, index)" class="text-red-500 hover:text-red-700"><XMarkIcon class="h-4 w-4" /></button>
              </li>
            </ul>
            <div class="flex gap-2">
              <input type="text" v-model="newPropertyName" @keyup.enter="addProperty(selectedItem)" placeholder="Neue Eigenschaft hinzufügen..." class="block w-full rounded-md border-gray-300 shadow-sm">
              <button @click="addProperty(selectedItem)" class="bg-gray-200 font-semibold py-2 px-4 rounded hover:bg-gray-300">Add</button>
            </div>
          </div>
        </div>
        <div class="mt-8 pt-6 border-t border-gray-200 flex justify-between items-center">
          <button @click="deleteItem" class="bg-red-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-red-600">Gerät Löschen</button>
          <div class="flex gap-4">
            <button @click="closeModal" class="bg-gray-200 text-gray-800 font-bold py-2 px-4 rounded-lg hover:bg-gray-300">Abbrechen</button>
            <button @click="saveChanges" class="bg-gray-800 text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-700">Änderungen Speichern</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isAddModalOpen" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl p-8">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-gray-800">Neues Gerät Eintragen</h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        
        <div class="space-y-4">
          <div>
            <label for="new-item-name" class="block text-sm font-medium text-gray-600">Gerätename *</label>
            <input type="text" id="new-item-name" v-model="newItem.name" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm">
          </div>
          
          <div>
            <h4 class="font-semibold text-gray-700 mb-2">Eigenschaften</h4>
            <ul v-if="newItem.properties.length > 0" class="space-y-2 mb-4">
              <li v-for="(prop, index) in newItem.properties" :key="prop.id" class="flex items-center justify-between bg-gray-100 p-2 rounded">
                <span>{{ prop.name }}</span>
                <button @click="removeProperty(newItem, index)" class="text-red-500 hover:text-red-700"><XMarkIcon class="h-4 w-4" /></button>
              </li>
            </ul>
            <div class="flex gap-2">
              <input type="text" v-model="newPropertyName" @keyup.enter="addProperty(newItem)" placeholder="Neue Eigenschaft hinzufügen..." class="block w-full rounded-md border-gray-300 shadow-sm">
              <button @click="addProperty(newItem)" type="button" class="bg-gray-200 font-semibold py-2 px-4 rounded hover:bg-gray-300">Add</button>
            </div>
          </div>
        </div>

        <div class="mt-8 pt-6 border-t border-gray-200 flex justify-end gap-4">
          <button @click="closeModal" type="button" class="bg-gray-200 text-gray-800 font-bold py-2 px-4 rounded-lg hover:bg-gray-300">Abbrechen</button>
          <button @click="handleAddNewItem" type="button" class="bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700">Gerät Erstellen</button>
        </div>
      </div>
    </div>
  </div>
</template>