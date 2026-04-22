<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <PageHeader 
        :title="$t('routes.title')"
        :description="$t('routes.subtitle')"
      />

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Routes Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('routes.totalRoutes') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(stats.total) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('routes.comparedPrevious') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Avg Duration Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('routes.avgDuration') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.avgDuration }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('routes.daysPerRoute') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Total Cost Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('routes.totalCost') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">${{ formatNumber(stats.totalCost) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('routes.allRoutes') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Total Distance Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('routes.totalDistance') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(stats.totalDistance) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('routes.kilometersCovered') }}</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Routes Table/Map Section -->
      <Card>
        <CardHeader>
          <div class="flex items-center justify-between">
            <div>
              <CardTitle>{{ $t('routes.allRoutes') }}</CardTitle>
              <CardDescription>
                {{ $t('routes.allRoutesDesc') }}
              </CardDescription>
            </div>
            <div class="flex items-center space-x-2">
              <Button 
                @click="viewMode = 'table'" 
                :variant="viewMode === 'table' ? 'default' : 'outline'"
                size="sm"
              >
                <List class="mr-2 h-4 w-4" />
                {{ $t('routes.table') }}
              </Button>
              <Button 
                @click="viewMode = 'map'" 
                :variant="viewMode === 'map' ? 'default' : 'outline'"
                size="sm"
              >
                <MapIcon class="mr-2 h-4 w-4" />
                {{ $t('routes.map') }}
              </Button>
              <Button @click="$router.push('/routes/create')">
                <Plus class="mr-2 h-4 w-4" />
                {{ $t('common.createRoute') }}
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <!-- Search and Filter Section -->
          <div class="flex flex-col md:flex-row gap-4 mb-6 relative z-10">
            <!-- Search Input -->
            <div class="relative flex-1">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                v-model="searchQuery"
                type="text"
                :placeholder="$t('routes.searchPlaceholder')"
                class="pl-10"
              />
            </div>

            <!-- Filter by Transportation Mode -->
            <Select v-model="filterMode">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue placeholder="Transport Mode" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ $t('routes.allModes') }}</SelectItem>
                <SelectItem value="OCEAN">Ocean</SelectItem>
                <SelectItem value="AIR">Air</SelectItem>
                <SelectItem value="ROAD">Road</SelectItem>
                <SelectItem value="RAIL">Rail</SelectItem>
              </SelectContent>
            </Select>

            <!-- Filter by Status -->
            <Select v-model="filterStatus">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ $t('common.allStatus') }}</SelectItem>
                <SelectItem value="Active">Active</SelectItem>
                <SelectItem value="Delayed">Delayed</SelectItem>
                <SelectItem value="Closed">Closed</SelectItem>
              </SelectContent>
            </Select>

            <!-- Clear Filters Button -->
            <Button 
              v-if="searchQuery || filterMode !== 'all' || filterStatus !== 'all'"
              @click="clearFilters" 
              variant="outline"
              size="icon"
              class="shrink-0"
            >
              <X class="h-4 w-4" />
            </Button>
          </div>
          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">{{ $t('routes.loadingRoutes') }}</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadRoutes" class="mt-4" variant="outline">
              {{ $t('common.tryAgain') }}
            </Button>
          </div>
          
          <!-- Map View -->
          <div v-else-if="viewMode === 'map'" class="h-[600px]">
            <Suspense>
              <template #default>
                <RouteMap 
                  :routes="routes" 
                  :filteredRoutes="filteredRoutes"
                  :highlightedRouteId="highlightedRouteId"
                  class="h-full rounded-lg" 
                />
              </template>
              <template #fallback>
                <div class="flex h-full items-center justify-center text-sm text-muted-foreground">
                  Loading map...
                </div>
              </template>
            </Suspense>
          </div>
          
          <!-- Table View -->
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{{ $t('common.serialNumber') }}</TableHead>
                  <TableHead>{{ $t('routes.originPort') }}</TableHead>
                  <TableHead>{{ $t('routes.destinationPort') }}</TableHead>
                  <TableHead>{{ $t('routes.distanceKm') }}</TableHead>
                  <TableHead>{{ $t('routes.durationDays') }}</TableHead>
                  <TableHead>{{ $t('routes.mode') }}</TableHead>
                  <TableHead>{{ $t('routes.costUsd') }}</TableHead>
                  <TableHead>{{ $t('common.status') }}</TableHead>
                  <TableHead class="text-right">{{ $t('common.actions') }}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="filteredRoutes.length === 0">
                  <TableCell :colspan="9" class="h-24 text-center">
                    <div v-if="searchQuery || filterMode !== 'all' || filterStatus !== 'all'">
                      <p class="text-gray-600 mb-2">{{ $t('routes.noRoutesMatches') }}</p>
                      <Button @click="clearFilters" variant="outline" size="sm">
                        {{ $t('common.clearFilters') }}
                      </Button>
                    </div>
                    <p v-else class="text-gray-600">
                      {{ $t('routes.noRoutes') }}
                    </p>
                  </TableCell>
                </TableRow>
                <TableRow 
                  v-for="(route, index) in filteredRoutes" 
                  :key="route.routeId"
                  :data-route-id="route.routeId"
                  @mouseover="highlightedRouteId = route.routeId"
                  @mouseleave="highlightedRouteId = null"
                >
                  <TableCell class="font-medium">{{ index + 1 }}</TableCell>
                  <TableCell>{{ route.originPort }}</TableCell>
                  <TableCell>{{ route.destinationPort }}</TableCell>
                  <TableCell>{{ formatNumber(route.distance) }}</TableCell>
                  <TableCell>{{ route.duration }}</TableCell>
                  <TableCell>
                    <Badge :variant="getModeBadgeVariant(route.transportationMode)">
                      {{ route.transportationMode }}
                    </Badge>
                  </TableCell>
                  <TableCell>${{ formatNumber(route.cost) }}</TableCell>
                  <TableCell>
                    <Badge :variant="getStatusBadgeVariant(route.status)">
                      {{ route.status }}
                    </Badge>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewRoute(route)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editRoute(route)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="openDeleteDialog(route)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
                        <Trash2 class="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Delete Confirmation Dialog -->
    <Dialog v-model:open="deleteDialog.isOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>
            <div class="flex items-center gap-2">
              <AlertCircle class="h-5 w-5 text-red-500" />
              {{ deleteDialog.title }}
            </div>
          </DialogTitle>
          <DialogDescription>
            {{ deleteDialog.message }}
          </DialogDescription>
        </DialogHeader>
        
        <!-- Route Details -->
        <div v-if="deleteDialog.route" class="bg-gray-50 dark:bg-sidebar-accent rounded-lg p-4 my-4">
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Route ID:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.route.routeId }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">From:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.route.originPort }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">To:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.route.destinationPort }}</span>
            </div>
          </div>
        </div>

        <!-- Error Message with Shipment Count -->
        <div v-if="deleteDialog.shipmentCount > 0" class="bg-amber-50 dark:bg-amber-950/50 border border-amber-200 dark:border-amber-800 rounded-lg p-4">
          <div class="flex gap-3">
            <AlertCircle class="h-5 w-5 text-amber-600 dark:text-amber-500 flex-shrink-0 mt-0.5" />
            <div class="flex-1">
              <p class="text-sm font-medium text-amber-800 dark:text-amber-400 mb-1">
                Cannot Delete Route
              </p>
              <p class="text-sm text-amber-700 dark:text-amber-300">
                This route is currently assigned to <strong>{{ deleteDialog.shipmentCount }} shipment(s)</strong>. 
                Please reassign or delete those shipments before deleting this route.
              </p>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button 
            v-if="deleteDialog.shipmentCount > 0"
            @click="closeDeleteDialog" 
            variant="default"
            class="w-full sm:w-auto"
          >
            Got it
          </Button>
          <template v-else>
            <Button 
              @click="closeDeleteDialog" 
              variant="outline"
            >
              Cancel
            </Button>
            <Button 
              @click="handleDelete" 
              variant="destructive"
              :disabled="deleteDialog.isDeleting"
            >
              <Loader2 v-if="deleteDialog.isDeleting" class="mr-2 h-4 w-4 animate-spin" />
              Delete Route
            </Button>
          </template>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '@/components/shared/PageHeader.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { 
  Dialog, 
  DialogContent, 
  DialogDescription, 
  DialogFooter, 
  DialogHeader, 
  DialogTitle 
} from '@/components/ui/dialog'
import { 
  Plus, 
  Route, 
  Clock, 
  DollarSign, 
  MapPin,
  Loader2, 
  AlertCircle, 
  Eye,
  Edit, 
  Trash2,
  List,
  Map as MapIcon,
  Search,
  X
} from 'lucide-vue-next'
import { routeApi } from '@/services/api'

const router = useRouter()
const RouteMap = defineAsyncComponent(() => import('@/components/shared/RouteMap.vue'))

const routes = ref([])
const isLoading = ref(false)
const error = ref(null)
const viewMode = ref('table') // 'table' or 'map'
const highlightedRouteId = ref(null)

// Search and Filter
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const filterMode = ref('all')
const filterStatus = ref('all')
let debounceTimeout = null

// Delete Dialog State
const deleteDialog = ref({
  isOpen: false,
  isDeleting: false,
  route: null,
  title: '',
  message: '',
  shipmentCount: 0
})

// Debounced search with 500ms delay
watch(searchQuery, (newValue) => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  debounceTimeout = setTimeout(() => {
    debouncedSearchQuery.value = newValue
  }, 500)
})

// Filtered routes based on search and filters
const filteredRoutes = computed(() => {
  let filtered = routes.value

  // Apply search filter
  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase()
    filtered = filtered.filter(route => 
      route.routeId?.toString().toLowerCase().includes(query) ||
      route.originPort?.toLowerCase().includes(query) ||
      route.destinationPort?.toLowerCase().includes(query)
    )
  }

  // Apply transportation mode filter
  if (filterMode.value && filterMode.value !== 'all') {
    filtered = filtered.filter(route => 
      route.transportationMode?.toUpperCase() === filterMode.value
    )
  }

  // Apply status filter
  if (filterStatus.value && filterStatus.value !== 'all') {
    filtered = filtered.filter(route => 
      route.status === filterStatus.value
    )
  }

  return filtered
})

const stats = computed(() => {
  const total = routes.value.length
  const avgDuration = routes.value.length > 0 
    ? Math.round(routes.value.reduce((sum, route) => sum + (route.duration || 0), 0) / routes.value.length)
    : 0
  const totalCost = routes.value.reduce((sum, route) => sum + (route.cost || 0), 0)
  const totalDistance = routes.value.reduce((sum, route) => sum + (route.distance || 0), 0)
  
  return {
    total,
    avgDuration,
    totalCost,
    totalDistance
  }
})

const clearFilters = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
  filterMode.value = 'all'
  filterStatus.value = 'all'
}

const loadRoutes = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await routeApi.getAll()
    routes.value = data || []
  } catch (err) {
    error.value = err.message || 'Failed to load routes'
    console.error('❌ Error loading routes:', err)
  } finally {
    isLoading.value = false
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

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const viewRoute = (route) => {
  router.push(`/routes/${route.routeId}`)
}

const editRoute = (route) => {
  router.push(`/routes/${route.routeId}/edit`)
}

const openDeleteDialog = (route) => {
  deleteDialog.value = {
    isOpen: true,
    isDeleting: false,
    route: route,
    title: 'Delete Route',
    message: `Are you sure you want to delete this route from ${route.originPort} to ${route.destinationPort}? This action cannot be undone.`,
    shipmentCount: 0
  }
}

const closeDeleteDialog = () => {
  deleteDialog.value = {
    isOpen: false,
    isDeleting: false,
    route: null,
    title: '',
    message: '',
    shipmentCount: 0
  }
}

const handleDelete = async () => {
  if (!deleteDialog.value.route) return
  
  deleteDialog.value.isDeleting = true
  
  try {
    await routeApi.delete(deleteDialog.value.route.routeId)
    
    // Success - close dialog and reload routes
    closeDeleteDialog()
    await loadRoutes()
    
    // Optional: Show success toast/notification
    console.log('✅ Route deleted successfully')
    
  } catch (error) {
    console.error('❌ Error deleting route:', error)
    
    // Check if error is about shipment assignments
    const errorMessage = error.message || ''
    
    if (errorMessage.includes('shipment')) {
      // Extract shipment count if available from backend response
      const countMatch = errorMessage.match(/(\d+)\s+shipment/i)
      const shipmentCount = countMatch ? parseInt(countMatch[1]) : 1
      
      // Update dialog to show error state
      deleteDialog.value.title = 'Unable to Delete Route'
      deleteDialog.value.message = errorMessage
      deleteDialog.value.shipmentCount = shipmentCount
    } else {
      // Generic error - close dialog and show alert
      closeDeleteDialog()
      alert(`Failed to delete route: ${errorMessage}`)
    }
  } finally {
    deleteDialog.value.isDeleting = false
  }
}

onMounted(() => {
  loadRoutes()
  
  // Listen for real-time updates
  window.addEventListener('routes-updated', loadRoutes)
})

onUnmounted(() => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  window.removeEventListener('routes-updated', loadRoutes)
})
</script>

<style scoped>
/* Ensure dropdowns appear above map */
:deep(.select-content) {
  z-index: 1000 !important;
}

/* Fix for shadcn-vue Select component dropdowns */
:deep([role="listbox"]) {
  z-index: 1000 !important;
}

/* Ensure the map container has lower z-index */
.h-\[600px\] {
  position: relative;
  z-index: 1;
}
</style>