<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Modal-Steuerung, clientseitige Formularlogik.
// - Dima: Implementierung der API-Aufrufe für die CRUD-Operationen (Create, Read, Update, Delete).
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted, computed } from 'vue';
import { XMarkIcon, UserPlusIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Steuerung der UI und zur Speicherung der Daten.
// ==================================================================================

const isEditModalOpen = ref(false);
const isAddModalOpen = ref(false);
const selectedUser = ref(null);
const newUser = ref({ name: '', email: '', password: '' });
const users = ref([]);

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

function openEditModal(user) {
  selectedUser.value = { ...user }; 
  isEditModalOpen.value = true;
}

function openAddModal() {
  newUser.value = { name: '', email: '', password: '' };
  isAddModalOpen.value = true;
}

function closeModal() {
  isEditModalOpen.value = false;
  isAddModalOpen.value = false;
  selectedUser.value = null;
}

// ==================================================================================
// API-Methoden (Verantwortlich: Dima)
// ==================================================================================

async function fetchUsers() {
  console.log("Lade Benutzer vom Server...");
  users.value = [
    { id: 1, name: 'Peter Grünning', email: 'peter@example.com', status: 'active' },
    { id: 2, name: 'Max Mustermann', email: 'max@example.com', status: 'blocked' },
    { id: 3, name: 'Erika Mustermann', email: 'erika@example.com', status: 'blocked' },
  ];
}

async function handleAddNewUser() {
  if (!newUser.value.name || !newUser.value.email || !newUser.value.password) {
    alert('Bitte alle Felder ausfüllen.');
    return;
  }
  console.log("Sende neuen Benutzer zum Server:", newUser.value);
  await fetchUsers();
  closeModal();
}

async function saveChanges() {
  if (!selectedUser.value) return;
  console.log("Sende Änderungen für Benutzer", selectedUser.value.id, ":", selectedUser.value);
  await fetchUsers();
  closeModal();
}

function activateUser() {
  if (!selectedUser.value) return;
  selectedUser.value.status = 'active';
  saveChanges();
}

function blockUser() {
  if (!selectedUser.value) return;
  selectedUser.value.status = 'blocked';
  saveChanges();
}

async function deleteUser() {
  if (!selectedUser.value) return;
  if (confirm(`Sind Sie sicher, dass Sie den Benutzer "${selectedUser.value.name}" löschen möchten?`)) {
    console.log("Lösche Benutzer mit ID:", selectedUser.value.id);
    await fetchUsers();
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
  fetchUsers();
});

</script>

<template>
  <div>
    <div :style="containerStyle" :class="{ 'high-contrast': isHighContrast }">
        <div class="bg-white p-4 sm:p-6 rounded-2xl shadow-lg">
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800 mb-4 sm:mb-0">Benutzerverwaltung</h2>
            <div class="flex items-center gap-4">
                <!-- BARRIEREFREIHEIT: Steuerelemente für Zoom und Kontrast. -->
                <div class="flex items-center gap-2">
                    <span class="text-sm text-gray-600">Zoom:</span>
                    <button @click="decreaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">-</button>
                    <button @click="increaseZoom" class="px-2 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">+</button>
                </div>
                <button @click="toggleHighContrast" class="px-3 py-1 text-sm bg-gray-200 rounded-md hover:bg-gray-300">Kontrast</button>
                <button @click="openAddModal" class="inline-flex items-center gap-2 bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors duration-200">
                    <UserPlusIcon class="h-5 w-5"/>
                    <span>Benutzer Eintragen</span>
                </button>
            </div>
        </div>
        
        <!-- Desktop-Tabelle -->
        <div class="hidden sm:block">
            <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
                <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Aktion</th>
                </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">{{ user.name }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ user.email }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="['px-2 inline-flex text-xs leading-5 font-semibold rounded-full', user.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800']">{{ user.status === 'active' ? 'Aktiv' : 'Gesperrt' }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right">
                    <button @click="openEditModal(user)" class="text-blue-600 hover:text-blue-900 font-semibold transition-colors duration-200">Verwalten</button>
                </td>
                </tr>
            </tbody>
            </table>
        </div>

        <!-- Mobile Ansicht als Karten -->
        <div class="block sm:hidden space-y-4">
            <div v-for="user in users" :key="user.id" class="bg-gray-50 p-4 rounded-lg shadow-sm">
            <div class="flex justify-between items-start">
                <div>
                <p class="font-bold text-gray-800">{{ user.name }}</p>
                <p class="text-sm text-gray-600">{{ user.email }}</p>
                </div>
                <span :class="['px-2 inline-flex text-xs leading-5 font-semibold rounded-full', user.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800']">{{ user.status === 'active' ? 'Aktiv' : 'Gesperrt' }}</span>
            </div>
            <div class="mt-4 text-right">
                <button @click="openEditModal(user)" class="text-blue-600 hover:text-blue-900 font-semibold transition-colors duration-200">Verwalten</button>
            </div>
            </div>
        </div>

        </div>
    </div>

    <!-- Modals -->
    <div v-if="isEditModalOpen || isAddModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" @click.self="closeModal">
      <div v-if="selectedUser" class="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6 sm:p-8">
        <div class="flex justify-between items-center pb-4 border-b border-gray-200">
          <h3 class="text-2xl font-bold text-gray-800">Benutzer Verwalten</h3>
          <button @click="closeModal" class="p-1 rounded-full text-gray-400 hover:bg-gray-200 hover:text-gray-600 transition-colors"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        <div class="mt-6">
          <div class="space-y-4">
            <div>
              <label for="username" class="block text-sm font-medium text-gray-700">Benutzername</label>
              <input type="text" id="username" v-model="selectedUser.name" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
              <input type="email" id="email" v-model="selectedUser.email" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
          </div>
        </div>
        <div class="mt-8 pt-6 border-t border-gray-200 flex flex-col sm:flex-row-reverse gap-3">
            <button @click="saveChanges" class="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">Speichern</button>
            <button v-if="selectedUser.status === 'active'" @click="blockUser" class="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-yellow-500 hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500">Sperren</button>
            <button v-if="selectedUser.status === 'blocked'" @click="activateUser" class="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-green-500 hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">Freischalten</button>
            <button @click="deleteUser" class="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-red-700 bg-white hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:mr-auto">Löschen</button>
        </div>
      </div>
       <div v-if="isAddModalOpen" class="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6 sm:p-8">
        <div class="flex justify-between items-center pb-4 border-b border-gray-200">
          <h3 class="text-2xl font-bold text-gray-800">Neuen Benutzer Eintragen</h3>
          <button @click="closeModal" class="p-1 rounded-full text-gray-400 hover:bg-gray-200 hover:text-gray-600 transition-colors"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        <form @submit.prevent="handleAddNewUser" class="mt-6">
          <div class="space-y-4">
            <div>
              <label for="new-username" class="block text-sm font-medium text-gray-700">Benutzername</label>
              <input type="text" id="new-username" v-model="newUser.name" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" required>
            </div>
            <div>
              <label for="new-email" class="block text-sm font-medium text-gray-700">Email</label>
              <input type="email" id="new-email" v-model="newUser.email" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" required>
            </div>
            <div>
              <label for="new-password" class="block text-sm font-medium text-gray-700">Passwort</label>
              <input type="password" id="new-password" v-model="newUser.password" class="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" required>
            </div>
          </div>
          <div class="mt-8 pt-6 border-t border-gray-200 flex justify-end gap-4">
            <button type="button" @click="closeModal" class="px-4 py-2 border border-gray-300 text-base font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">Abbrechen</button>
            <button type="submit" class="inline-flex justify-center items-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">Konto Erstellen</button>
          </div>
        </form>
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
.high-contrast .text-gray-800, .high-contrast .text-gray-600, .high-contrast .text-gray-500, .high-contrast .text-green-800, .high-contrast .text-red-800 {
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
.high-contrast .bg-green-100 { background-color: #004d00 !important; }
.high-contrast .bg-red-100 { background-color: #660000 !important; }
.high-contrast input { 
    background-color: #555 !important;
    border: 1px solid yellow !important;
    color: #fff !important;
}
</style>