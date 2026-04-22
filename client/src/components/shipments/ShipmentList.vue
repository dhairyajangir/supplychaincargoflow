<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">{{ $t('shipments.title') }}</h1>
          <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">{{ $t('shipments.subtitle') }}</p>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Shipments Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('shipments.totalShipments') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(stats.total) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 15%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.comparedPrevious') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- In Transit Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('shipments.inTransit') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.inTransit }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 8%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.currentlyMoving') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Delivered Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('shipments.delivered') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.delivered }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 10%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.successfullyCompleted') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Pending Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('shipments.pending') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.pending }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 5%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.awaitingProcessing') }}</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Shipments Table -->
      <Card>
        <CardHeader>
          <div class="flex items-center justify-between">
            <div>
              <CardTitle>{{ $t('shipments.allShipments') }}</CardTitle>
              <CardDescription>
                {{ $t('shipments.allShipmentsDesc') }}
              </CardDescription>
            </div>
            <Button @click="$router.push('/shipments/create')">
              <Plus class="mr-2 h-4 w-4" />
              {{ $t('common.newShipment') }}
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <!-- Search and Filter Section -->
          <div class="flex flex-col md:flex-row gap-4 mb-6">
            <!-- Search Input -->
            <div class="relative flex-1">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                v-model="searchQuery"
                type="text"
                :placeholder="$t('shipments.searchPlaceholder')"
                class="pl-10"
              />
            </div>

            <!-- Filter by Status -->
            <Select v-model="filterStatus">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue :placeholder="$t('common.status')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ $t('common.allStatus') }}</SelectItem>
                <SelectItem value="Pending">{{ $t('shipments.pending') }}</SelectItem>
                <SelectItem value="Created">{{ $t('shipments.pending') }}</SelectItem>
                <SelectItem value="Picked Up">{{ $t('shipments.pending') }}</SelectItem>
                <SelectItem value="Shipped">{{ $t('shipments.inTransit') }}</SelectItem>
                <SelectItem value="In Transit">{{ $t('shipments.inTransit') }}</SelectItem>
                <SelectItem value="Delivered">{{ $t('shipments.delivered') }}</SelectItem>
                <SelectItem value="Cancelled">{{ $t('shipments.cancelled') }}</SelectItem>
              </SelectContent>
            </Select>

            <!-- Clear Filters Button -->
            <Button
              v-if="searchQuery || filterStatus !== 'all'"
              @click="clearFilters"
              variant="outline"
              class="w-full md:w-auto"
            >
              <X class="h-4 w-4 mr-2" />
              {{ $t('common.clear') }}
            </Button>
          </div>

          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">{{ $t('shipments.loadingShipments') }}</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadShipments" class="mt-4" variant="outline">
              {{ $t('common.tryAgain') }}
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{{ $t('common.serialNumber') }}</TableHead>
                  <TableHead>{{ $t('common.id') }}</TableHead>
                  <TableHead>{{ $t('common.origin') }} → {{ $t('common.destination') }}</TableHead>
                  <TableHead>{{ $t('common.status') }}</TableHead>
                  <TableHead>{{ $t('common.estDelivery') }}</TableHead>
                  <TableHead>{{ $t('common.route') }}</TableHead>
                  <TableHead>{{ $t('common.vendor') }}</TableHead>
                  <TableHead class="text-right">{{ $t('common.actions') }}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="filteredShipments.length === 0">
                  <TableCell :colspan="8" class="h-24 text-center">
                    <div class="flex flex-col items-center justify-center text-gray-500">
                      <Truck class="h-12 w-12 text-gray-300 mb-2" />
                      <div v-if="searchQuery || filterStatus !== 'all'" class="space-y-2">
                        <p class="font-medium">{{ $t('shipments.noShipmentsMatches') }}</p>
                        <Button @click="clearFilters" variant="outline" size="sm">
                          {{ $t('common.clearFilters') }}
                        </Button>
                      </div>
                      <div v-else>
                        <p class="font-medium">{{ $t('shipments.noShipments') }}</p>
                        <p class="text-sm">{{ $t('shipments.addFirstShipment') }}</p>
                      </div>
                    </div>
                  </TableCell>
                </TableRow>
                <TableRow v-for="(shipment, index) in filteredShipments" :key="shipment.shipmentId">
                  <TableCell class="font-medium">{{ index + 1 }}</TableCell>
                  <TableCell class="font-medium">#{{ shipment.shipmentId }}</TableCell>
                  <TableCell>
                    <div class="flex flex-col">
                      <span class="font-medium">{{ shipment.origin }} → {{ shipment.destination }}</span>
                      <span v-if="shipment.cargoItems && shipment.cargoItems.length > 0" class="text-xs text-gray-500">
                        {{ shipment.cargoItems.length }} cargo item(s)
                      </span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge :variant="getStatusVariant(shipment.status)">
                      {{ shipment.status }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ formatDate(shipment.estimatedDelivery) }}</TableCell>
                  <TableCell>
                    <span v-if="shipment.assignedRoute" class="text-sm">
                      {{ shipment.assignedRoute.originPort }} → {{ shipment.assignedRoute.destinationPort }}
                    </span>
                    <span v-else class="text-gray-400 text-sm">Not assigned</span>
                  </TableCell>
                  <TableCell>
                    <span v-if="shipment.assignedVendor" class="text-sm">
                      {{ shipment.assignedVendor.name }}
                    </span>
                    <span v-else class="text-gray-400 text-sm">Not assigned</span>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewShipment(shipment)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editShipment(shipment)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="confirmDelete(shipment)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
        
        <!-- Shipment Details -->
        <div v-if="deleteDialog.shipment" class="bg-gray-50 dark:bg-sidebar-accent rounded-lg p-4 my-4">
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Shipment ID:</span>
              <span class="font-medium dark:text-sidebar-foreground">#{{ deleteDialog.shipment.shipmentId }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Route:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.shipment.origin }} → {{ deleteDialog.shipment.destination }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Status:</span>
              <Badge :variant="getStatusVariant(deleteDialog.shipment.status)">
                {{ deleteDialog.shipment.status }}
              </Badge>
            </div>
          </div>
        </div>

        <!-- Error Message with Cargo Count -->
        <div v-if="deleteDialog.assignmentCount > 0" class="bg-amber-50 dark:bg-amber-950/50 border border-amber-200 dark:border-amber-800 rounded-lg p-4">
          <div class="flex gap-3">
            <AlertCircle class="h-5 w-5 text-amber-600 dark:text-amber-500 flex-shrink-0 mt-0.5" />
            <div class="flex-1">
              <p class="text-sm font-medium text-amber-800 dark:text-amber-400 mb-1">
                Cannot Delete Shipment
              </p>
              <p class="text-sm text-amber-700 dark:text-amber-300">
                {{ deleteDialog.errorDetails }}
              </p>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button 
            v-if="deleteDialog.assignmentCount > 0"
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
              Delete Shipment
            </Button>
          </template>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
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
  Loader2, 
  AlertCircle, 
  Eye,
  Edit, 
  Trash2,
  Truck,
  Search,
  X
} from 'lucide-vue-next'
import { shipmentApi } from '@/services/api'

const router = useRouter()

const shipments = ref([])
const isLoading = ref(false)
const error = ref(null)

// Delete Dialog State
const deleteDialog = ref({
  isOpen: false,
  isDeleting: false,
  shipment: null,
  title: '',
  message: '',
  assignmentCount: 0,
  errorDetails: ''
})

// Search and filter state
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const filterStatus = ref('all')
let debounceTimeout = null

// Debounced search with 500ms delay
watch(searchQuery, (newValue) => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  debounceTimeout = setTimeout(() => {
    debouncedSearchQuery.value = newValue
  }, 500)
})

// Filtered shipments based on search and filters
const filteredShipments = computed(() => {
  let filtered = shipments.value

  // Apply search filter
  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase()
    filtered = filtered.filter(shipment => 
      shipment.shipmentId?.toString().toLowerCase().includes(query) ||
      shipment.origin?.toLowerCase().includes(query) ||
      shipment.destination?.toLowerCase().includes(query) ||
      shipment.assignedRoute?.originPort?.toLowerCase().includes(query) ||
      shipment.assignedRoute?.destinationPort?.toLowerCase().includes(query) ||
      shipment.assignedVendor?.name?.toLowerCase().includes(query)
    )
  }

  // Apply status filter
  if (filterStatus.value && filterStatus.value !== 'all') {
    filtered = filtered.filter(shipment => 
      shipment.status === filterStatus.value
    )
  }

  return filtered
})

const stats = computed(() => {
  const total = filteredShipments.value.length
  const inTransit = filteredShipments.value.filter(s => 
    s.status === 'In Transit' || s.status === 'Shipped' || s.status === 'Picked Up'
  ).length
  const delivered = filteredShipments.value.filter(s => s.status === 'Delivered').length
  const pending = filteredShipments.value.filter(s => 
    s.status === 'Pending' || s.status === 'Created'
  ).length
  
  return { total, inTransit, delivered, pending }
})

const clearFilters = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
  filterStatus.value = 'all'
}

const loadShipments = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await shipmentApi.getAll()
    shipments.value = data || []
  } catch (err) {
    error.value = err.message || 'Failed to load shipments'
    console.error('❌ Error loading shipments:', err)
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

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

const viewShipment = (shipment) => {
  router.push(`/shipments/${shipment.shipmentId}`)
}

const editShipment = (shipment) => {
  router.push(`/shipments/${shipment.shipmentId}/edit`)
}

const openDeleteDialog = (shipment) => {
  deleteDialog.value = {
    isOpen: true,
    isDeleting: false,
    shipment: shipment,
    title: 'Delete Shipment',
    message: `Are you sure you want to delete shipment #${shipment.shipmentId} (${shipment.origin} → ${shipment.destination})? This action cannot be undone.`,
    assignmentCount: 0,
    errorDetails: ''
  }
}

const closeDeleteDialog = () => {
  deleteDialog.value = {
    isOpen: false,
    isDeleting: false,
    shipment: null,
    title: '',
    message: '',
    assignmentCount: 0,
    errorDetails: ''
  }
}

const handleDelete = async () => {
  if (!deleteDialog.value.shipment) return
  
  deleteDialog.value.isDeleting = true
  
  try {
    await shipmentApi.delete(deleteDialog.value.shipment.shipmentId)
    
    // Success - close dialog and reload shipments
    closeDeleteDialog()
    await loadShipments()
    
    // Optional: Show success toast/notification
    console.log('✅ Shipment deleted successfully')
    
  } catch (error) {
    console.error('❌ Error deleting shipment:', error)
    
    // Check if error is about cargo assignments
    const errorMessage = error.message || ''
    
    if (errorMessage.includes('cargo') || errorMessage.includes('assigned') || errorMessage.includes('items')) {
      // Extract count if available from backend response
      const countMatch = errorMessage.match(/(\d+)\s+cargo/i)
      const assignmentCount = countMatch ? parseInt(countMatch[1]) : 1
      
      // Update dialog to show error state
      deleteDialog.value.title = 'Unable to Delete Shipment'
      deleteDialog.value.assignmentCount = assignmentCount
      deleteDialog.value.errorDetails = errorMessage
    } else {
      // Generic error - close dialog and show alert
      closeDeleteDialog()
      alert(`Failed to delete shipment: ${errorMessage}`)
    }
  } finally {
    deleteDialog.value.isDeleting = false
  }
}

// Keep old function name for backward compatibility but use new dialog
const confirmDelete = (shipment) => {
  openDeleteDialog(shipment)
}

onMounted(() => {
  loadShipments()
  
  // Listen for real-time updates
  window.addEventListener('shipments-updated', loadShipments)
  window.addEventListener('shipment-updated', loadShipments)
})

onUnmounted(() => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  window.removeEventListener('shipments-updated', loadShipments)
  window.removeEventListener('shipment-updated', loadShipments)
})
</script>