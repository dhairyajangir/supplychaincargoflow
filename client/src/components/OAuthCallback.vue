<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl p-8 max-w-md w-full text-center">
      <div v-if="loading" class="space-y-4">
        <svg class="animate-spin h-12 w-12 text-blue-600 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <h2 class="text-2xl font-bold text-gray-900">Completing Sign In</h2>
        <p class="text-gray-600">Please wait while we authenticate your account...</p>
      </div>

      <div v-else-if="error" class="space-y-4">
        <div class="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center mx-auto">
          <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-900">Authentication Failed</h2>
        <p class="text-gray-600">{{ error }}</p>
        <button
          @click="router.push('/login')"
          class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Back to Login
        </button>
      </div>

      <div v-else class="space-y-4">
        <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto">
          <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-900">Success!</h2>
        <p class="text-gray-600">Redirecting to dashboard...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../services/auth'

const router = useRouter()
const auth = useAuth()

const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    // Give the backend a moment to create the session
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // Fetch the current user from the backend
    const user = await auth.getCurrentUser()
    
    if (user) {
      loading.value = false
      
      // Redirect to dashboard after a brief moment
      setTimeout(() => {
        router.push('/dashboard')
      }, 1000)
    } else {
      throw new Error('Failed to retrieve user information')
    }
  } catch (err) {
    console.error('OAuth callback error:', err)
    loading.value = false
    error.value = 'Authentication failed. Please try again.'
    
    // Redirect to login after 3 seconds
    setTimeout(() => {
      router.push('/login')
    }, 3000)
  }
})
</script>
