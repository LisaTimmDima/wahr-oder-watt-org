<script setup>
import { ref } from 'vue';
import { XMarkIcon } from '@heroicons/vue/24/solid';

const isEditModalOpen = ref(false);
const isAddModalOpen = ref(false);
const selectedUser = ref(null);

const newUser = ref({ name: '', email: '', password: '' });

const users = ref([
  { id: 1, name: 'Peter Grünning', email: 'peter@example.com', status: 'active' },
  { id: 2, name: 'Max Mustermann', email: 'max@example.com', status: 'blocked' },
  { id: 3, name: 'Erika Mustermann', email: 'erika@example.com', status: 'blocked' },
]);

// --- Modal-Steuerung ---
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

// --- Aktionen ---
function handleAddNewUser() {
    if (!newUser.value.name || !newUser.value.email || !newUser.value.password) {
        alert('Bitte alle Felder ausfüllen.');
        return;
    }
    users.value.push({
        id: Date.now(),
        name: newUser.value.name,
        email: newUser.value.email,
        status: 'blocked'
    });
    closeModal();
}

function saveChanges() {
    if (!selectedUser.value) return;
    const userIndex = users.value.findIndex(u => u.id === selectedUser.value.id);
    if (userIndex !== -1) {
        users.value[userIndex] = selectedUser.value;
    }
    console.log(`Änderungen für User ${selectedUser.value.id} gespeichert.`);
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

function deleteUser() {
  if (!selectedUser.value) return;
  if (confirm('Sind Sie sicher, dass Sie diesen Benutzer löschen möchten?')) {
    users.value = users.value.filter(u => u.id !== selectedUser.value.id);
    closeModal();
  }
}
</script>

<template>
  <div>
    <div class="bg-white p-6 rounded-xl shadow-sm">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Benutzerverwaltung</h2>
        <button @click="openAddModal" class="bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors duration-200">
          Benutzer Eintragen
        </button>
      </div>
      
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Aktion</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in users" :key="user.id">
            <td class="px-6 py-4">{{ user.name }}</td>
            <td class="px-6 py-4">{{ user.email }}</td>
            <td class="px-6 py-4">
              <span v-if="user.status === 'active'" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Aktiv</span>
              <span v-if="user.status === 'blocked'" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">Gesperrt</span>
            </td>
            <td class="px-6 py-4 text-right">
              <button @click="openEditModal(user)" class="text-blue-600 hover:text-blue-900 transition-colors duration-200">Verwalten</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="isEditModalOpen" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center p-4 z-50">
      <div v-if="selectedUser" class="bg-white rounded-lg shadow-xl w-full max-w-lg p-8">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-gray-800">Benutzer Verwalten</h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors duration-200"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        
        <div>
          <h4 class="font-semibold text-gray-700 mb-4">Benutzerdaten</h4>
          <div class="space-y-4">
            <div>
              <label for="username" class="block text-sm font-medium text-gray-600">Benutzername *</label>
              <input type="text" id="username" v-model="selectedUser.name" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div>
              <label for="email" class="block text-sm font-medium text-gray-600">Email *</label>
              <input type="email" id="email" v-model="selectedUser.email" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
          </div>
        </div>

        <div class="mt-8 pt-6 border-t border-gray-200 flex flex-col sm:flex-row sm:justify-between items-center gap-4">
            <button @click="deleteUser" class="w-full sm:w-auto bg-red-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-red-600 transition-colors duration-200">Löschen</button>
            
            <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto mt-4 sm:mt-0">
                <button v-if="selectedUser.status === 'active'" @click="blockUser" class="bg-blue-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-yellow-600 transition-colors duration-200">Sperren</button>
                <button v-if="selectedUser.status === 'blocked'" @click="activateUser" class="bg-green-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-green-600 transition-colors duration-200">Freischalten</button>
                <button @click="saveChanges" class="bg-gray-800 text-white font-bold py-2 px-4 rounded-lg hover:bg-gray-700 transition-colors duration-200">Speichern</button>
            </div>
        </div>
      </div>
    </div>
    
    <div v-if="isAddModalOpen" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-lg p-8">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-gray-800">Neuen Benutzer Eintragen</h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors duration-200"><XMarkIcon class="h-6 w-6" /></button>
        </div>
        <form @submit.prevent="handleAddNewUser">
          <div class="space-y-4">
            <div>
              <label for="new-username" class="block text-sm font-medium text-gray-600">Benutzername *</label>
              <input type="text" id="new-username" v-model="newUser.name" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div>
              <label for="new-email" class="block text-sm font-medium text-gray-600">Email *</label>
              <input type="email" id="new-email" v-model="newUser.email" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div>
              <label for="new-password" class="block text-sm font-medium text-gray-600">Passwort *</label>
              <input type="password" id="new-password" v-model="newUser.password" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
          </div>
          <div class="mt-8 pt-6 border-t border-gray-200 flex justify-end gap-4">
            <button type="button" @click="closeModal" class="bg-gray-200 text-gray-800 font-bold py-2 px-4 rounded-lg hover:bg-gray-300 transition-colors duration-200">Abbrechen</button>
            <button type="submit" class="bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors duration-200">Konto Erstellen</button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>