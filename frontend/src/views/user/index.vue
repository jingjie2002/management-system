<template>
  <div>
    <UserManagement v-if="isAdmin" />
    <Profile v-else />
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import Profile from './Profile.vue'
import UserManagement from './UserManagement.vue'

const roleKeys = ref<string[]>(JSON.parse(localStorage.getItem('roleKeys') || '[]'))
const isAdmin = computed(() => roleKeys.value.includes('admin'))
</script>
