<script setup>
import { ref } from 'vue';
import { UsersIcon, CogIcon, LockClosedIcon } from '@heroicons/vue/24/outline';

// Mock data for the dashboard overview
const usersToApprove = ref(1);
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold text-gray-800 mb-6">Schnellzugriff</h2>
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
      <button class="bg-white p-6 rounded-xl shadow-sm text-left hover:bg-gray-50 transition-colors">
        <div class="flex justify-between items-center">
          <span class="text-lg font-semibold text-gray-700">Spielen</span>
          <UsersIcon class="h-8 w-8 text-gray-400" />
        </div>
      </button>
    </div>

    <h2 class="text-2xl font-bold text-gray-800 mt-12 mb-6">Übersicht</h2>
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
      <div class="bg-gray-800 text-white p-6 rounded-xl shadow-lg">
        <div class="flex justify-between items-center">
          <span class="font-semibold">Benutzer Freischalten</span>
          <LockClosedIcon class="h-6 w-6 text-gray-400" />
        </div>
        <div class="text-4xl font-bold mt-2">{{ usersToApprove }}</div>
      </div>
    </div>
  </div>
</template>