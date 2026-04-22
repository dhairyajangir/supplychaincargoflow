<template>
  <div class="p-4 bg-gray-100 dark:bg-gray-800 rounded-lg">
    <h3 class="text-lg font-semibold mb-2">Admin Debug Info</h3>
    <div class="space-y-2 text-sm">
      <div>
        <strong>LocalStorage User:</strong>
        <pre class="bg-white dark:bg-gray-900 p-2 rounded mt-1 overflow-auto">{{ localStorageUser }}</pre>
      </div>
      <div>
        <strong>Session Check:</strong>
        <button @click="checkSession" class="ml-2 px-3 py-1 bg-blue-600 text-white rounded">
          Test Session
        </button>
      </div>
      <div v-if="sessionResult">
        <strong>Session Result:</strong>
        <pre class="bg-white dark:bg-gray-900 p-2 rounded mt-1 overflow-auto">{{ sessionResult }}</pre>
      </div>
      <div>
        <strong>Cookies:</strong>
        <pre class="bg-white dark:bg-gray-900 p-2 rounded mt-1 overflow-auto">{{ cookies || 'No cookies found' }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const localStorageUser = ref(null)
const sessionResult = ref(null)
const cookies = ref('')
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

onMounted(() => {
  try {
    const userStr = localStorage.getItem('user')
    localStorageUser.value = userStr ? JSON.parse(userStr) : null
    cookies.value = document.cookie || 'No cookies found'
  } catch (e) {
    localStorageUser.value = { error: e.message }
  }
})

const checkSession = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/pending-users`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    
    const result = {
      status: response.status,
      statusText: response.statusText,
      ok: response.ok,
    }
    
    if (response.ok) {
      result.data = await response.json()
    } else {
      result.error = await response.text()
    }
    
    sessionResult.value = result
  } catch (error) {
    sessionResult.value = { error: error.message }
  }
}
</script>
