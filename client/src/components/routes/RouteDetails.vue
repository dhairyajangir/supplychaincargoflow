<template>
  <div class="container mx-auto px-4 py-8 max-w-6xl">
    <div class="space-y-6">
      <!-- Back Button -->
      <div>
        <Button @click="$router.push('/routes')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Routes
        </Button>
      </div>

      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">Route Details</h1>
          <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">Detailed information about the selected route</p>
        </div>
        <div v-if="routeData" class="flex items-center space-x-2">
          <Button @click="editRoute" variant="outline" size="sm">
            <Edit class="mr-2 h-4 w-4" />
            Edit Route
          </Button>
          <Button @click="confirmDelete" variant="outline" size="sm" class="text-red-600 hover:text-red-700">
            <Trash2 class="mr-2 h-4 w-4" />
            Delete Route
          </Button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-32">
        <Loader2 class="h-6 w-6 animate-spin dark:text-sidebar-foreground" />
        <span class="ml-2 dark:text-sidebar-foreground">Loading route details...</span>
      </div>
      
      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 dark:text-sidebar-foreground/70">{{ error }}</p>
        <Button @click="loadRoute" class="mt-4" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Route Content -->
      <div v-else-if="routeData" class="space-y-6">
        <!-- Route Information Card + Compact Metrics -->
        <Card>
          <CardHeader>
            <div class="flex items-center justify-between">
              <div>
                <CardTitle class="text-xl">
                  {{ routeData.originPort }} → {{ routeData.destinationPort }}
                </CardTitle>
                <CardDescription>Route ID: #{{ routeData.routeId }}</CardDescription>
              </div>
              <div class="flex items-center space-x-2">
                <Badge :variant="getStatusBadgeVariant(routeData.status)" class="text-sm">
                  {{ routeData.status }}
                </Badge>
                <Badge :variant="getModeBadgeVariant(routeData.transportationMode)" class="text-sm">
                  {{ routeData.transportationMode }}
                </Badge>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <!-- List-style stat cards (match RouteList) -->
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
              <!-- Distance -->
              <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
                <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
                  <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Distance</span>
                </div>
                <CardContent class="px-4 py-1.5 pb-3">
                  <div class="flex items-center gap-2">
                    <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(routeData.distance) }} km</div>
                  </div>
                  <div class="mt-1">
                    <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Total route length</span>
                  </div>
                </CardContent>
              </Card>

              <!-- Duration -->
              <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
                <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
                  <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Duration</span>
                </div>
                <CardContent class="px-4 py-1.5 pb-3">
                  <div class="flex items-center gap-2">
                    <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ routeData.duration }} days</div>
                  </div>
                  <div class="mt-1">
                    <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Estimated travel time</span>
                  </div>
                </CardContent>
              </Card>

              <!-- Cost -->
              <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
                <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
                  <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Cost</span>
                </div>
                <CardContent class="px-4 py-1.5 pb-3">
                  <div class="flex items-center gap-2">
                    <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">${{ formatNumber(routeData.cost) }}</div>
                  </div>
                  <div class="mt-1">
                    <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Operational estimate</span>
                  </div>
                </CardContent>
              </Card>

              <!-- Created -->
              <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
                <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
                  <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Created</span>
                </div>
                <CardContent class="px-4 py-1.5 pb-3">
                  <div class="flex items-center gap-2">
                    <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatDate(routeData.createdAt) }}</div>
                  </div>
                  <div class="mt-1">
                    <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Date added</span>
                  </div>
                </CardContent>
              </Card>
            </div>
          </CardContent>
        </Card>

        <!-- Route Map -->
        <Card>
          <CardHeader>
            <CardTitle>Route Visualization</CardTitle>
            <CardDescription>
              Geographic representation of the route
            </CardDescription>
          </CardHeader>
          <CardContent>
            <RouteMap :routes="[routeData]" class="h-[400px] rounded-lg" />
          </CardContent>
        </Card>

        <!-- Assigned Shipments -->
        <Card>
          <CardHeader>
            <CardTitle>Assigned Shipments</CardTitle>
            <CardDescription>
              Shipments currently using this route
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div v-if="loadingShipments" class="flex items-center justify-center h-32">
              <Loader2 class="h-6 w-6 animate-spin dark:text-sidebar-foreground" />
              <span class="ml-2 dark:text-sidebar-foreground">Loading shipments...</span>
            </div>
            <div v-else-if="assignedShipments.length === 0" class="text-center py-8">
              <Package class="h-12 w-12 text-gray-400 dark:text-sidebar-foreground/60 mx-auto mb-4" />
              <p class="text-gray-600 dark:text-sidebar-foreground/70">No shipments assigned to this route</p>
            </div>
            <div v-else>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Shipment ID</TableHead>
                    <TableHead>Origin</TableHead>
                    <TableHead>Destination</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Estimated Delivery</TableHead>
                    <TableHead class="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  <TableRow v-for="shipment in assignedShipments" :key="shipment.shipmentId">
                    <TableCell class="font-medium">#{{ shipment.shipmentId }}</TableCell>
                    <TableCell>{{ shipment.origin }}</TableCell>
                    <TableCell>{{ shipment.destination }}</TableCell>
                    <TableCell>
                      <Badge :variant="getShipmentStatusBadgeVariant(shipment.status)">
                        {{ shipment.status }}
                      </Badge>
                    </TableCell>
                    <TableCell>{{ formatDate(shipment.estimatedDelivery) }}</TableCell>
                    <TableCell class="text-right">
                      <Button @click="viewShipment(shipment)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import RouteMap from '@/components/shared/RouteMap.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { 
  ArrowLeft, 
  Loader2, 
  AlertCircle, 
  Package,
  Eye,
  Edit, 
  Trash2,
  MapPin,
  Clock,
  DollarSign,
  Calendar
} from 'lucide-vue-next'
import { routeApi, shipmentApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const routeData = ref(null)
const assignedShipments = ref([])
const isLoading = ref(false)
const loadingShipments = ref(false)
const error = ref(null)

const loadRoute = async () => {
  const routeId = Number(route.params.id)
  if (!routeId) {
    error.value = 'Invalid route ID'
    return
  }

  isLoading.value = true
  error.value = null
  try {
    routeData.value = await routeApi.getById(routeId)
    // Load shipments after route is loaded
    await loadShipments()
  } catch (err) {
    error.value = err.message || 'Failed to load route details'
    console.error('Error loading route:', err)
  } finally {
    isLoading.value = false
  }
}

const loadShipments = async () => {
  if (!routeData.value) return
  
  loadingShipments.value = true
  try {
    // Fetch all shipments
    const allShipments = await shipmentApi.getAll()
    
    // Filter shipments that are assigned to this route
    assignedShipments.value = allShipments.filter(
      shipment => shipment.assignedRoute && 
                   shipment.assignedRoute.routeId === routeData.value.routeId
    )
    
    console.log(`✅ Found ${assignedShipments.value.length} shipments for route ${routeData.value.routeId}`)
  } catch (err) {
    console.error('❌ Error loading shipments:', err)
    // Don't show error, just keep shipments empty
    assignedShipments.value = []
  } finally {
    loadingShipments.value = false
  }
}

const getModeBadgeVariant = (mode) => {
  const modeUpper = (mode || '').toUpperCase()
  switch (modeUpper) {
    case 'SEA':
    case 'OCEAN':
      return 'default'
    case 'AIR':
      return 'secondary'
    case 'ROAD':
    case 'ROAD':
      return 'outline'
    case 'RAIL':
      return 'destructive'
    default:
      return 'outline'
  }
}

const getStatusBadgeVariant = (status) => {
  switch (status) {
    case 'Active':
      return 'default'
    case 'Delayed':
      return 'destructive'
    case 'Closed':
      return 'secondary'
    default:
      return 'outline'
  }
}

const getShipmentStatusBadgeVariant = (status) => {
  switch (status) {
    case 'Delivered':
      return 'default'
    case 'In Transit':
    case 'Shipped':
      return 'secondary'
    case 'Pending':
    case 'Created':
      return 'outline'
    case 'Cancelled':
      return 'destructive'
    default:
      return 'outline'
  }
}

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

const viewShipment = (shipment) => {
  router.push(`/shipments/${shipment.shipmentId}`)
}

const editRoute = () => {
  router.push(`/routes/${routeData.value.routeId}/edit`)
}

const confirmDelete = async () => {
  if (!routeData.value) return
  
  if (!confirm(`Are you sure you want to delete route "${routeData.value.originPort} → ${routeData.value.destinationPort}"?`)) {
    return
  }
  
  try {
    await routeApi.delete(routeData.value.routeId)
    
    // Dispatch event for real-time updates
    window.dispatchEvent(new CustomEvent('routes-updated'))
    
    router.push('/routes')
  } catch (error) {
    console.error('Error deleting route:', error)
    alert('Failed to delete route. Please try again.')
  }
}

onMounted(() => {
  loadRoute()
})
</script>
