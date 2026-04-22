<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-6xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Shipments
        </Button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading shipment details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 dark:text-sidebar-foreground/70 mb-4">{{ error }}</p>
        <Button @click="loadShipment" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Shipment Details -->
      <div v-else-if="shipment" class="space-y-6">
        <!-- Header with Actions -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">
              Shipment #{{ shipment.shipmentId }}
            </h1>
            <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">
              {{ shipment.origin }} → {{ shipment.destination }}
            </p>
          </div>
          <div class="flex space-x-2">
            <Button @click="editShipment" variant="outline">
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
          <!-- Status -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Status</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ shipment.status }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Current shipment status</span>
              </div>
            </CardContent>
          </Card>

          <!-- Est. Delivery -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Est. Delivery</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatDate(shipment.estimatedDelivery) }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Expected arrival date</span>
              </div>
            </CardContent>
          </Card>

          <!-- Cargo Items -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Cargo Items</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ shipment.cargoItems?.length || 0 }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Items in this shipment</span>
              </div>
            </CardContent>
          </Card>

          <!-- Route Assignment -->
          <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
            <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
              <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Route Status</span>
            </div>
            <CardContent class="px-4 py-1.5 pb-3">
              <div class="flex items-center gap-2">
                <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ shipment.assignedRoute ? 'Assigned' : 'Unassigned' }}</div>
              </div>
              <div class="mt-1">
                <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Route assignment</span>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Shipment Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Essential shipment details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Shipment ID</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">#{{ shipment.shipmentId }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Origin</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">{{ shipment.origin }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Destination</label>
                <p class="mt-1 text-base font-medium dark:text-sidebar-foreground">{{ shipment.destination }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Status</label>
                <Badge class="mt-1" :variant="getStatusVariant(shipment.status)">
                  {{ shipment.status }}
                </Badge>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Estimated Delivery</label>
                <p class="mt-1 text-base dark:text-sidebar-foreground">{{ formatDate(shipment.estimatedDelivery) }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Assignment Information -->
          <Card>
            <CardHeader>
              <CardTitle>Assignment Information</CardTitle>
              <CardDescription>Route and vendor details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Assigned Route</label>
                <div v-if="shipment.assignedRoute" class="mt-1">
                  <p class="text-base font-medium dark:text-sidebar-foreground">
                    {{ shipment.assignedRoute.originPort }} → {{ shipment.assignedRoute.destinationPort }}
                  </p>
                  <p class="text-sm text-gray-600 dark:text-sidebar-foreground/70">
                    Duration: {{ shipment.assignedRoute.duration }} days
                  </p>
                </div>
                <p v-else class="mt-1 text-base text-gray-400 dark:text-sidebar-foreground/60">Not assigned</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500 dark:text-sidebar-foreground/70">Assigned Vendor</label>
                <div v-if="shipment.assignedVendor" class="mt-1">
                  <p class="text-base font-medium dark:text-sidebar-foreground">{{ shipment.assignedVendor.name }}</p>
                  <Badge class="mt-1" variant="outline">
                    {{ shipment.assignedVendor.serviceType }}
                  </Badge>
                </div>
                <p v-else class="mt-1 text-base text-gray-400 dark:text-sidebar-foreground/60">Not assigned</p>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Cargo Items -->
        <Card>
          <CardHeader>
            <CardTitle>Cargo Items</CardTitle>
            <CardDescription>Items included in this shipment</CardDescription>
          </CardHeader>
          <CardContent>
            <div v-if="shipment.cargoItems && shipment.cargoItems.length > 0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Cargo ID</TableHead>
                    <TableHead>Type</TableHead>
                    <TableHead>Weight (kg)</TableHead>
                    <TableHead>Volume (m³)</TableHead>
                    <TableHead>Value ($)</TableHead>
                    <TableHead class="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  <TableRow v-for="item in shipment.cargoItems" :key="item.cargoId">
                    <TableCell class="font-medium">#{{ item.cargoId }}</TableCell>
                    <TableCell>
                      <Badge variant="outline">{{ item.type || 'N/A' }}</Badge>
                    </TableCell>
                    <TableCell>{{ item.weight || 0 }}</TableCell>
                    <TableCell>{{ item.volume || 'N/A' }}</TableCell>
                    <TableCell>${{ formatNumber(item.value || 0) }}</TableCell>
                    <TableCell class="text-right">
                      <Button 
                        @click="viewCargo(item)" 
                        size="sm" 
                        variant="outline"
                      >
                        View Details
                      </Button>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </div>
            <div v-else class="text-center py-8 text-gray-500 dark:text-sidebar-foreground/60">
              <Package class="h-12 w-12 mx-auto mb-2 opacity-50" />
              <p>No cargo items assigned to this shipment.</p>
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
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  Edit,
  Trash2,
  Truck,
  Calendar,
  Package,
  MapPin
} from 'lucide-vue-next'
import { shipmentApi, cargoApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const shipment = ref(null)
const isLoading = ref(false)
const error = ref(null)

const shipmentId = route.params.id

const loadShipment = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await shipmentApi.getById(shipmentId)
    
    // Fetch all cargo items and filter by this shipment
    try {
      const allCargo = await cargoApi.getAll()
      // Filter cargo items that belong to this shipment
      const shipmentCargo = allCargo.filter(cargo => {
        // Check if cargo has shipmentId matching this shipment
        return cargo.shipmentId === shipmentId || 
               cargo.shipmentId === parseInt(shipmentId) ||
               cargo.shipment?.shipmentId === shipmentId ||
               cargo.shipment?.shipmentId === parseInt(shipmentId)
      })
      
      // Attach cargo items to shipment data
      data.cargoItems = shipmentCargo
      console.log('Loaded cargo items for shipment:', shipmentCargo)
    } catch (cargoErr) {
      console.error('Error loading cargo items:', cargoErr)
      // Continue with empty cargo items if fetch fails
      data.cargoItems = []
    }
    
    shipment.value = data
  } catch (err) {
    error.value = err.message || 'Failed to load shipment details'
    console.error('Error loading shipment:', err)
  } finally {
    isLoading.value = false
  }
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

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  })
}

const formatNumber = (num) => {
  if (!num && num !== 0) return '0'
  return new Intl.NumberFormat().format(num)
}

const viewCargo = (cargo) => {
  router.push(`/cargo/${cargo.cargoId}`)
}

const editShipment = () => {
  router.push(`/shipments/${shipmentId}/edit`)
}

const confirmDelete = async () => {
  if (!confirm(`Are you sure you want to delete shipment #${shipment.value.shipmentId}?`)) {
    return
  }

  try {
    await shipmentApi.delete(shipmentId)
    // Emit event for real-time updates
    window.dispatchEvent(new Event('shipments-updated'))
    router.push('/shipments')
  } catch (error) {
    console.error('Error deleting shipment:', error)
    alert('Failed to delete shipment. Please try again.')
  }
}

const goBack = () => {
  router.push('/shipments')
}

onMounted(() => {
  loadShipment()
})
</script>
