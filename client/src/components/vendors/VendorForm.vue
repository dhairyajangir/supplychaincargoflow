<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-3xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Vendors
        </Button>
        <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">
          {{ isEditMode ? 'Edit Vendor' : 'Create Vendor' }}
        </h1>
        <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">
          {{ isEditMode ? 'Update vendor information' : 'Add a new vendor to your supply chain' }}
        </p>
      </div>

      <!-- Form Card -->
      <Card>
        <CardHeader>
          <CardTitle>Vendor Information</CardTitle>
          <CardDescription>
            Fill in the details for the vendor
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Vendor Name -->
            <div class="space-y-2">
              <label for="name" class="text-sm font-medium dark:text-sidebar-foreground">
                Vendor Name <span class="text-red-500">*</span>
              </label>
              <Input
                id="name"
                v-model="formData.name"
                placeholder="Enter vendor name"
                required
                :disabled="isSubmitting"
              />
            </div>

            <!-- Service Type -->
            <div class="space-y-2">
              <label for="serviceType" class="text-sm font-medium dark:text-sidebar-foreground">
                Service Type <span class="text-red-500">*</span>
              </label>
              <Select v-model="formData.serviceType" required :disabled="isSubmitting">
                <SelectTrigger>
                  <SelectValue placeholder="Select service type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Logistics">Logistics</SelectItem>
                  <SelectItem value="Transportation">Transportation</SelectItem>
                  <SelectItem value="Warehousing">Warehousing</SelectItem>
                  <SelectItem value="Freight">Freight</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <!-- Contact Email -->
            <div class="space-y-2">
              <label for="contactEmail" class="text-sm font-medium dark:text-sidebar-foreground">
                Contact Email
              </label>
              <Input
                id="contactEmail"
                v-model="formData.contactEmail"
                type="email"
                placeholder="vendor@example.com"
                :disabled="isSubmitting"
              />
            </div>

            <!-- Contact Phone -->
            <div class="space-y-2">
              <label for="contactPhone" class="text-sm font-medium dark:text-sidebar-foreground">
                Contact Phone
              </label>
              <Input
                id="contactPhone"
                v-model="formData.contactPhone"
                placeholder="+1 (555) 123-4567"
                :disabled="isSubmitting"
              />
            </div>

            <!-- Address -->
            <div class="space-y-2">
              <label for="address" class="text-sm font-medium dark:text-sidebar-foreground">
                Address
              </label>
              <Textarea
                id="address"
                v-model="formData.address"
                placeholder="Enter vendor address"
                rows="3"
                :disabled="isSubmitting"
              />
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-md">
              <div class="flex">
                <AlertCircle class="h-5 w-5 text-red-600 mr-2" />
                <p class="text-sm text-red-600">{{ errorMessage }}</p>
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
                {{ isSubmitting ? 'Saving...' : (isEditMode ? 'Update Vendor' : 'Create Vendor') }}
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ArrowLeft, Loader2, AlertCircle, CheckCircle } from 'lucide-vue-next'
import { vendorApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const isEditMode = computed(() => !!route.params.id)
const vendorId = computed(() => route.params.id)

const formData = ref({
  name: '',
  serviceType: '',
  contactEmail: '',
  contactPhone: '',
  address: ''
})

const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const isFormValid = computed(() => {
  return formData.value.name.trim() !== '' && formData.value.serviceType !== ''
})

// Helper function to parse contactInfo from backend
const parseContactInfo = (contactInfo) => {
  if (!contactInfo) return { email: '', phone: '', address: '' }
  
  // Split by comma and trim whitespace
  const parts = contactInfo.split(',').map(part => part.trim())
  
  let email = ''
  let phone = ''
  let address = ''
  
  parts.forEach(part => {
    // Check if it's an email (contains @)
    if (part.includes('@')) {
      email = part
    }
    // Check if it's a phone (starts with + or contains numbers and dashes/parentheses)
    else if (part.match(/^[\+\d\(\)\-\s]+$/)) {
      phone = part
    }
    // Otherwise, treat as address
    else if (part.length > 0) {
      address = address ? address + ', ' + part : part
    }
  })
  
  return { email, phone, address }
}

// Helper function to combine fields into contactInfo for backend
const combineContactInfo = (email, phone, address) => {
  const parts = []
  if (email) parts.push(email)
  if (phone) parts.push(phone)
  if (address) parts.push(address)
  return parts.join(', ')
}

const loadVendor = async () => {
  if (!isEditMode.value) return
  
  try {
    const vendor = await vendorApi.getById(vendorId.value)
    
    // Parse contactInfo into separate fields
    const contactData = parseContactInfo(vendor.contactInfo)
    
    formData.value = {
      name: vendor.name || '',
      serviceType: vendor.serviceType || '',
      contactEmail: contactData.email,
      contactPhone: contactData.phone,
      address: contactData.address
    }
  } catch (error) {
    console.error('Error loading vendor:', error)
    errorMessage.value = 'Failed to load vendor details. Please try again.'
  }
}

const handleSubmit = async () => {
  if (!isFormValid.value) {
    errorMessage.value = 'Please fill in all required fields'
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    // Combine contact fields into contactInfo for backend
    const contactInfo = combineContactInfo(
      formData.value.contactEmail.trim(),
      formData.value.contactPhone.trim(),
      formData.value.address.trim()
    )
    
    // Prepare payload
    const payload = {
      name: formData.value.name.trim(),
      serviceType: formData.value.serviceType,
      contactInfo: contactInfo || null,
      isActive: true
    }

    if (isEditMode.value) {
      // Update existing vendor
      await vendorApi.update(vendorId.value, payload)
      successMessage.value = 'Vendor updated successfully!'
    } else {
      // Create new vendor
      await vendorApi.create(payload)
      successMessage.value = 'Vendor created successfully!'
    }

    // Emit event for real-time updates with details
    window.dispatchEvent(new CustomEvent('vendors-updated', {
      detail: {
        action: isEditMode.value ? 'updated' : 'created',
        vendor: {
          vendorId: payload.vendorId || vendorId.value,
          name: payload.name,
          serviceType: payload.serviceType
        }
      }
    }))

    // Redirect after short delay
    setTimeout(() => {
      router.push('/vendors')
    }, 1500)

  } catch (error) {
    console.error('Error saving vendor:', error)
    errorMessage.value = error.message || 'Failed to save vendor. Please try again.'
    
    // If timeout error, verify if vendor was actually created
    if (error.message?.toLowerCase().includes('timeout')) {
      try {
        // Wait a bit and check if vendor exists
        setTimeout(async () => {
          const vendors = await vendorApi.getAll()
          const created = vendors.find(v => 
            v.name === payload.name && 
            v.serviceType === payload.serviceType
          )
          
          if (created) {
            successMessage.value = 'Vendor created successfully (verified)!'
            errorMessage.value = ''
            setTimeout(() => router.push('/vendors'), 1500)
          }
        }, 3000)
      } catch (verifyError) {
        console.error('Verification error:', verifyError)
      }
    }
  } finally {
    isSubmitting.value = false
  }
}

const goBack = () => {
  router.push('/vendors')
}

onMounted(() => {
  loadVendor()
})
</script>
