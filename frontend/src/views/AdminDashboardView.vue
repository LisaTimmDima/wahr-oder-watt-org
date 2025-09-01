<script setup>
import { ref } from 'vue';
import { UsersIcon, CubeIcon, BellIcon } from '@heroicons/vue/24/outline';


const stats = ref([
  { name: 'Benutzer zur Freischaltung', value: 1, icon: BellIcon, color: 'bg-red-500' },
  { name: 'Gesamte Benutzer', value: 12, icon: UsersIcon, color: 'bg-blue-500' },
  { name: 'Gesamte Geräte', value: 2, icon: CubeIcon, color: 'bg-yellow-500' },
]);
</script>

<template>
  <div class="p-4 sm:p-6 bg-gray-100 min-h-full">
    <h1 class="text-3xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>

    <!-- Stats Overview -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
      <div v-for="stat in stats" :key="stat.name" :class="['text-white p-6 rounded-2xl shadow-lg', stat.color]">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-4xl font-extrabold">{{ stat.value }}</p>
            <p class="text-lg font-semibold">{{ stat.name }}</p>
          </div>
          <component :is="stat.icon" class="h-10 w-10 opacity-80" />
        </div>
      </div>
    </div>

  </div>
</template>
