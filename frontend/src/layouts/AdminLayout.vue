<script setup>
import { ref } from 'vue';
// Um die Icons zu nutzen
import { HomeIcon, UsersIcon, QuestionMarkCircleIcon, BellIcon, UserCircleIcon } from '@heroicons/vue/24/outline';
// Importiere die Ansichten, die im Layout angezeigt werden sollen
import AdminDashboardView from '../views/AdminDashboardView.vue';
import AdminUserManagement from '../views/AdminUserManagement.vue';
import AdminItemsView from '../views/AdminItemsView.vue';

// Dieser Zustand steuert, welche Ansicht (Dashboard oder Benutzer) gerade aktiv ist
const activeView = ref('dashboard');
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex">
    <aside class="w-64 bg-white p-6 flex-shrink-0">
      <h1 class="text-xl font-bold text-gray-800 mb-10">Wahr oder Watt-Admin</h1>
      <nav class="space-y-2">
        <button @click="activeView = 'dashboard'" :class="['w-full flex items-center gap-3 p-3 rounded-lg text-left', activeView === 'dashboard' ? 'bg-blue-500 text-white' : 'hover:bg-gray-100']">
          <HomeIcon class="h-6 w-6" />
          <span class="font-semibold">Dashboard</span>
        </button>
        <button @click="activeView = 'users'" :class="['w-full flex items-center gap-3 p-3 rounded-lg text-left', activeView === 'users' ? 'bg-blue-500 text-white' : 'hover:bg-gray-100']">
          <UsersIcon class="h-6 w-6" />
          <span class="font-semibold">Benutzer</span>
        </button>
        <button @click="activeView = 'items'" :class="['w-full flex items-center gap-3 p-3 rounded-lg text-left', activeView === 'items' ? 'bg-blue-500 text-white' : 'hover:bg-gray-100']">
          <QuestionMarkCircleIcon class="h-6 w-6" />
          <span class="font-semibold">Geräte und Eigenschaften</span>
        </button>
      </nav>
    </aside>

    <div class="flex-grow p-8">
      <header class="flex justify-between items-center mb-8">
        <h2 class="text-3xl font-bold text-gray-900">{{ activeView.charAt(0).toUpperCase() + activeView.slice(1) }}</h2>
        <div class="flex items-center gap-4">
          <div class="relative">
            <BellIcon class="h-6 w-6 text-gray-500" />
            <span class="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-xs text-white">2</span>
          </div>
          <div class="flex items-center gap-2">
            <UserCircleIcon class="h-8 w-8 text-gray-500" />
            <div>
              <div class="font-semibold">Peter Grünning</div>
              <div class="text-xs text-gray-500">Admin</div>
            </div>
          </div>
        </div>
      </header>

      <main>
        <AdminDashboardView v-if="activeView === 'dashboard'" />
        <AdminUserManagement v-if="activeView === 'users'" />
        <AdminItemsView v-if="activeView === 'items'" />
        <div v-if="activeView === 'items'" class="text-center text-gray-500 mt-20 p-8 bg-white rounded-lg shadow-sm">
          <h3 class="text-xl font-semibold">Ansicht: Geräte und Eigenschaften</h3>
          <p>Diese Sektion ist noch im Aufbau.</p>
        </div>
      </main>
    </div>
  </div>
</template>