<script setup>
// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: UI-Struktur, Modal-Steuerung, clientseitige Formularlogik.
// - Dima: Implementierung der API-Aufrufe für die CRUD-Operationen (Create, Read, Update, Delete).
// ==================================================================================

// import: Lädt Vue-Funktionen (ref, onMounted) und Icon-Komponenten.
import { ref, onMounted } from 'vue';
import { XMarkIcon, UserPlusIcon } from '@heroicons/vue/24/solid';

// ==================================================================================
// Reactive State: ref() erstellt reaktive Variablen zur Steuerung der UI und zur Speicherung der Daten.
// ==================================================================================

/**
 * @type {import('vue').Ref<boolean>}
 * @description Steuert die Sichtbarkeit des Modals zum Bearbeiten eines Benutzers.
 * @author Lisa
 */
const isEditModalOpen = ref(false);

/**
 * @type {import('vue').Ref<boolean>}
 * @description Steuert die Sichtbarkeit des Modals zum Hinzufügen eines neuen Benutzers.
 * @author Lisa
 */
const isAddModalOpen = ref(false);

/**
 * @type {import('vue').Ref<object | null>}
 * @description Speichert eine Kopie des Benutzers, der gerade im Modal bearbeitet wird.
 * @author Lisa
 */
const selectedUser = ref(null);

/**
 * @type {import('vue').Ref<object>}
 * @description Dient als Datenmodell für das Formular zum Hinzufügen eines neuen Benutzers.
 * @author Lisa
 */
const newUser = ref({ name: '', email: '', password: '' });

/**
 * @type {import('vue').Ref<Array<object>>}
 * @description Speichert die Liste aller Benutzer, die vom Server geladen wurden.
 * @author Dima
 */
const users = ref([]);

// ==================================================================================
// UI-Methoden (Verantwortlich: Lisa)
// ==================================================================================

/**
 * @function openEditModal
 * @author Lisa
 * @description Öffnet das Bearbeiten-Modal und erstellt eine flache Kopie des ausgewählten Benutzers.
 * @param {object} user - Der Benutzer, der bearbeitet werden soll.
 */
function openEditModal(user) {
  selectedUser.value = { ...user }; 
  isEditModalOpen.value = true;
}

/**
 * @function openAddModal
 * @author Lisa
 * @description Öffnet das Hinzufügen-Modal und setzt das Formular zurück.
 */
function openAddModal() {
  newUser.value = { name: '', email: '', password: '' };
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
  selectedUser.value = null;
}

// ==================================================================================
// API-Methoden (Verantwortlich: Dima)
// ==================================================================================

/**
 * @function fetchUsers
 * @author Dima
 * @description Lädt die Liste aller Benutzer vom Server.
 * @todo Diese Funktion muss implementiert werden, um echte Daten zu laden.
 */
async function fetchUsers() {
  console.log("Lade Benutzer vom Server...");
  // Simuliert einen API-Aufruf
  users.value = [
    { id: 1, name: 'Peter Grünning', email: 'peter@example.com', status: 'active' },
    { id: 2, name: 'Max Mustermann', email: 'max@example.com', status: 'blocked' },
    { id: 3, name: 'Erika Mustermann', email: 'erika@example.com', status: 'blocked' },
  ];
}

/**
 * @function handleAddNewUser
 * @author Dima
 * @description Sendet den neuen Benutzer an den Server und aktualisiert bei Erfolg die lokale Liste.
 * @todo Echte API-Anbindung implementieren. Bessere Nutzerführung statt `alert()`.
 */
async function handleAddNewUser() {
  if (!newUser.value.name || !newUser.value.email || !newUser.value.password) {
    alert('Bitte alle Felder ausfüllen.');
    return;
  }
  console.log("Sende neuen Benutzer zum Server:", newUser.value);
  // Hier käme der API-Aufruf, z.B. await fetch('/api/users', { method: 'POST', ... });
  await fetchUsers(); // Liste neu laden
  closeModal();
}

/**
 * @function saveChanges
 * @author Dima
 * @description Sendet die Änderungen eines Benutzers an den Server.
 * @todo Echte API-Anbindung implementieren.
 */
async function saveChanges() {
  if (!selectedUser.value) return;
  console.log("Sende Änderungen für Benutzer", selectedUser.value.id, ":", selectedUser.value);
  // Hier käme der API-Aufruf, z.B. await fetch(`/api/users/${selectedUser.value.id}`, { method: 'PUT', ... });
  await fetchUsers(); // Liste neu laden, um Änderungen zu sehen
  closeModal();
}

/**
 * @function activateUser
 * @author Dima
 * @description Ändert den Status eines Benutzers auf 'active' und speichert die Änderung.
 */
function activateUser() {
  if (!selectedUser.value) return;
  selectedUser.value.status = 'active';
  saveChanges();
}

/**
 * @function blockUser
 * @author Dima
 * @description Ändert den Status eines Benutzers auf 'blocked' und speichert die Änderung.
 */
function blockUser() {
  if (!selectedUser.value) return;
  selectedUser.value.status = 'blocked';
  saveChanges();
}

/**
 * @function deleteUser
 * @author Dima
 * @description Löscht einen Benutzer auf dem Server.
 * @todo Echte API-Anbindung implementieren. `confirm()` durch ein UI-Element ersetzen.
 */
async function deleteUser() {
  if (!selectedUser.value) return;
  if (confirm(`Sind Sie sicher, dass Sie den Benutzer "${selectedUser.value.name}" löschen möchten?`)) {
    console.log("Lösche Benutzer mit ID:", selectedUser.value.id);
    // Hier käme der API-Aufruf, z.B. await fetch(`/api/users/${selectedUser.value.id}`, { method: 'DELETE', ... });
    await fetchUsers();
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
  fetchUsers();
});

</script>

<template>
  <!-- 
    Vue Template Grundlagen:
    - v-for:      Schleife über eine Liste, um Elemente (Tabellenzeilen, Karten) zu rendern.
    - @click:      Führt eine Methode bei einem Klick aus.
    - v-if:       Bedingtes Rendern, hier zur Anzeige der Modals.
    - v-model:    Zwei-Wege-Datenbindung für Formularfelder.
    - :class:     Bindet Klassen dynamisch, z.B. für den farbigen Status-Badge.
  -->
  <div>
    <div class="bg-white p-4 sm:p-6 rounded-2xl shadow-lg">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800 mb-4 sm:mb-0">Benutzerverwaltung</h2>
        <button @click="openAddModal" class="inline-flex items-center gap-2 bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors duration-200">
          <UserPlusIcon class="h-5 w-5"/>
          <span>Benutzer Eintragen</span>
        </button>
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

    <!-- Bearbeiten-Modal -->
    <div v-if="isEditModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" @click.self="closeModal">
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
    </div>

    <!-- Hinzufügen-Modal -->
    <div v-if="isAddModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" @click.self="closeModal">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6 sm:p-8">
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