// Authentication Service - Manages user authentication state
import { ref, computed } from 'vue'
import { authApi } from './api'

// Reactive state for authentication
const user = ref(null)
const isAuthenticated = ref(false)
const loading = ref(false)
const error = ref(null)

/**
 * Initialize auth state from localStorage
 */
const initializeAuth = () => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      user.value = JSON.parse(storedUser)
      isAuthenticated.value = true
    } catch (e) {
      console.error('Failed to parse stored user:', e)
      localStorage.removeItem('user')
    }
  }
}

/**
 * Login with email and password
 * @param {Object} credentials - Email and password
 * @returns {Promise<Object>} - Response with success status
 */
const login = async (credentials) => {
  loading.value = true
  error.value = null
  
  try {
    const response = await authApi.login(credentials)
    
    if (response.success) {
      user.value = response.user
      isAuthenticated.value = true
      
      // Store user in localStorage
      localStorage.setItem('user', JSON.stringify(response.user))
      
      return { success: true, user: response.user }
    } else {
      error.value = response.message || 'Login failed'
      return { success: false, message: error.value, isPending: response.isPending }
    }
  } catch (err) {
    error.value = err.message || 'An error occurred during login'
    return { success: false, message: error.value }
  } finally {
    loading.value = false
  }
}

/**
 * Signup new user
 * @param {Object} userData - Name, email, and password
 * @returns {Promise<Object>} - Response with success status
 */
const signup = async (userData) => {
  loading.value = true
  error.value = null
  
  try {
    const response = await authApi.signup(userData)
    
    if (response.success) {
      return { success: true, message: response.message, isPending: response.isPending }
    } else {
      error.value = response.message || 'Signup failed'
      return { success: false, message: error.value }
    }
  } catch (err) {
    error.value = err.message || 'An error occurred during signup'
    return { success: false, message: error.value }
  } finally {
    loading.value = false
  }
}

/**
 * Logout current user
 * @returns {Promise<void>}
 */
const logout = async () => {
  loading.value = true
  error.value = null
  
  try {
    await authApi.logout()
  } catch (err) {
    console.error('Logout error:', err)
  } finally {
    // Clear state regardless of API response
    user.value = null
    isAuthenticated.value = false
    localStorage.removeItem('user')
    loading.value = false
  }
}

/**
 * Get current user from backend
 * Useful for verifying session on app load
 * @returns {Promise<Object|null>} - User object or null
 */
const getCurrentUser = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await authApi.getCurrentUser()
    
    if (response.success && response.user) {
      user.value = response.user
      isAuthenticated.value = true
      
      // Update localStorage
      localStorage.setItem('user', JSON.stringify(response.user))
      
      return response.user
    } else {
      // User not authenticated, clear state
      user.value = null
      isAuthenticated.value = false
      localStorage.removeItem('user')
      return null
    }
  } catch (err) {
    console.error('Failed to get current user:', err)
    user.value = null
    isAuthenticated.value = false
    localStorage.removeItem('user')
    return null
  } finally {
    loading.value = false
  }
}

/**
 * Initiate Google OAuth login
 */
const loginWithGoogle = () => {
  authApi.googleLogin()
}

/**
 * Get user initials for avatar
 */
const userInitials = computed(() => {
  if (!user.value || !user.value.name) return 'U'
  
  const names = user.value.name.split(' ')
  if (names.length >= 2) {
    return (names[0][0] + names[1][0]).toUpperCase()
  }
  return names[0].substring(0, 2).toUpperCase()
})

/**
 * Get user display name
 */
const userName = computed(() => {
  return user.value?.name || 'User'
})

/**
 * Get user email
 */
const userEmail = computed(() => {
  return user.value?.email || ''
})

/**
 * Get user role
 */
const userRole = computed(() => {
  return user.value?.role || 'User'
})

/**
 * Get user picture URL
 */
const userPicture = computed(() => {
  return user.value?.picture || null
})

// Initialize auth on module load
initializeAuth()

// Export auth service
export const useAuth = () => {
  return {
    // State
    user: computed(() => user.value),
    isAuthenticated: computed(() => isAuthenticated.value),
    loading: computed(() => loading.value),
    error: computed(() => error.value),
    
    // Computed
    userInitials,
    userName,
    userEmail,
    userRole,
    userPicture,
    
    // Methods
    login,
    signup,
    logout,
    getCurrentUser,
    loginWithGoogle
  }
}
