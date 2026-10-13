// API Service - Centralized HTTP request handling
// Created for backend-frontend integration

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
let csrfInitialized = false

const getCookieValue = (name) => {
  const cookies = document.cookie ? document.cookie.split('; ') : []
  const prefix = `${name}=`
  const found = cookies.find(cookie => cookie.startsWith(prefix))
  return found ? decodeURIComponent(found.substring(prefix.length)) : null
}

const ensureCsrfCookie = async () => {
  if (csrfInitialized || typeof window === 'undefined') {
    return
  }

  try {
    await fetch(`${API_BASE_URL}/auth/csrf-token`, {
      method: 'GET',
      credentials: 'include'
    })
  } finally {
    csrfInitialized = true
  }
}

/**
 * Generic API request handler with error handling
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {Object} options - Fetch options (method, headers, body, etc.)
 * @returns {Promise} - Response data or throws error
 */
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`
  const method = (options.method || 'GET').toUpperCase()
  const isUnsafeMethod = !['GET', 'HEAD', 'OPTIONS'].includes(method)

  if (isUnsafeMethod) {
    await ensureCsrfCookie()
  }
  
  // Add timeout support - increased to 60 seconds for route creation (backend can be slow)
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), 60000) // 60 second timeout
  const csrfToken = getCookieValue('XSRF-TOKEN')
  
  const defaultOptions = {
    credentials: 'include', // Include cookies for session management
    headers: {
      'Content-Type': 'application/json',
      ...(isUnsafeMethod && csrfToken ? { 'X-XSRF-TOKEN': csrfToken } : {}),
      ...options.headers
    },
    signal: controller.signal,
    ...options
  }

  try {
    const response = await fetch(url, defaultOptions)
    clearTimeout(timeoutId)
    
    if (!response.ok) {
      const contentType = response.headers.get('content-type')
      let errorMessage = response.statusText

      // Read body as text first to avoid double-consume bug
      const bodyText = await response.text()

      if (contentType && contentType.includes('application/json') && bodyText) {
        try {
          const errorData = JSON.parse(bodyText)
          errorMessage = errorData.message || errorData.error || errorMessage
        } catch {
          errorMessage = bodyText || errorMessage
        }
      } else if (bodyText) {
        errorMessage = bodyText
      }

      // Handle specific error cases with user-friendly messages
      if (errorMessage.includes('foreign key constraint') && errorMessage.includes('shipment')) {
        throw new Error('Cannot delete route: This route is currently assigned to one or more shipments. Please reassign or delete those shipments first.')
      }

      if (errorMessage.includes('foreign key constraint') && errorMessage.includes('cargo')) {
        throw new Error('Cannot delete cargo: This cargo is currently assigned to a shipment or route. Please remove those assignments first.')
      }

      if (errorMessage.includes('foreign key constraint') && errorMessage.includes('vendor')) {
        throw new Error('Cannot delete vendor: This vendor is currently assigned to active shipments or routes. Please reassign those first.')
      }

      throw new Error(errorMessage)
    }
    
    // Handle empty responses (like 204 No Content)
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      return await response.json()
    }
    
    return null
  } catch (error) {
    clearTimeout(timeoutId)
    
    // Enhanced error messages for common connection issues
    if (error.name === 'AbortError') {
      console.error(`API Request timeout: ${endpoint}`)
      throw new Error('Request timeout - backend server may be slow or unavailable')
    }
    
    if (error.message.includes('Failed to fetch') || error.code === 'ECONNREFUSED') {
      console.error(`API Connection failed: ${endpoint}`)
      throw new Error('Backend server is not running on port 8080. Please start the server or use demo mode.')
    }
    
    console.error(`API Request failed: ${endpoint}`, error)
    throw error
  }
}

// Cargo API endpoints
export const cargoApi = {
  getAll: () => apiRequest('/cargo'),
  getById: (id) => apiRequest(`/cargo/${id}`),
  create: (data) => apiRequest('/cargo', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/cargo/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  remove: (id) => apiRequest(`/cargo/${id}`, {
    method: 'DELETE'
  })
}

// Vendor API endpoints
export const vendorApi = {
  getAll: () => apiRequest('/vendors'),
  getById: (id) => apiRequest(`/vendors/${id}`),
  create: (data) => apiRequest('/vendors', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/vendors/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/vendors/${id}`, {
    method: 'DELETE'
  })
}

// Route API endpoints
export const routeApi = {
  getAll: () => apiRequest('/routes'),
  getById: (id) => apiRequest(`/routes/${id}`),
  create: (data) => apiRequest('/routes', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/routes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/routes/${id}`, {
    method: 'DELETE'
  })
}

// Shipment API endpoints
export const shipmentApi = {
  getAll: () => apiRequest('/shipments'),
  getById: (id) => apiRequest(`/shipments/${id}`),
  create: (data) => apiRequest('/shipments', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/shipments/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/shipments/${id}`, {
    method: 'DELETE'
  })
}

// Delivery API endpoints
export const deliveryApi = {
  getAll: () => apiRequest('/deliveries'),
  getById: (id) => apiRequest(`/deliveries/${id}`),
  create: (data) => apiRequest('/deliveries', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/deliveries/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/deliveries/${id}`, {
    method: 'DELETE'
  })
}

// Auth API endpoints
export const authApi = {
  login: (credentials) => apiRequest('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials)
  }),
  signup: (userData) => apiRequest('/auth/signup', {
    method: 'POST',
    body: JSON.stringify(userData)
  }),
  logout: () => apiRequest('/auth/logout', {
    method: 'POST'
  }),
  getCurrentUser: () => apiRequest('/auth/user'),
  googleLogin: () => {
    // Redirect to backend Google OAuth
  window.location.href = API_BASE_URL.replace(/\/api$/, '') + '/oauth2/authorization/google'
  }
}

// Health check endpoint
export const dashboardApi = {
  getMetrics: () => apiRequest('/dashboard/metrics'),
  getRecentActivities: () => apiRequest('/dashboard/recent-activities')
}

export const healthApi = {
  check: () => apiRequest('/health').catch(() => false)
}

export const notificationApi = {
  sendEmail: (data) => apiRequest('/notifications/email', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export default {
  authApi,
  cargoApi,
  vendorApi,
  routeApi,
  shipmentApi,
  deliveryApi,
  dashboardApi,
  healthApi,
  notificationApi
}
