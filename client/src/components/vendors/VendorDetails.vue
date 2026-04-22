<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-6xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Vendors
        </Button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin dark:text-sidebar-foreground" />
        <span class="ml-2 dark:text-sidebar-foreground">Loading vendor details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 dark:text-sidebar-foreground/70 mb-4">{{ error }}</p>
        <Button @click="loadVendor" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Vendor Details -->
      <div v-else-if="vendor" class="space-y-6">
        <!-- Header with Actions -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">{{ vendor.name }}</h1>
            <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">Vendor ID: {{ vendor.vendorId }}</p>
          </div>
          <div class="flex space-x-2">
            <Button @click="editVendor" variant="outline">
              <Edit class="mr-2 h-4 w-4" />
              Edit
            </Button>
            <Button @click="confirmDelete" variant="destructive">
              <Trash2 class="mr-2 h-4 w-4" />
              Delete
            </Button>
          </div>
        </div>

        <!-- Stats Cards (RouteList list-stats style) -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <!-- Service Type -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Service Type</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ vendor.serviceType }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Operational role</span>
              </div>
            </CardContent>
          </Card>

          <!-- Contact Status -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Contact Status</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ vendor.contactEmail ? 'Available' : 'No Contact' }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Email availability</span>
              </div>
            </CardContent>
          </Card>

          <!-- Phone Status -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Phone Status</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ vendor.contactPhone ? 'Available' : 'No Phone' }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Phone availability</span>
              </div>
            </CardContent>
          </Card>

          <!-- Address Status -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Address Status</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ vendor.address ? 'Available' : 'No Address' }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Address availability</span>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Vendor Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Essential vendor details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Vendor ID</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">{{ vendor.vendorId }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Vendor Name</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">{{ vendor.name }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Service Type</label>
                <Badge class="mt-1" :variant="getServiceTypeBadgeVariant(vendor.serviceType)">
                  {{ vendor.serviceType }}
                </Badge>
              </div>
            </CardContent>
          </Card>

          <!-- Contact Information -->
          <Card>
            <CardHeader>
              <CardTitle>Contact Information</CardTitle>
              <CardDescription>Communication details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Email</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ vendor.contactEmail || 'Not provided' }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Phone</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ vendor.contactPhone || 'Not provided' }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Address</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ vendor.address || 'Not provided' }}</p>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Additional Information (placeholder for future features) -->
        <Card>
          <CardHeader>
            <CardTitle>Associated Shipments</CardTitle>
            <CardDescription>Shipments managed by this vendor</CardDescription>
          </CardHeader>
          <CardContent>
            <!-- Loading State -->
            <div v-if="loadingShipments" class="text-center py-8">
              <Loader2 class="h-8 w-8 animate-spin mx-auto mb-2 dark:text-sidebar-foreground" />
              <p class="text-gray-500 dark:text-sidebar-foreground/70">Loading shipments...</p>
            </div>

            <!-- No Shipments -->
            <div v-else-if="vendorShipments.length === 0" class="text-center py-8 text-gray-500 dark:text-sidebar-foreground/70">
              <Package class="h-12 w-12 mx-auto mb-2 opacity-50 dark:text-sidebar-foreground/60" />
              <p>No shipments assigned to this vendor yet.</p>
            </div>

            <!-- Shipments Table -->
            <div v-else class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-gray-50 dark:bg-sidebar/50">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-sidebar-foreground/70 uppercase">Shipment ID</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-sidebar-foreground/70 uppercase">Origin</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-sidebar-foreground/70 uppercase">Destination</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-sidebar-foreground/70 uppercase">Status</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-sidebar-foreground/70 uppercase">Est. Delivery</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-sidebar-foreground/70 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 dark:divide-sidebar-border">
                  <tr v-for="shipment in vendorShipments" :key="shipment.shipmentId" class="hover:bg-gray-50 dark:hover:bg-sidebar/30">
                    <td class="px-4 py-3 text-sm font-medium dark:text-sidebar-foreground">#{{ shipment.shipmentId }}</td>
                    <td class="px-4 py-3 text-sm dark:text-sidebar-foreground">{{ shipment.origin }}</td>
                    <td class="px-4 py-3 text-sm dark:text-sidebar-foreground">{{ shipment.destination }}</td>
                    <td class="px-4 py-3 text-sm">
                      <Badge :variant="getStatusBadgeVariant(shipment.status)">
                        {{ shipment.status }}
                      </Badge>
                    </td>
                    <td class="px-4 py-3 text-sm dark:text-sidebar-foreground">
                      {{ shipment.estimatedDelivery ? new Date(shipment.estimatedDelivery).toLocaleDateString() : 'N/A' }}
                    </td>
                    <td class="px-4 py-3 text-sm">
                      <Button @click="viewShipment(shipment.shipmentId)" variant="ghost" size="sm">
                        <Eye class="h-4 w-4" />
                      </Button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  Edit,
  Trash2,
  Package,
  Mail,
  Phone,
  MapPin,
  Eye
} from 'lucide-vue-next'
import { vendorApi, shipmentApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const vendor = ref(null)
const isLoading = ref(false)
const error = ref(null)
const vendorShipments = ref([])
const loadingShipments = ref(false)

const vendorId = route.params.id

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

const loadVendor = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await vendorApi.getById(vendorId)
    
    // Parse contactInfo into separate fields
    const contactData = parseContactInfo(data.contactInfo)
    
    vendor.value = {
      ...data,
      contactEmail: contactData.email,
      contactPhone: contactData.phone,
      address: contactData.address
    }

    // Load shipments after vendor is loaded
    await loadVendorShipments()
  } catch (err) {
    error.value = err.message || 'Failed to load vendor details'
    console.error('❌ Error loading vendor:', err)
  } finally {
    isLoading.value = false
  }
}

const loadVendorShipments = async () => {
  loadingShipments.value = true
  try {
    // Fetch all shipments
    const allShipments = await shipmentApi.getAll()
    
    // Filter shipments that are assigned to this vendor
    vendorShipments.value = allShipments.filter(
      shipment => shipment.assignedVendor && 
                   shipment.assignedVendor.vendorId === parseInt(vendorId)
    )
    
    console.log(`✅ Found ${vendorShipments.value.length} shipments for vendor ${vendorId}`)
  } catch (err) {
    console.error('❌ Error loading vendor shipments:', err)
    // Don't show error, just keep shipments empty
  } finally {
    loadingShipments.value = false
  }
}

const getServiceTypeBadgeVariant = (serviceType) => {
  switch (serviceType) {
    case 'Logistics':
      return 'default'
    case 'Transportation':
      return 'secondary'
    case 'Warehousing':
      return 'outline'
    case 'Freight':
      return 'destructive'
    default:
      return 'outline'
  }
}

const getStatusBadgeVariant = (status) => {
  switch (status) {
    case 'Delivered':
      return 'default'
    case 'In Transit':
      return 'secondary'
    case 'Pending':
      return 'outline'
    case 'Delayed':
      return 'destructive'
    default:
      return 'outline'
  }
}

const viewShipment = (shipmentId) => {
  router.push(`/shipments/${shipmentId}`)
}

const editVendor = () => {
  router.push(`/vendors/${vendorId}/edit`)
}

const confirmDelete = async () => {
  if (!confirm(`Are you sure you want to delete vendor "${vendor.value.name}"?`)) {
    return
  }

  try {
    await vendorApi.delete(vendorId)
    // Emit event for real-time updates
    window.dispatchEvent(new Event('vendors-updated'))
    router.push('/vendors')
  } catch (error) {
    console.error('❌ Error deleting vendor:', error)
    alert('Failed to delete vendor. Please try again.')
  }
}

const goBack = () => {
  router.push('/vendors')
}

onMounted(() => {
  loadVendor()
})
</script>
