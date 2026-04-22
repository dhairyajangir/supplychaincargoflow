<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">{{ $t('cargo.title') }}</h1>
          <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">{{ $t('cargo.subtitle') }}</p>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Cargo Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('cargo.totalCargo') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(stats.total) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 18%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.comparedPrevious') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Total Weight Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('cargo.totalWeight') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(stats.totalWeight) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.kilograms') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Total Value Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('cargo.totalValue') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">${{ formatNumber(stats.totalValue) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 22%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.usdTotalValue') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Electronics Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('cargo.electronics') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.electronics }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 15%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('common.highValueItems') }}</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Cargo Table -->
      <Card>
        <CardHeader>
          <div class="flex items-center justify-between">
            <div>
              <CardTitle>{{ $t('cargo.allCargo') }}</CardTitle>
              <CardDescription>
                {{ $t('cargo.allCargoDesc') }}
              </CardDescription>
            </div>
            <Button @click="$router.push('/cargo/create')">
              <Plus class="mr-2 h-4 w-4" />
              {{ $t('common.addNewCargo') }}
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
                :placeholder="$t('cargo.searchPlaceholder')"
                class="pl-10"
              />
            </div>

            <!-- Filter by Type -->
            <Select v-model="filterType">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue :placeholder="$t('cargo.cargoType')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ $t('common.allTypes') }}</SelectItem>
                <SelectItem value="Electronics">Electronics</SelectItem>
                <SelectItem value="Clothing">Clothing</SelectItem>
                <SelectItem value="Food">Food</SelectItem>
                <SelectItem value="Machinery">Machinery</SelectItem>
                <SelectItem value="Perishables">Perishables</SelectItem>
                <SelectItem value="Hazardous">Hazardous</SelectItem>
                <SelectItem value="General">General</SelectItem>
              </SelectContent>
            </Select>

            <!-- Clear Filters Button -->
            <Button
              v-if="searchQuery || filterType !== 'all'"
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
            <span class="ml-2">{{ $t('cargo.loadingCargo') }}</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadCargo" class="mt-4" variant="outline">
              {{ $t('common.tryAgain') }}
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{{ $t('common.serialNumber') }}</TableHead>
                  <TableHead>{{ $t('common.type') }}</TableHead>
                  <TableHead>{{ $t('common.weight') }}</TableHead>
                  <TableHead>{{ $t('common.value') }}</TableHead>
                  <TableHead>{{ $t('common.origin') }}</TableHead>
                  <TableHead>{{ $t('common.destination') }}</TableHead>
                  <TableHead>{{ $t('common.shipment') }}</TableHead>
                  <TableHead class="text-right">{{ $t('common.actions') }}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="filteredCargo.length === 0">
                  <TableCell :colspan="8" class="h-24 text-center">
                    <div class="flex flex-col items-center justify-center text-gray-500">
                      <Package class="h-12 w-12 text-gray-300 mb-2" />
                      <div v-if="searchQuery || filterType !== 'all'" class="space-y-2">
                        <p class="font-medium">{{ $t('cargo.noCargoMatches') }}</p>
                        <Button @click="clearFilters" variant="outline" size="sm">
                          {{ $t('common.clearFilters') }}
                        </Button>
                      </div>
                      <div v-else>
                        <p class="font-medium">{{ $t('cargo.noCargo') }}</p>
                        <p class="text-sm">{{ $t('cargo.addFirstCargo') }}</p>
                      </div>
                    </div>
                  </TableCell>
                </TableRow>
                <TableRow v-for="(item, index) in filteredCargo" :key="item.cargoId">
                  <TableCell class="font-medium">{{ index + 1 }}</TableCell>
                  <TableCell>
                    <Badge :variant="getTypeBadgeVariant(item.type)">
                      {{ item.type }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ item.weight ? formatNumber(item.weight) : 'N/A' }}</TableCell>
                  <TableCell>${{ formatNumber(item.value) }}</TableCell>
                  <TableCell>{{ item.shipment?.origin || 'N/A' }}</TableCell>
                  <TableCell>{{ item.shipment?.destination || 'N/A' }}</TableCell>
                  <TableCell>
                    <span v-if="item.shipment" class="text-blue-600 text-sm">
                      #{{ item.shipment.shipmentId }}
                    </span>
                    <span v-else class="text-gray-400 text-sm">Unassigned</span>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewCargo(item)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editCargo(item)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="confirmDelete(item)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
        
        <!-- Cargo Details -->
        <div v-if="deleteDialog.cargo" class="bg-gray-50 dark:bg-sidebar-accent rounded-lg p-4 my-4">
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Cargo ID:</span>
              <span class="font-medium dark:text-sidebar-foreground">#{{ deleteDialog.cargo.cargoId }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Type:</span>
              <Badge :variant="getTypeBadgeVariant(deleteDialog.cargo.type)">
                {{ deleteDialog.cargo.type }}
              </Badge>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Weight:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ formatNumber(deleteDialog.cargo.weight) }} kg</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Value:</span>
              <span class="font-medium dark:text-sidebar-foreground">${{ formatNumber(deleteDialog.cargo.value) }}</span>
            </div>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="deleteDialog.hasError" class="bg-amber-50 dark:bg-amber-950/50 border border-amber-200 dark:border-amber-800 rounded-lg p-4">
          <div class="flex gap-3">
            <AlertCircle class="h-5 w-5 text-amber-600 dark:text-amber-500 flex-shrink-0 mt-0.5" />
            <div class="flex-1">
              <p class="text-sm font-medium text-amber-800 dark:text-amber-400 mb-1">
                Cannot Delete Cargo
              </p>
              <p class="text-sm text-amber-700 dark:text-amber-300">
                {{ deleteDialog.errorDetails }}
              </p>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button 
            v-if="deleteDialog.hasError"
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
              Delete Cargo
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
  Package,
  Search,
  X
} from 'lucide-vue-next'
import { cargoApi } from '@/services/api'

const router = useRouter()

const cargo = ref([])
const isLoading = ref(false)
const error = ref(null)

// Delete Dialog State
const deleteDialog = ref({
  isOpen: false,
  isDeleting: false,
  cargo: null,
  title: '',
  message: '',
  hasError: false,
  errorDetails: ''
})

// Search and filter state
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const filterType = ref('all')
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

// Filtered cargo based on search and filters
const filteredCargo = computed(() => {
  let filtered = cargo.value

  // Apply search filter
  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase()
    filtered = filtered.filter(item => 
      item.cargoId?.toString().toLowerCase().includes(query) ||
      item.type?.toLowerCase().includes(query) ||
      item.shipment?.origin?.toLowerCase().includes(query) ||
      item.shipment?.destination?.toLowerCase().includes(query) ||
      item.shipment?.shipmentId?.toString().toLowerCase().includes(query)
    )
  }

  // Apply type filter
  if (filterType.value && filterType.value !== 'all') {
    filtered = filtered.filter(item => 
      item.type === filterType.value
    )
  }

  return filtered
})

const stats = computed(() => {
  const total = filteredCargo.value.length
  const totalWeight = filteredCargo.value.reduce((sum, item) => sum + (item.weight || 0), 0)
  const totalValue = filteredCargo.value.reduce((sum, item) => sum + (item.value || 0), 0)
  const electronics = filteredCargo.value.filter(item => item.type === 'Electronics').length
  
  return { total, totalWeight, totalValue, electronics }
})

const clearFilters = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
  filterType.value = 'all'
}

const loadCargo = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await cargoApi.getAll()
    cargo.value = data || []
  } catch (err) {
    error.value = err.message || 'Failed to load cargo'
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

const formatNumber = (num) => {
  if (!num && num !== 0) return '0'
  return new Intl.NumberFormat().format(num)
}

const viewCargo = (item) => {
  router.push(`/cargo/${item.cargoId}`)
}

const editCargo = (item) => {
  router.push(`/cargo/${item.cargoId}/edit`)
}

const openDeleteDialog = (item) => {
  deleteDialog.value = {
    isOpen: true,
    isDeleting: false,
    cargo: item,
    title: 'Delete Cargo',
    message: `Are you sure you want to delete cargo "${item.type}" (ID: #${item.cargoId})? This action cannot be undone.`,
    hasError: false,
    errorDetails: ''
  }
}

const closeDeleteDialog = () => {
  deleteDialog.value = {
    isOpen: false,
    isDeleting: false,
    cargo: null,
    title: '',
    message: '',
    hasError: false,
    errorDetails: ''
  }
}

const handleDelete = async () => {
  if (!deleteDialog.value.cargo) return
  
  deleteDialog.value.isDeleting = true
  
  try {
    await cargoApi.remove(deleteDialog.value.cargo.cargoId)
    
    // Success - close dialog and reload cargo
    closeDeleteDialog()
    await loadCargo()
    
    // Optional: Show success toast/notification
    console.log('✅ Cargo deleted successfully')
    
  } catch (error) {
    console.error('❌ Error deleting cargo:', error)
    
    // Check if error is about dependencies
    const errorMessage = error.message || ''
    
    if (errorMessage.includes('shipment') || errorMessage.includes('assigned') || errorMessage.includes('dependency')) {
      // Update dialog to show error state
      deleteDialog.value.title = 'Unable to Delete Cargo'
      deleteDialog.value.hasError = true
      deleteDialog.value.errorDetails = errorMessage
    } else {
      // Generic error - close dialog and show alert
      closeDeleteDialog()
      alert(`Failed to delete cargo: ${errorMessage}`)
    }
  } finally {
    deleteDialog.value.isDeleting = false
  }
}

// Keep old function name for backward compatibility but use new dialog
const confirmDelete = (item) => {
  openDeleteDialog(item)
}

onMounted(() => {
  loadCargo()
  
  // Listen for real-time updates
  window.addEventListener('cargo-updated', loadCargo)
})

onUnmounted(() => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  window.removeEventListener('cargo-updated', loadCargo)
})
</script>