<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-6xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Cargo
        </Button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin dark:text-sidebar-foreground" />
        <span class="ml-2 dark:text-sidebar-foreground">Loading cargo details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 dark:text-sidebar-foreground/70 mb-4">{{ error }}</p>
        <Button @click="loadCargo" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Cargo Details -->
      <div v-else-if="cargo" class="space-y-6">
        <!-- Header with Actions -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">
              Cargo #{{ cargo.cargoId }}
            </h1>
            <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">{{ cargo.type }}</p>
          </div>
          <div class="flex space-x-2">
            <Button @click="editCargo" variant="outline">
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
          <!-- Cargo Type -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Cargo Type</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ cargo.type }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Category</span>
              </div>
            </CardContent>
          </Card>

          <!-- Weight -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Weight</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ cargo.weight || 0 }} kg</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Gross weight</span>
              </div>
            </CardContent>
          </Card>

          <!-- Value -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Value</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">${{ formatNumber(cargo.value || 0) }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Declared value</span>
              </div>
            </CardContent>
          </Card>

          <!-- Volume -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Volume</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ cargo.volume ? `${cargo.volume} m³` : 'N/A' }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Cubic volume</span>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Cargo Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Essential cargo details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Cargo ID</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">#{{ cargo.cargoId }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Type</label>
                <Badge class="mt-1" :variant="getTypeBadgeVariant(cargo.type)">
                  {{ cargo.type }}
                </Badge>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Weight</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ cargo.weight || 0 }} kg</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Value</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">${{ formatNumber(cargo.value || 0) }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Volume</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ cargo.volume ? `${cargo.volume} m³` : 'Not specified' }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Shipment Information -->
          <Card>
            <CardHeader>
              <CardTitle>Shipment Information</CardTitle>
              <CardDescription>Associated shipment details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Shipment ID</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">#{{ cargo.shipment.shipmentId }}</p>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Origin</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ cargo.shipment.origin }}</p>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Destination</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ cargo.shipment.destination }}</p>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Status</label>
                <Badge class="mt-1" :variant="getStatusVariant(cargo.shipment.status)">
                  {{ cargo.shipment.status }}
                </Badge>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Estimated Delivery</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ formatDate(cargo.shipment.estimatedDelivery) }}</p>
              </div>
              <div v-if="!cargo.shipment" class="text-center py-8 text-gray-500 dark:text-sidebar-foreground/60">
                <Package class="h-12 w-12 mx-auto mb-2 opacity-50" />
                <p>Not assigned to any shipment</p>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Description -->
        <Card v-if="cargo.description">
          <CardHeader>
            <CardTitle>Description</CardTitle>
            <CardDescription>Additional cargo information</CardDescription>
          </CardHeader>
          <CardContent>
            <p class="text-base text-gray-700 dark:text-sidebar-foreground">{{ cargo.description }}</p>
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
  Weight,
  DollarSign,
  Box
} from 'lucide-vue-next'
import { cargoApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const cargo = ref(null)
const isLoading = ref(false)
const error = ref(null)

const cargoId = route.params.id

const loadCargo = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await cargoApi.getById(cargoId)
    cargo.value = data
  } catch (err) {
    error.value = err.message || 'Failed to load cargo details'
    console.error('❌ Error loading cargo:', err)
  } finally {
    isLoading.value = false
  }
}

const getTypeBadgeVariant = (type) => {
  const variants = {
    'Electronics': 'default',
    'Clothing': 'secondary',
    'Food': 'outline',
    'Machinery': 'destructive',
    'Perishables': 'secondary',
    'Hazardous': 'destructive',
    'General': 'outline'
  }
  return variants[type] || 'outline'
}

const getStatusVariant = (status) => {
  const variants = {
    'Shipped': 'default',
    'In Transit': 'secondary',
    'Delivered': 'default',
    'Cancelled': 'destructive',
    'Pending': 'outline',
    'Created': 'outline',
    'Picked Up': 'secondary'
  }
  return variants[status] || 'outline'
}

const formatNumber = (num) => {
  if (!num && num !== 0) return '0'
  return new Intl.NumberFormat().format(num)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  })
}

const editCargo = () => {
  router.push(`/cargo/${cargoId}/edit`)
}

const confirmDelete = async () => {
  if (!confirm(`Are you sure you want to delete cargo #${cargo.value.cargoId}?`)) {
    return
  }

  try {
    await cargoApi.remove(cargoId)
    // Emit event for real-time updates
    window.dispatchEvent(new Event('cargo-updated'))
    router.push('/cargo')
  } catch (error) {
    console.error('❌ Error deleting cargo:', error)
    alert('Failed to delete cargo. Please try again.')
  }
}

const goBack = () => {
  router.push('/cargo')
}

onMounted(() => {
  loadCargo()
})
</script>
