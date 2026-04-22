<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-3xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Cargo
        </Button>
        <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">
          {{ isEditMode ? 'Edit Cargo' : 'Create Cargo' }}
        </h1>
        <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">
          {{ isEditMode ? 'Update cargo information' : 'Add new cargo to your inventory' }}
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading && isEditMode" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin dark:text-sidebar-foreground" />
        <span class="ml-2 dark:text-sidebar-foreground">Loading cargo details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="loadError" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 dark:text-sidebar-foreground/70 mb-4">{{ loadError }}</p>
        <Button @click="goBack" variant="outline">
          Back to Cargo
        </Button>
      </div>

      <!-- Form Card -->
      <Card v-else>
        <CardHeader>
          <CardTitle>Cargo Information</CardTitle>
          <CardDescription>
            Fill in the details for the cargo
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Type and Weight -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label for="type" class="text-sm font-medium dark:text-sidebar-foreground">
                  Cargo Type <span class="text-red-500">*</span>
                </label>
                <select 
                  id="type"
                  v-model="formData.type" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                  :disabled="isSubmitting"
                >
                  <option value="">Select cargo type</option>
                  <option value="Electronics">Electronics</option>
                  <option value="Perishables">Perishables</option>
                  <option value="Hazardous">Hazardous</option>
                  <option value="General">General</option>
                  <option value="Clothing">Clothing</option>
                  <option value="Food">Food</option>
                  <option value="Machinery">Machinery</option>
                  <option value="Raw Materials">Raw Materials</option>
                  <option value="Pharmaceuticals">Pharmaceuticals</option>
                  <option value="Automotive">Automotive</option>
                  <option value="Textiles">Textiles</option>
                  <option value="Chemicals">Chemicals</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              
              <div class="space-y-2">
                <label for="weight" class="text-sm font-medium dark:text-sidebar-foreground">
                  Weight (kg) <span class="text-red-500">*</span>
                </label>
                <Input
                  id="weight"
                  v-model.number="formData.weight"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="Enter weight in kg"
                  required
                  :disabled="isSubmitting"
                />
              </div>
            </div>

            <!-- Value and Volume -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label for="value" class="text-sm font-medium dark:text-sidebar-foreground">
                  Value ($) <span class="text-red-500">*</span>
                </label>
                <Input
                  id="value"
                  v-model.number="formData.value"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="Enter cargo value"
                  required
                  :disabled="isSubmitting"
                />
              </div>
              
              <div class="space-y-2">
                <label for="volume" class="text-sm font-medium dark:text-sidebar-foreground">
                  Volume (m³) (Optional)
                </label>
                <Input
                  id="volume"
                  v-model.number="formData.volume"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="Enter volume"
                  :disabled="isSubmitting"
                />
              </div>
            </div>

            <!-- Shipment Assignment -->
            <div class="space-y-2">
              <label for="shipment" class="text-sm font-medium dark:text-sidebar-foreground">
                Assign to Shipment (Optional)
              </label>
              <div class="relative">
                <select 
                  id="shipment"
                  v-model="formData.shipmentId" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  :disabled="isSubmitting || isLoadingShipments"
                >
                  <option value="">
                    {{ isLoadingShipments ? 'Loading shipments...' : 'Select shipment (optional)' }}
                  </option>
                  <option 
                    v-for="shipment in shipments" 
                    :key="shipment.shipmentId" 
                    :value="shipment.shipmentId.toString()"
                  >
                    #{{ shipment.shipmentId }} - {{ shipment.origin }} → {{ shipment.destination }}
                  </option>
                </select>
                <Loader2 v-if="isLoadingShipments" class="absolute right-3 top-3 h-4 w-4 animate-spin text-gray-400 dark:text-sidebar-foreground/60" />
              </div>
              <p v-if="shipments.length === 0 && !isLoadingShipments" class="text-xs text-gray-500 dark:text-sidebar-foreground/60">
                No shipments available. Create a shipment first.
              </p>
            </div>

            <!-- Description -->
            <div class="space-y-2">
              <label for="description" class="text-sm font-medium dark:text-sidebar-foreground">
                Description (Optional)
              </label>
              <Textarea
                id="description"
                v-model="formData.description"
                placeholder="Enter cargo description (optional)"
                rows="3"
                :disabled="isSubmitting"
              />
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-900 rounded-md">
              <div class="flex">
                <AlertCircle class="h-5 w-5 text-red-600 dark:text-red-400 mr-2" />
                <p class="text-sm text-red-600 dark:text-red-400">{{ errorMessage }}</p>
              </div>
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="p-4 bg-green-50 border border-green-200 rounded-md">
              <div class="flex">
                <CheckCircle class="h-5 w-5 text-green-600 mr-2" />
                <p class="text-sm text-green-600">{{ successMessage }}</p>
              </div>
            </div>

            <!-- Form Actions -->
            <div class="flex items-center justify-end space-x-4 pt-4 border-t">
              <Button
                type="button"
                @click="goBack"
                variant="outline"
                :disabled="isSubmitting"
              >
                Cancel
              </Button>
              <Button
                type="submit"
                :disabled="isSubmitting || !isFormValid"
              >
                <Loader2 v-if="isSubmitting" class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Saving...' : (isEditMode ? 'Update Cargo' : 'Create Cargo') }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { ArrowLeft, Loader2, AlertCircle, CheckCircle } from 'lucide-vue-next'
import { cargoApi, shipmentApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const isEditMode = computed(() => !!route.params.id)
const cargoId = computed(() => route.params.id)

const formData = ref({
  type: '',
  weight: null,
  value: null,
  volume: null,
  description: '',
  shipmentId: ''
})

const shipments = ref([])
const isLoading = ref(false)
const isLoadingShipments = ref(false)
const isSubmitting = ref(false)
const loadError = ref('')
const errorMessage = ref('')
const successMessage = ref('')

const isFormValid = computed(() => {
  const hasType = formData.value.type !== ''
  const hasWeight = formData.value.weight !== null && formData.value.weight > 0
  const hasValue = formData.value.value !== null && formData.value.value > 0
  
  console.log('🔍 Form validation:', { hasType, hasWeight, hasValue, formData: formData.value })
  
  return hasType && hasWeight && hasValue
})

const loadShipments = async () => {
  isLoadingShipments.value = true
  try {
    console.log('Loading shipments...')
    const data = await shipmentApi.getAll()
    console.log(' Shipments loaded:', data)
    shipments.value = data || []
    console.log('📦 Total shipments:', shipments.value.length)
  } catch (error) {
    console.error('❌ Error loading shipments:', error)
    errorMessage.value = 'Failed to load shipments. Please refresh the page.'
  } finally {
    isLoadingShipments.value = false
  }
}

const loadCargo = async () => {
  if (!isEditMode.value) return
  
  isLoading.value = true
  try {
    const cargo = await cargoApi.getById(cargoId.value)
    formData.value = {
      type: cargo.type || '',
      weight: cargo.weight || 0,
      value: cargo.value || 0,
      volume: cargo.volume || 0,
      description: cargo.description || '',
      shipmentId: cargo.shipment?.shipmentId?.toString() || ''
    }
  } catch (error) {
    console.error('Error loading cargo:', error)
    loadError.value = 'Failed to load cargo details. Please try again.'
  } finally {
    isLoading.value = false
  }
}

const handleSubmit = async () => {
  console.log('🚀 Form submitted')
  console.log('📋 Form data:', formData.value)
  
  if (!isFormValid.value) {
    errorMessage.value = 'Please fill in all required fields'
    console.log('❌ Form validation failed')
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    // Prepare payload
    const payload = {
      type: formData.value.type,
      weight: Number(formData.value.weight),
      value: Number(formData.value.value),
      volume: formData.value.volume ? Number(formData.value.volume) : null,
      weightUnit: 'kg',
      description: formData.value.description?.trim() || null,
      shipmentId: formData.value.shipmentId ? Number(formData.value.shipmentId) : null
    }

    console.log('📦 Payload to send:', payload)

    if (isEditMode.value) {
      // Update existing cargo
      console.log('🔄 Updating cargo...')
      await cargoApi.update(cargoId.value, payload)
      successMessage.value = 'Cargo updated successfully!'
    } else {
      // Create new cargo
      console.log('➕ Creating new cargo...')
      const result = await cargoApi.create(payload)
      console.log('✅ Cargo created:', result)
      successMessage.value = 'Cargo created successfully!'
    }

    // Emit event for real-time updates with details
    window.dispatchEvent(new CustomEvent('cargo-updated', {
      detail: {
        action: isEditMode.value ? 'updated' : 'created',
        cargo: {
          cargoId: result?.cargoId || cargoId.value,
          type: payload.type,
          weight: payload.weight,
          value: payload.value
        }
      }
    }))

    // Redirect after short delay
    setTimeout(() => {
      router.push('/cargo')
    }, 1500)

  } catch (error) {
    console.error('❌ Error saving cargo:', error)
    errorMessage.value = error.message || 'Failed to save cargo. Please try again.'
    
    // If timeout error, verify if cargo was actually created
    if (error.message?.toLowerCase().includes('timeout') && !isEditMode.value) {
      try {
        setTimeout(async () => {
          const cargoList = await cargoApi.getAll()
          const created = cargoList.find(c => 
            c.type === payload.type && 
            c.weight === payload.weight &&
            c.value === payload.value
          )
          
          if (created) {
            successMessage.value = 'Cargo created successfully (verified)!'
            errorMessage.value = ''
            setTimeout(() => router.push('/cargo'), 1500)
          }
        }, 3000)
      } catch (verifyError) {
        console.error('❌ Verification error:', verifyError)
      }
    }
  } finally {
    isSubmitting.value = false
  }
}

const goBack = () => {
  router.push('/cargo')
}

onMounted(async () => {
  await loadShipments()
  await loadCargo()
})
</script>
