<template>
  <div class="container mx-auto px-4 py-8 max-w-3xl">
    <div class="space-y-6">
      <!-- Back Button -->
      <div>
        <Button @click="$router.push('/routes')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Routes
        </Button>
      </div>

      <!-- Header Section -->
      <div>
        <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">
          {{ isEditMode ? 'Edit Route' : 'Create New Route' }}
        </h1>
        <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">
          {{ isEditMode ? 'Update route information' : 'Set up a new shipping route' }}
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-32">
        <Loader2 class="h-6 w-6 animate-spin dark:text-sidebar-foreground" />
        <span class="ml-2 dark:text-sidebar-foreground">{{ isEditMode ? 'Loading route...' : 'Saving route...' }}</span>
      </div>

      <!-- Form Card -->
      <Card v-else>
        <CardHeader>
          <CardTitle>Route Information</CardTitle>
          <CardDescription>
            Fill in the details for the shipping route
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Origin and Destination -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="originPort">
                  Origin Port <span class="text-red-500">*</span>
                </label>
                <LocationAutocomplete
                  input-id="originPort"
                  v-model="form.originPort"
                  placeholder="e.g., Jawaharlal Nehru Port"
                  required
                />
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="destinationPort">
                  Destination Port <span class="text-red-500">*</span>
                </label>
                <LocationAutocomplete
                  input-id="destinationPort"
                  v-model="form.destinationPort"
                  placeholder="e.g., Chennai Port"
                  required
                />
              </div>
            </div>

            <!-- Duration and Distance -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="duration">
                  Duration (days) <span class="text-red-500">*</span>
                </label>
                <Input
                  id="duration"
                  v-model.number="form.duration"
                  type="number"
                  min="1"
                  placeholder="0"
                  required
                />
                <p v-if="autoEstimationState.isCalculating" class="text-xs text-gray-500">
                  Calculating optimal duration…
                </p>
                <p v-else-if="autoEstimationState.error" class="text-xs text-red-500">
                  {{ autoEstimationState.error }}
                </p>
                <p v-else-if="autoEstimationSummary" class="text-xs text-gray-500">
                  {{ autoEstimationSummary }}
                </p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="distance">
                  Distance (km)
                </label>
                <Input
                  id="distance"
                  v-model.number="form.distance"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="0"
                />
              </div>
            </div>

            <!-- Transportation Mode and Status -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="transportationMode">
                  Transport Mode
                </label>
                <select
                  id="transportationMode"
                  v-model="form.transportationMode"
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                >
                  <option value="OCEAN">🚢 Ocean Transport</option>
                  <option value="AIR">✈️ Air Transport</option>
                  <option value="ROAD">🚛 Road Transport</option>
                  <option value="RAIL">🚂 Rail Transport</option>
                </select>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="status">
                  Status <span class="text-red-500">*</span>
                </label>
                <select
                  id="status"
                  v-model="form.status"
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                  required
                >
                  <option value="Active">Active</option>
                  <option value="Delayed">Delayed</option>
                  <option value="Closed">Closed</option>
                </select>
              </div>
            </div>

            <!-- Cost -->
            <div class="space-y-2">
              <label class="text-sm font-medium dark:text-sidebar-foreground" for="cost">
                Cost ($)
              </label>
              <Input
                id="cost"
                v-model.number="form.cost"
                type="number"
                min="0"
                step="0.01"
                placeholder="0.00"
              />
            </div>

            <!-- Loading Message -->
            <div v-if="isSubmitting" class="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded-md flex items-center">
              <Loader2 class="h-5 w-5 mr-2 animate-spin" />
              <span>Saving route... This may take up to a minute. Please wait.</span>
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md flex items-center">
              <AlertCircle class="h-5 w-5 mr-2" />
              <span>{{ errorMessage }}</span>
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-md flex items-center">
              <CheckCircle class="h-5 w-5 mr-2" />
              <span>{{ successMessage }}</span>
            </div>

            <!-- Form Actions -->
            <div class="flex items-center justify-end space-x-4 pt-4 border-t">
              <Button type="button" variant="outline" @click="handleCancel" :disabled="isSubmitting">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Loader2 v-if="isSubmitting" class="mr-2 h-4 w-4 animate-spin" />
                {{ isEditMode ? 'Update Route' : 'Create Route' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import LocationAutocomplete from '@/components/shared/LocationAutocomplete.vue'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ArrowLeft, Loader2, AlertCircle, CheckCircle } from 'lucide-vue-next'
import { routeApi } from '@/services/api'
import { calculateDurationDetails } from '@/services/logisticsEstimator'

const router = useRouter()
const route = useRoute()

const isEditMode = ref(false)
const isLoading = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref(null)
const successMessage = ref(null)

const autoEstimationState = reactive({
  isCalculating: false,
  error: null,
  distanceSource: null,
  totalDistance: null,
  durationDays: null,
  lastUpdated: null
})

const autoEstimationSummary = computed(() => {
  if (autoEstimationState.error || !autoEstimationState.lastUpdated) {
    return ''
  }

  const sourceLabel = autoEstimationState.distanceSource === 'provided'
    ? 'provided distance'
    : 'coordinate lookup'

  const distanceLabel = typeof autoEstimationState.totalDistance === 'number'
    ? `${Math.round(autoEstimationState.totalDistance).toLocaleString()} km`
    : ''

  const durationLabel = typeof autoEstimationState.durationDays === 'number'
    ? `${autoEstimationState.durationDays} day${autoEstimationState.durationDays === 1 ? '' : 's'}`
    : ''

  const parts = [`Auto-estimated via ${sourceLabel}`]
  if (distanceLabel) parts.push(distanceLabel)
  if (durationLabel) parts.push(durationLabel)
  return parts.join(' · ')
})

const form = ref({
  originPort: '',
  destinationPort: '',
  duration: null,
  distance: null,
  transportationMode: 'OCEAN',
  status: 'Active',
  cost: null
})

const loadRoute = async () => {
  const routeId = Number(route.params.id)
  if (!routeId) return

  isLoading.value = true
  errorMessage.value = null

  try {
    const data = await routeApi.getById(routeId)
    form.value = {
      originPort: data.originPort || '',
      destinationPort: data.destinationPort || '',
      duration: data.duration || null,
      distance: data.distance || null,
      transportationMode: data.transportationMode || 'OCEAN',
      status: data.status || 'Active',
      cost: data.cost || null
    }
    scheduleRouteEstimation()
  } catch (err) {
    errorMessage.value = err.message || 'Failed to load route'
    console.error('Error loading route:', err)
  } finally {
    isLoading.value = false
  }
}

const handleSubmit = async () => {
  errorMessage.value = null
  successMessage.value = null
  isSubmitting.value = true

  try {
    // Prepare route data matching backend model
    const routeData = {
      originPort: form.value.originPort,
      destinationPort: form.value.destinationPort,
      duration: Number(form.value.duration) || 0,
      distance: Number(form.value.distance) || 0,
      status: form.value.status || 'Active',
      transportationMode: form.value.transportationMode || 'OCEAN',
      cost: Number(form.value.cost) || 0
    }

    // Validate required fields
    if (!routeData.originPort || !routeData.destinationPort) {
      errorMessage.value = 'Origin Port and Destination Port are required'
      return
    }

    if (!routeData.duration || routeData.duration < 1) {
      errorMessage.value = 'Duration must be at least 1 day'
      return
    }

    console.log('Sending route data:', routeData)

    if (!isEditMode.value) {
      try {
        const existingRoutes = await routeApi.getAll()
        const duplicate = existingRoutes?.find(r =>
          r.originPort?.toLowerCase() === routeData.originPort.toLowerCase() &&
          r.destinationPort?.toLowerCase() === routeData.destinationPort.toLowerCase()
        )

        if (duplicate) {
          errorMessage.value = `A route from ${routeData.originPort} to ${routeData.destinationPort} already exists.`
          return
        }
      } catch (checkError) {
        console.warn('Duplicate check failed, proceeding with create:', checkError)
      }
    }

    let savedRoute
    try {
      if (isEditMode.value) {
        const routeId = Number(route.params.id)
        savedRoute = await routeApi.update(routeId, routeData)
        successMessage.value = 'Route updated successfully!'
      } else {
        savedRoute = await routeApi.create(routeData)
        successMessage.value = 'Route created successfully!'
      }

      console.log('Route saved:', savedRoute)

      // Dispatch event for real-time updates with details
      window.dispatchEvent(new CustomEvent('routes-updated', {
        detail: {
          action: isEditMode.value ? 'updated' : 'created',
          route: {
            routeId: savedRoute?.routeId,
            originPort: routeData.originPort,
            destinationPort: routeData.destinationPort,
            transportationMode: routeData.transportationMode
          }
        }
      }))

      // Redirect after short delay
      setTimeout(() => {
        router.push('/routes')
      }, 1500)
    } catch (apiError) {
      // If we get a timeout but the route might have been created,
      // verify by checking if we can load the routes list
      if (apiError.message && apiError.message.includes('timeout')) {
        console.warn('Timeout occurred, verifying route creation...')
        
        try {
          // Try to load routes to verify if creation was successful
          const allRoutes = await routeApi.getAll()
          
          // Check if a route with our data exists (created in last 10 seconds)
          const justCreated = allRoutes.find(r => 
            r.originPort === routeData.originPort && 
            r.destinationPort === routeData.destinationPort &&
            new Date(r.createdAt) > new Date(Date.now() - 10000)
          )
          
          if (justCreated) {
            console.log('Route was created successfully despite timeout:', justCreated)
            successMessage.value = 'Route created successfully!'
            
            // Dispatch event for real-time updates
            window.dispatchEvent(new CustomEvent('routes-updated'))
            
            // Redirect after short delay
            setTimeout(() => {
              router.push('/routes')
            }, 1500)
            return // Exit early, don't show error
          }
        } catch (verifyError) {
          console.error('Could not verify route creation:', verifyError)
        }
      }
      
      // If we couldn't verify or it wasn't a timeout, throw the error
      throw apiError
    }

  } catch (err) {
    console.error('Error saving route:', err)
    if (err.message?.includes('duplicate key value')) {
      errorMessage.value = `A route from ${form.value.originPort} to ${form.value.destinationPort} already exists.`
    } else {
      errorMessage.value = err.message || 'Failed to save route. Please try again.'
    }
  } finally {
    isSubmitting.value = false
  }
}

const handleCancel = () => {
  if (confirm('Are you sure you want to cancel? Any unsaved changes will be lost.')) {
    router.push('/routes')
  }
}

onMounted(() => {
  isEditMode.value = route.path.includes('/edit')
  if (isEditMode.value) {
    loadRoute()
  }
  scheduleRouteEstimation()
})

onUnmounted(() => {
  if (routeEstimationTimer) {
    clearTimeout(routeEstimationTimer)
  }
})

const applyAutoEstimation = () => {
  const origin = form.value.originPort?.trim()
  const destination = form.value.destinationPort?.trim()
  const mode = form.value.transportationMode

  if (!origin || !destination || !mode) {
    autoEstimationState.error = null
    autoEstimationState.distanceSource = null
    autoEstimationState.totalDistance = null
    autoEstimationState.durationDays = null
    return
  }

  try {
    autoEstimationState.isCalculating = true
    autoEstimationState.error = null
    const estimation = calculateDurationDetails({
      origin,
      destination,
      transportationMode: mode
    })

    if (typeof estimation.durationDays === 'number') {
      form.value.duration = estimation.durationDays
    }

    if (typeof estimation.totalDistance === 'number') {
      form.value.distance = Math.round(estimation.totalDistance)
    }

    autoEstimationState.distanceSource = estimation.distanceSource
    autoEstimationState.totalDistance = estimation.totalDistance
    autoEstimationState.durationDays = estimation.durationDays
    autoEstimationState.lastUpdated = Date.now()
  } catch (estimationError) {
    console.warn('Route auto-estimation failed:', estimationError)
    autoEstimationState.error = estimationError.message || 'Unable to auto-estimate route details'
  } finally {
    autoEstimationState.isCalculating = false
  }
}

let routeEstimationTimer = null

const scheduleRouteEstimation = () => {
  if (routeEstimationTimer) {
    clearTimeout(routeEstimationTimer)
  }

  routeEstimationTimer = setTimeout(() => {
    applyAutoEstimation()
  }, 300)
}

watch(
  () => [form.value.originPort, form.value.destinationPort, form.value.transportationMode],
  () => scheduleRouteEstimation()
)
</script>
