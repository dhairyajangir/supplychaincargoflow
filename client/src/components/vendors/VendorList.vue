<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">{{ $t('vendors.title') }}</h1>
          <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">{{ $t('vendors.subtitle') }}</p>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Vendors Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('vendors.totalVendors') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ formatNumber(stats.total) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('vendors.comparedPrevious') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Active Vendors Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('vendors.activeVendors') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.active }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 8%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('vendors.currentlyOperational') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Logistics Vendors Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('vendors.logistics') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.logistics }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 5%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('vendors.serviceProviders') }}</span>
            </div>
          </CardContent>
        </Card>

        <!-- Transportation Vendors Card -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
          <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
            <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ $t('vendors.transportation') }}</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.transportation }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 3%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ $t('vendors.serviceProviders') }}</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Vendors Table -->
      <Card>
        <CardHeader>
          <div class="flex items-center justify-between">
            <div>
              <CardTitle>{{ $t('vendors.allVendors') }}</CardTitle>
              <CardDescription>
                {{ $t('vendors.allVendorsDesc') }}
              </CardDescription>
            </div>
            <Button @click="$router.push('/vendors/create')">
              <Plus class="mr-2 h-4 w-4" />
              {{ $t('common.addVendor') }}
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
                :placeholder="$t('vendors.searchVendors')"
                class="pl-10"
              />
            </div>

            <!-- Filter by Service Type -->
            <Select v-model="filterServiceType">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue placeholder="Service Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ $t('common.allTypes') }}</SelectItem>
                <SelectItem value="Logistics">Logistics</SelectItem>
                <SelectItem value="Transportation">Transportation</SelectItem>
                <SelectItem value="Warehousing">Warehousing</SelectItem>
                <SelectItem value="Freight">Freight</SelectItem>
              </SelectContent>
            </Select>

            <!-- Clear Filters Button -->
            <Button
              v-if="searchQuery || filterServiceType !== 'all'"
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
            <span class="ml-2">{{ $t('vendors.loadingVendors') }}</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadVendors" class="mt-4" variant="outline">
              {{ $t('common.tryAgain') }}
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{{ $t('common.serialNumber') }}</TableHead>
                  <TableHead>{{ $t('common.name') }}</TableHead>
                  <TableHead>{{ $t('vendors.serviceType') }}</TableHead>
                  <TableHead>{{ $t('vendors.contact_email') }}</TableHead>
                  <TableHead>{{ $t('vendors.contact_phone') }}</TableHead>
                  <TableHead>{{ $t('vendors.address') }}</TableHead>
                  <TableHead class="text-right">{{ $t('common.actions') }}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="filteredVendors.length === 0">
                  <TableCell :colspan="7" class="h-24 text-center">
                    <div v-if="searchQuery || filterServiceType !== 'all'" class="space-y-2">
                      <p>{{ $t('vendors.noVendorsMatches') }}</p>
                      <Button @click="clearFilters" variant="outline" size="sm">
                        {{ $t('common.clearFilters') }}
                      </Button>
                    </div>
                    <p v-else>{{ $t('vendors.noVendors') }}</p>
                  </TableCell>
                </TableRow>
                <TableRow v-for="(vendor, index) in filteredVendors" :key="vendor.vendorId">
                  <TableCell class="font-medium">{{ index + 1 }}</TableCell>
                  <TableCell>{{ vendor.name }}</TableCell>
                  <TableCell>
                    <Badge :variant="getServiceTypeBadgeVariant(vendor.serviceType)">
                      {{ vendor.serviceType }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ vendor.contact_email || 'N/A' }}</TableCell>
                  <TableCell>{{ vendor.contact_phone || 'N/A' }}</TableCell>
                  <TableCell>{{ vendor.address || 'N/A' }}</TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewVendor(vendor)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editVendor(vendor)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="openDeleteDialog(vendor)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
        
        <!-- Vendor Details -->
        <div v-if="deleteDialog.vendor" class="bg-gray-50 dark:bg-sidebar-accent rounded-lg p-4 my-4">
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Vendor ID:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.vendor.vendorId }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Name:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.vendor.name }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600 dark:text-sidebar-foreground/70">Service Type:</span>
              <span class="font-medium dark:text-sidebar-foreground">{{ deleteDialog.vendor.serviceType }}</span>
            </div>
          </div>
        </div>

        <!-- Error Message with Shipment/Route Count -->
        <div v-if="deleteDialog.assignmentCount > 0" class="bg-amber-50 dark:bg-amber-950/50 border border-amber-200 dark:border-amber-800 rounded-lg p-4">
          <div class="flex gap-3">
            <AlertCircle class="h-5 w-5 text-amber-600 dark:text-amber-500 flex-shrink-0 mt-0.5" />
            <div class="flex-1">
              <p class="text-sm font-medium text-amber-800 dark:text-amber-400 mb-1">
                Cannot Delete Vendor
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
              Delete Vendor
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
  Search,
  X 
} from 'lucide-vue-next'
import { vendorApi } from '@/services/api'

const router = useRouter()

const vendors = ref([])
const isLoading = ref(false)
const error = ref(null)

// Delete Dialog State
const deleteDialog = ref({
  isOpen: false,
  isDeleting: false,
  vendor: null,
  title: '',
  message: '',
  assignmentCount: 0,
  errorDetails: ''
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

// Search and filter state
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const filterServiceType = ref('all')
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

// Filtered vendors based on search and filters
const filteredVendors = computed(() => {
  let filtered = vendors.value

  // Apply search filter
  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase()
    filtered = filtered.filter(vendor => 
      vendor.vendorId?.toString().toLowerCase().includes(query) ||
      vendor.name?.toLowerCase().includes(query) ||
      vendor.contact_email?.toLowerCase().includes(query) ||
      vendor.contact_phone?.toLowerCase().includes(query) ||
      vendor.address?.toLowerCase().includes(query)
    )
  }

  // Apply service type filter
  if (filterServiceType.value && filterServiceType.value !== 'all') {
    filtered = filtered.filter(vendor => 
      vendor.serviceType === filterServiceType.value
    )
  }

  return filtered
})

const stats = computed(() => {
  const total = filteredVendors.value.length
  const active = filteredVendors.value.filter(v => v.serviceType !== 'Inactive').length
  const logistics = filteredVendors.value.filter(v => v.serviceType === 'Logistics').length
  const transportation = filteredVendors.value.filter(v => v.serviceType === 'Transportation').length
  
  return { total, active, logistics, transportation }
})

const clearFilters = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
  filterServiceType.value = 'all'
}

const loadVendors = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await vendorApi.getAll()
    
    // Parse contactInfo for each vendor
    vendors.value = (data || []).map(vendor => {
      const contactData = parseContactInfo(vendor.contactInfo)
      return {
        ...vendor,
        contact_email: contactData.email,
        contact_phone: contactData.phone,
        address: contactData.address
      }
    })
  } catch (err) {
    error.value = err.message || 'Failed to load vendors'
    console.error('Error loading vendors:', err)
  } finally {
    isLoading.value = false
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

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const viewVendor = (vendor) => {
  router.push(`/vendors/${vendor.vendorId}`)
}

const editVendor = (vendor) => {
  router.push(`/vendors/${vendor.vendorId}/edit`)
}

const openDeleteDialog = (vendor) => {
  deleteDialog.value = {
    isOpen: true,
    isDeleting: false,
    vendor: vendor,
    title: 'Delete Vendor',
    message: `Are you sure you want to delete vendor "${vendor.name}"? This action cannot be undone.`,
    assignmentCount: 0,
    errorDetails: ''
  }
}

const closeDeleteDialog = () => {
  deleteDialog.value = {
    isOpen: false,
    isDeleting: false,
    vendor: null,
    title: '',
    message: '',
    assignmentCount: 0,
    errorDetails: ''
  }
}

const handleDelete = async () => {
  if (!deleteDialog.value.vendor) return
  
  deleteDialog.value.isDeleting = true
  
  try {
    await vendorApi.delete(deleteDialog.value.vendor.vendorId)
    
    // Success - close dialog and reload vendors
    closeDeleteDialog()
    await loadVendors()
    
    // Optional: Show success toast/notification
    console.log('✅ Vendor deleted successfully')
    
  } catch (error) {
    console.error('❌ Error deleting vendor:', error)
    
    // Check if error is about shipment/route assignments
    const errorMessage = error.message || ''
    
    if (errorMessage.includes('shipment') || errorMessage.includes('route') || errorMessage.includes('assigned')) {
      // Extract count if available from backend response
      const countMatch = errorMessage.match(/(\d+)\s+(shipment|route)/i)
      const assignmentCount = countMatch ? parseInt(countMatch[1]) : 1
      
      // Update dialog to show error state
      deleteDialog.value.title = 'Unable to Delete Vendor'
      deleteDialog.value.assignmentCount = assignmentCount
      deleteDialog.value.errorDetails = errorMessage
    } else {
      // Generic error - close dialog and show alert
      closeDeleteDialog()
      alert(`Failed to delete vendor: ${errorMessage}`)
    }
  } finally {
    deleteDialog.value.isDeleting = false
  }
}

// Keep old function name for backward compatibility but use new dialog
const confirmDelete = (vendor) => {
  openDeleteDialog(vendor)
}

onMounted(() => {
  loadVendors()
  
  // Listen for real-time updates
  window.addEventListener('vendors-updated', loadVendors)
})

onUnmounted(() => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  window.removeEventListener('vendors-updated', loadVendors)
})
</script>