<script setup>
import { ref } from 'vue';
import { XMarkIcon, PlusIcon } from '@heroicons/vue/24/solid';

const isEditModalOpen = ref(false);
const isAddModalOpen = ref(false);
const selectedItem = ref(null);

const newItem = ref({ name: '', properties: [] });
const newPropertyName = ref('');

const items = ref([
  { 
    id: 1, 
    name: 'Desktop PC', 
    icon: '🖥️', 
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

function openEditModal(item) {
  selectedItem.value = JSON.parse(JSON.stringify(item));
  isEditModalOpen.value = true;
}
function openAddModal() {
  newItem.value = { name: '', properties: [] };
  isAddModalOpen.value = true;
}
function closeModal() {
  isEditModalOpen.value = false;
  isAddModalOpen.value = false;
  selectedItem.value = null;
  newPropertyName.value = '';
}

function handleAddNewItem() {
  if (!newItem.value.name) {
    alert('Bitte geben Sie einen Gerätenamen ein.');
    return;
  }
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

function addProperty(itemRef) {
  if (newPropertyName.value.trim() === '') return;
  itemRef.properties.push({ id: Date.now(), name: newPropertyName.value.trim() });
  newPropertyName.value = '';
}

function removeProperty(itemRef, propertyIndex) {
  itemRef.properties.splice(propertyIndex, 1);
}
</script>

<template>
  <div>
    <div class="bg-white p-4 sm:p-6 rounded-2xl shadow-lg">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800 mb-4 sm:mb-0">Geräte & Eigenschaften</h2>
        <button @click="openAddModal" class="inline-flex items-center gap-2 bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
          <PlusIcon class="h-5 w-5"/>
          <span>Neues Gerät</span>
        </button>
      </div>

      <!-- Desktop Table -->
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

      <!-- Mobile Card List -->
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

    <!-- Modals -->
    <div v-if="isEditModalOpen || isAddModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" @click.self="closeModal">
      <!-- Edit Modal -->
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
          <button @click="deleteItem" class="w-full sm:w-auto inline-flex justify-center px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50">Gerät Löschen</button>
          <div class="flex flex-col-reverse sm:flex-row gap-3 w-full sm:w-auto">
            <button @click="closeModal" class="w-full sm:w-auto inline-flex justify-center px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50">Abbrechen</button>
            <button @click="saveChanges" class="w-full sm:w-auto inline-flex justify-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">Änderungen Speichern</button>
          </div>
        </div>
      </div>

      <!-- Add Modal -->
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
