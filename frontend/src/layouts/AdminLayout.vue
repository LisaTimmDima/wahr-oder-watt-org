<script setup>
import { ref } from 'vue';
import { HomeIcon, UsersIcon, CubeIcon, BellIcon, UserCircleIcon, PlayIcon, XMarkIcon, ArrowLeftOnRectangleIcon } from '@heroicons/vue/24/outline';
import AdminDashboardView from '../views/AdminDashboardView.vue';
import AdminUserManagement from '../views/AdminUserManagement.vue';
import AdminItemsView from '../views/AdminItemsView.vue';

const emit = defineEmits(['go-to-lobby']);

const activeView = ref('dashboard');
const showNotifications = ref(false);

const navigation = [
  { name: 'Dashboard', view: 'dashboard', icon: HomeIcon },
  { name: 'Benutzer verwalten', view: 'users', icon: UsersIcon },
  { name: 'Items verwalten', view: 'items', icon: CubeIcon },
];

const notifications = ref([
  { id: 1, text: 'Neuer Benutzer "Max Mustermann" wartet auf Freischaltung.', time: 'vor 5 Minuten' },
  { id: 2, text: 'Item "Grafikkarte" wurde bearbeitet.', time: 'vor 1 Stunde' },
]);

function handleAdminPlay() {
  emit('go-to-lobby');
}

function navigate(view) {
  activeView.value = view;
}

function logout() {
  localStorage.removeItem('jwt');
  window.location.href = '/login';
}

</script>

<template>
  <div class="bg-gray-100 min-h-screen flex font-sans">
    <aside class="w-64 bg-white p-6 flex flex-col flex-shrink-0 shadow-lg">
      <div>
        <div class="flex items-center gap-3 mb-10">
          <img src="../assets/logo.svg" alt="Logo" class="h-24 w-auto">
          <h1 class="text-xl font-bold text-gray-800">Admin</h1>
        </div>
        <nav class="space-y-2">
          <button v-for="item in navigation" :key="item.name" @click="activeView = item.view" :class="['w-full flex items-center gap-3 p-3 rounded-lg text-left transition-colors', activeView === item.view ? 'bg-blue-600 text-white shadow-md' : 'text-gray-600 hover:bg-gray-200']">
            <component :is="item.icon" class="h-6 w-6" />
            <span class="font-semibold">{{ item.name }}</span>
          </button>
        </nav>
      </div>
      <div class="mt-auto pt-6 border-t border-gray-200">
        <button @click="logout" class="w-full flex items-center gap-3 p-3 rounded-lg text-left text-red-500 hover:bg-red-50 transition-colors">
          <ArrowLeftOnRectangleIcon class="h-6 w-6" />
          <span class="font-semibold">Abmelden</span>
        </button>
      </div>
    </aside>

    <div class="flex-grow flex flex-col">
      <header class="bg-white shadow-md p-4 flex justify-end items-center z-20">
        <div class="flex items-center gap-6">
           <button @click="handleAdminPlay" class="flex items-center gap-2 text-gray-600 hover:text-blue-600 font-semibold transition-colors">
            <PlayIcon class="h-6 w-6" />
            <span>Spielen</span>
          </button>
          <div class="relative">
            <button @click="showNotifications = !showNotifications" class="relative">
              <BellIcon class="h-7 w-7 text-gray-500" />
              <span v-if="notifications.length > 0" class="absolute top-0 right-0 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-xs text-white">{{ notifications.length }}</span>
            </button>
            <!-- Notifications Popup -->
            <div v-if="showNotifications" class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl overflow-hidden border border-gray-200">
              <div class="p-4 flex justify-between items-center border-b">
                <h3 class="font-bold text-gray-800">Benachrichtigungen</h3>
                <button @click="showNotifications = false"><XMarkIcon class="h-5 w-5 text-gray-500"/></button>
              </div>
              <ul v-if="notifications.length > 0" class="divide-y divide-gray-200">
                <li v-for="notification in notifications" :key="notification.id" class="p-4 hover:bg-gray-50">
                  <p class="text-sm text-gray-700">{{ notification.text }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ notification.time }}</p>
                </li>
              </ul>
              <div v-else class="p-4 text-center text-gray-500">
                Keine neuen Benachrichtigungen.
              </div>
              <div class="p-2 bg-gray-50 text-center border-t">
                <a href="#" class="text-sm font-semibold text-blue-600 hover:underline">Alle ansehen</a>
              </div>
            </div>
          </div>
          <div class="flex items-center gap-3">
            <UserCircleIcon class="h-10 w-10 text-gray-500" />
            <div>
              <div class="font-semibold">Peter Grünning</div>
              <div class="text-xs text-gray-500">Administrator</div>
            </div>
          </div>
        </div>
      </header>

      <main class="flex-grow p-6">
        <AdminDashboardView v-if="activeView === 'dashboard'" @admin-play="handleAdminPlay" @navigate="navigate" />
        <AdminUserManagement v-else-if="activeView === 'users'" />
        <AdminItemsView v-else-if="activeView === 'items'" />
      </main>
    </div>
  </div>
</template>
