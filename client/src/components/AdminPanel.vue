<template>
  <div class="min-h-screen bg-background">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-3xl font-bold text-foreground">Admin Panel</h1>
          <p class="text-muted-foreground mt-1">Manage operator approvals and access control</p>
        </div>
        <Button
          @click="logout"
          variant="destructive"
          class="shadow-sm"
        >
          <LogOut class="w-4 h-4 mr-2" />
          Logout
        </Button>
      </div>

      <!-- Stats Card -->
      <Card class="mb-8 rounded-xl border shadow-sm">
        <CardContent class="p-6">
          <div class="flex items-center gap-4">
            <div class="p-3 bg-primary/10 rounded-xl">
              <Users class="w-8 h-8 text-primary" />
            </div>
            <div>
              <p class="text-sm text-muted-foreground">Pending Approvals</p>
              <p class="text-3xl font-bold text-foreground">{{ pendingUsers.length }}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Pending Users Card -->
      <Card class="rounded-xl border shadow-sm">
        <CardHeader class="pb-3">
          <div class="flex items-center justify-between">
            <CardTitle class="text-xl font-semibold">Operator Requests</CardTitle>
            <Button
              @click="loadPendingUsers"
              variant="outline"
              size="sm"
              :disabled="loading"
            >
              <RefreshCcw :class="['w-4 h-4 mr-2', loading && 'animate-spin']" />
              Refresh
            </Button>
          </div>
          <p class="text-sm text-muted-foreground mt-1">
            Review and approve operator access requests
          </p>
        </CardHeader>

        <CardContent class="pt-0">
          <!-- Loading State -->
          <div v-if="loading" class="text-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
            <p class="mt-4 text-muted-foreground">Loading requests...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="pendingUsers.length === 0" class="text-center py-12">
            <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-muted">
              <CheckCircle class="h-8 w-8 text-muted-foreground" />
            </div>
            <h3 class="text-lg font-semibold text-foreground mb-1">All caught up!</h3>
            <p class="text-muted-foreground">No pending approval requests at the moment</p>
          </div>

          <!-- Users List -->
          <div v-else class="space-y-3">
            <div
              v-for="user in pendingUsers"
              :key="user.id"
              class="border border-border rounded-xl p-5 hover:bg-accent/50 transition-colors"
            >
              <div class="flex items-center justify-between gap-4">
                <div class="flex items-center gap-4 flex-1 min-w-0">
                  <!-- Avatar -->
                  <div v-if="user.picture" class="flex-shrink-0">
                    <img :src="user.picture" alt="" class="h-14 w-14 rounded-full ring-2 ring-border" />
                  </div>
                  <div v-else class="flex-shrink-0">
                    <div class="h-14 w-14 rounded-full bg-primary flex items-center justify-center text-primary-foreground font-semibold text-xl ring-2 ring-border">
                      {{ user.name.charAt(0).toUpperCase() }}
                    </div>
                  </div>
                  
                  <!-- User Info -->
                  <div class="flex-1 min-w-0">
                    <h3 class="text-base font-semibold text-foreground truncate">
                      {{ user.name }}
                    </h3>
                    <p class="text-sm text-muted-foreground truncate">
                      {{ user.email }}
                    </p>
                    <div class="flex items-center gap-2 mt-1">
                      <span class="inline-flex items-center rounded-md bg-secondary px-2 py-1 text-xs font-medium text-secondary-foreground ring-1 ring-inset ring-border">
                        {{ user.provider === 'GOOGLE' ? 'Google OAuth' : 'Local' }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="flex gap-2 flex-shrink-0">
                  <Button
                    @click="approveUser(user.id)"
                    :disabled="processing"
                    size="default"
                    class="bg-green-600 hover:bg-green-700 text-white shadow-sm"
                  >
                    <Check class="h-4 w-4 mr-2" />
                    Approve
                  </Button>

                  <Button
                    @click="rejectUser(user.id)"
                    :disabled="processing"
                    variant="destructive"
                    size="default"
                    class="shadow-sm"
                  >
                    <X class="h-4 w-4 mr-2" />
                    Reject
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Toast Message -->
      <div
        v-if="message"
        :class="[
          'fixed bottom-6 right-6 p-4 rounded-xl shadow-lg border animate-slide-up max-w-md',
          messageType === 'success' 
            ? 'bg-green-50 dark:bg-green-950 text-green-800 dark:text-green-200 border-green-200 dark:border-green-800' 
            : 'bg-red-50 dark:bg-red-950 text-red-800 dark:text-red-200 border-red-200 dark:border-red-800'
        ]"
      >
        <div class="flex items-center gap-3">
          <CheckCircle v-if="messageType === 'success'" class="w-5 h-5 flex-shrink-0" />
          <XCircle v-else class="w-5 h-5 flex-shrink-0" />
          <span class="font-medium">{{ message }}</span>
          <button @click="message = ''" class="ml-auto p-1 hover:bg-black/5 dark:hover:bg-white/10 rounded">
            <X class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Card, CardContent, CardHeader, CardTitle } from './ui/card'
import { Button } from './ui/button'
import { LogOut, Users, RefreshCcw, Check, X, CheckCircle, XCircle } from 'lucide-vue-next'
import api from '../services/api'
import { useAuth } from '../services/auth'

const router = useRouter()
const auth = useAuth()

const pendingUsers = ref([])
const loading = ref(true)
const processing = ref(false)
const message = ref('')
const messageType = ref('success')

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const loadPendingUsers = async () => {
  loading.value = true
  try {
    console.log('Loading pending users from:', `${API_BASE_URL}/admin/pending-users`)
    console.log('Current user from localStorage:', localStorage.getItem('user'))
    
    const response = await fetch(`${API_BASE_URL}/admin/pending-users`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      }
    })

    console.log('Response status:', response.status)
    console.log('Response headers:', response.headers)

    if (response.status === 403) {
      // Not authorized as admin
      console.error('403 Forbidden - Not authorized as admin')
      showMessage('Access denied. Admin privileges required.', 'error')
      setTimeout(() => router.push('/login'), 2000)
      return
    }

    if (!response.ok) {
      const errorText = await response.text()
      console.error('Response not OK:', response.status, errorText)
      throw new Error(`Failed to load pending users: ${response.status} ${errorText}`)
    }

    const data = await response.json()
    console.log('Pending users loaded:', data)
    pendingUsers.value = data
  } catch (error) {
    console.error('Error loading pending users:', error)
    showMessage(`Failed to load pending users: ${error.message}`, 'error')
  } finally {
    loading.value = false
  }
}

const approveUser = async (userId) => {
  if (processing.value) return

  processing.value = true
  try {
    const response = await fetch(`${API_BASE_URL}/admin/approve-user/${userId}`, {
      method: 'POST',
      credentials: 'include'
    })

    const data = await response.json()

    if (data.success) {
      showMessage(`User approved successfully`, 'success')
      await loadPendingUsers()
    } else {
      showMessage(data.message || 'Failed to approve user', 'error')
    }
  } catch (error) {
    console.error('Error approving user:', error)
    showMessage('Failed to approve user', 'error')
  } finally {
    processing.value = false
  }
}

const rejectUser = async (userId) => {
  if (processing.value) return
  
  if (!confirm('Are you sure you want to reject this user? This will delete their account.')) {
    return
  }

  processing.value = true
  try {
    const response = await fetch(`${API_BASE_URL}/admin/reject-user/${userId}`, {
      method: 'DELETE',
      credentials: 'include'
    })

    const data = await response.json()

    if (data.success) {
      showMessage('User rejected and deleted', 'success')
      await loadPendingUsers()
    } else {
      showMessage(data.message || 'Failed to reject user', 'error')
    }
  } catch (error) {
    console.error('Error rejecting user:', error)
    showMessage('Failed to reject user', 'error')
  } finally {
    processing.value = false
  }
}

const logout = async () => {
  try {
    await auth.logout()
    // Clear any local state
    pendingUsers.value = []
    // Redirect to login page
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
    // Even if logout API fails, clear local state and redirect
    pendingUsers.value = []
    router.push('/login')
  }
}

const showMessage = (msg, type = 'success') => {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 5000)
}

onMounted(() => {
  loadPendingUsers()
})
</script>
