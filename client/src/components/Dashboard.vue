<template>
  <div class="space-y-5 animate-fade-in">
    <!-- Page Header -->
    <PageHeader
      :title="$t('dashboard.title')"
      :description="$t('dashboard.subtitle')"
    />


    <!-- Error State -->
    <div v-if="error" class="bg-red-50 dark:bg-destructive/10 border-l-4 border-red-500 dark:border-destructive p-6 rounded-xl mb-8">
      <div class="flex items-center gap-3">
        <svg class="w-5 h-5 text-red-500 dark:text-destructive-foreground" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
        <span class="text-red-700 dark:text-destructive-foreground font-medium">{{ error }}</span>
        <button 
          @click="loadDashboardData" 
          class="ml-auto px-3 py-1 bg-red-100 dark:bg-sidebar-accent text-red-700 dark:text-sidebar-foreground rounded-md hover:bg-red-200 dark:hover:bg-sidebar-border transition-colors text-sm font-medium"
        >
          {{ $t('common.retry') }}
        </button>
      </div>
    </div>

    <!-- Metrics Grid - Compact & Clean Design -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
      <Card 
        v-for="(metric, index) in metrics" 
        :key="metric.id"
        class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
          <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">{{ metric.label }}</span>
        </div>
        <CardContent class="px-4 py-1.5 pb-3">
          <div class="flex items-center gap-2">
            <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ metric.value }}</div>
            <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">
              {{ metric.change }}
            </span>
          </div>
          <div class="mt-1 flex items-center gap-1.5">
            <component :is="metric.icon" :class="['w-3.5 h-3.5', metric.iconColor]" />
            <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">{{ metric.description }}</span>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Main Content -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-5">
      <!-- Recent Activity -->
      <Card class="lg:col-span-2 rounded-xl border shadow-sm hover:shadow-md transition-shadow">
        <CardHeader class="pb-3">
          <div class="flex items-center justify-between gap-3">
            <CardTitle class="flex items-center gap-2 text-base">
              <Clock class="w-4 h-4 text-gray-600 dark:text-sidebar-foreground/70" />
              {{ $t('dashboard.recentActivity') }}
            </CardTitle>

             <!-- <div class="hidden md:flex items-center gap-1">
              <Button size="sm" :variant="activityFilter === 'all' ? 'default' : 'outline'" @click="activityFilter = 'all'">{{ $t('dashboard.all') }}</Button>
              <Button size="sm" :variant="activityFilter === 'shipments' ? 'default' : 'outline'" @click="activityFilter = 'shipments'">{{ $t('dashboard.shipments') }}</Button>
              <Button size="sm" :variant="activityFilter === 'cargo' ? 'default' : 'outline'" @click="activityFilter = 'cargo'">{{ $t('dashboard.cargo') }}</Button>
              <Button size="sm" :variant="activityFilter === 'routes' ? 'default' : 'outline'" @click="activityFilter = 'routes'">{{ $t('dashboard.routes') }}</Button>
              <Button size="sm" :variant="activityFilter === 'vendors' ? 'default' : 'outline'" @click="activityFilter = 'vendors'">{{ $t('dashboard.vendors') }}</Button>
              <Button size="sm" :variant="activityFilter === 'deliveries' ? 'default' : 'outline'" @click="activityFilter = 'deliveries'">{{ $t('dashboard.deliveries') }}</Button>
            </div> -->
            <Button variant="link" size="sm" class="text-xs h-auto p-0" @click="router.push('/shipments')">
              {{ $t('dashboard.viewAll') }}
            </Button>
          </div>
        </CardHeader>
        <CardContent class="pt-0">
          <!-- Empty state -->
          <div v-if="filteredActivities.length === 0" class="py-10 text-center">
            <div class="mx-auto mb-3 flex h-10 w-10 items-center justify-center rounded-full bg-gray-100 dark:bg-sidebar-accent">
              <Clock class="h-5 w-5 text-gray-500 dark:text-sidebar-foreground/70" />
            </div>
            <p class="text-sm text-gray-600 dark:text-sidebar-foreground/70">{{ $t('dashboard.noRecentActivity') }}</p>
          </div>

          <!-- Activity list -->
          <div v-else class="divide-y divide-gray-100 dark:divide-sidebar-border">
            <div
              v-for="activity in filteredActivities"
              :key="activity.id || activity.timestamp"
              @click="navigateToActivity(activity)"
              class="group relative flex items-start gap-3 p-3 hover:bg-gray-50 dark:hover:bg-sidebar-accent/50 cursor-pointer transition-colors border-l-2"
              :class="getActivityAccent(activity.type)"
            >
              <!-- Icon bubble -->
              <div class="mt-0.5 flex h-9 w-9 items-center justify-center rounded-full bg-gray-100 dark:bg-sidebar-accent ring-1 ring-inset ring-gray-200 dark:ring-sidebar-border">
                <component :is="getActivityIcon(activity.type)" :class="['h-4.5 w-4.5', getActivityIconColor(activity.type)]" />
              </div>
              <!-- Content -->
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between gap-3">
                  <div class="min-w-0">
                    <p class="text-sm text-gray-900 dark:text-sidebar-foreground truncate">
                      {{ formatActivityTitle(activity) }}
                    </p>
                    <p v-if="formatActivitySubtitle(activity)" class="mt-0.5 text-xs text-gray-500 dark:text-sidebar-foreground/60 truncate">
                      {{ formatActivitySubtitle(activity) }}
                    </p>
                  </div>
                  <div class="flex items-center gap-2 shrink-0">
                    <span class="text-[11px] text-gray-500 dark:text-sidebar-foreground/60 whitespace-nowrap">{{ formatRelativeTime(activity.timestamp) }}</span>
                    <Badge v-if="activity.status" :variant="getActivityBadgeVariant(activity.status)" class="text-[10px] px-1.5 py-0">
                      {{ formatStatus(activity.status) }}
                    </Badge>
                    <svg class="h-4 w-4 text-gray-300 dark:text-sidebar-border group-hover:text-gray-400 dark:group-hover:text-sidebar-foreground/50 transition-opacity" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Right Column -->
      <div class="space-y-5">
        <!-- Quick Actions -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow">
          <CardHeader class="pb-3">
            <CardTitle class="text-base">{{ $t('dashboard.quickActions') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-2 pt-0">
            <Button 
              v-for="action in quickActions" 
              :key="action.id"
              @click="handleQuickAction(action.id)"
              variant="outline"
              size="sm"
              class="w-full justify-start gap-2 h-9 hover:bg-sidebar-accent dark:hover:bg-sidebar-accent transition-all duration-200"
            >
              <component :is="action.icon" class="w-4 h-4" />
              <span class="text-sm">{{ action.label }}</span>
            </Button>
          </CardContent>
        </Card>

        <!-- Shipment Status -->
        <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow">
          <CardHeader class="pb-3">
            <CardTitle class="text-base">{{ $t('dashboard.shipmentStatus') }}</CardTitle>
          </CardHeader>
          <CardContent class="pt-0">
            <!-- Status Bar with gaps -->
            <div class="h-8 bg-gray-100 dark:bg-sidebar-accent rounded-lg overflow-hidden mb-3 flex gap-0.5 p-0.5">
              <div 
                v-for="(status, index) in shipmentStatuses" 
                :key="status.status"
                :class="[status.gradientClass, 'rounded transition-all duration-500 hover:scale-y-105 cursor-pointer']"
                :style="{ width: `calc(${status.percentage}% - 2px)`, animationDelay: `${index * 150}ms` }"
              ></div>
            </div>

            <!-- Legend -->
            <div class="space-y-2">
              <div 
                v-for="status in shipmentStatuses" 
                :key="status.status"
                class="flex items-center justify-between text-xs group cursor-pointer"
              >
                <div class="flex items-center gap-2">
                  <div :class="['w-2 h-2 rounded-full shadow-sm transition-transform duration-200 group-hover:scale-125', status.color]"></div>
                  <span class="text-gray-700 dark:text-sidebar-foreground font-medium">{{ status.status }}</span>
                </div>
                <Badge variant="secondary" class="text-[10px] px-1.5 py-0">{{ status.count }}</Badge>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { 
  Plus, Clock, Truck, Package, Route, Users, 
  TrendingUp, TrendingDown, ArrowUp, ArrowDown, Minus,
  CheckCircle, AlertCircle, MapPin, UserPlus, Home
} from 'lucide-vue-next'
import { dashboardApi } from '../services/api.js'
import PageHeader from './shared/PageHeader.vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'

// i18n
const { t } = useI18n()

// Router
const router = useRouter()

// Loading and error states
const isLoading = ref(false)
const error = ref(null)

// Metric values (reactive)
const metricValues = reactive({
  shipments: 0,
  cargo: 0,
  routes: 0,
  vendors: 0
})

// Reactive data with premium styling and translations
const metrics = computed(() => [
  {
    id: 'shipments',
    label: t('dashboard.totalShipments'),
    value: metricValues.shipments,
    change: '↗ 12%',
    description: t('common.inTransitDelivered'),
    icon: Truck,
    iconColor: 'text-[#3B82F6]'
  },
  {
    id: 'cargo',
    label: t('dashboard.activeCargo'),
    value: metricValues.cargo,
    change: '↗ 8%',
    description: t('common.itemsTracked'),
    icon: Package,
    iconColor: 'text-[#10B981]'
  },
  {
    id: 'routes',
    label: t('dashboard.availableRoutes'),
    value: metricValues.routes,
    change: '↗ 5%',
    description: t('common.activeRoutes'),
    icon: Route,
    iconColor: 'text-[#F59E0B]'
  },
  {
    id: 'vendors',
    label: t('dashboard.partnerVendors'),
    value: metricValues.vendors,
    change: '↗ 15%',
    description: t('common.verifiedPartners'),
    icon: Users,
    iconColor: 'text-[#A855F7]'
  }
])

const activities = reactive([])
const activityFilter = ref('all') // all | shipments | cargo | routes | vendors | deliveries

const filteredActivities = computed(() => {
  if (activityFilter.value === 'all') return activities
  
  // Filter based on entity type (check ID properties)
  return activities.filter(a => {
    const filter = activityFilter.value
    
    if (filter === 'shipments') {
      // Check if activity has shipmentId or is shipment-related type
      return a.shipmentId || ['shipment', 'picked-up', 'delivered', 'delayed', 'in-transit', 'created'].includes((a.type || '').toLowerCase())
    }
    if (filter === 'cargo') {
      return a.cargoId || (a.type || '').toLowerCase() === 'cargo'
    }
    if (filter === 'routes') {
      return a.routeId || (a.type || '').toLowerCase() === 'route'
    }
    if (filter === 'vendors') {
      return a.vendorId || (a.type || '').toLowerCase() === 'vendor'
    }
    if (filter === 'deliveries') {
      return a.deliveryId || (a.type || '').toLowerCase() === 'delivery'
    }
    
    return true
  })
})

const quickActions = computed(() => [
  { id: 'new-shipment', label: t('common.newShipment'), icon: Plus },
  { id: 'add-cargo', label: t('common.addNewCargo'), icon: Plus },
  { id: 'create-route', label: t('common.createRoute'), icon: MapPin },
  { id: 'add-vendor', label: t('common.addVendor'), icon: UserPlus }
])

const shipmentStatuses = reactive([])

// API Methods
const loadDashboardData = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    // Load metrics and activities in parallel
    const [metricsData, activitiesData] = await Promise.all([
      dashboardApi.getMetrics(),
      dashboardApi.getRecentActivities()
    ])
    
    // Update metrics
    updateMetrics(metricsData)
    
    // Normalize and update activities
    const normalized = (activitiesData || []).map(a => ({
      ...a,
      type: (a.type || '').toLowerCase(),
      timestamp: a.timestamp || a.time || new Date().toISOString()
    }))
    activities.splice(0, activities.length, ...normalized)
    
    // Update shipment statuses
    updateShipmentStatuses(metricsData.shipmentStatuses || {})
    
  } catch (err) {
    console.error('Failed to load dashboard data:', err)
    error.value = 'Failed to load dashboard data. Please try again.'
  } finally {
    isLoading.value = false
  }
}

const updateMetrics = (data) => {
  // Update metrics values
  const metricsUpdate = {
    shipments: {
      value: data.totalShipments || 0,
      change: `${data.totalShipments || 0} total`
    },
    cargo: {
      value: data.activeCargo || 0,
      change: `${data.activeCargo || 0} items`
    },
    routes: {
      value: data.availableRoutes || 0,
      change: `${data.availableRoutes || 0} available`
    },
    vendors: {
      value: data.partnerVendors || 0,
      change: `${data.partnerVendors || 0} partners`
    }
  }
  
  // Update the reactive metric values
  metricValues.shipments = metricsUpdate.shipments.value
  metricValues.cargo = metricsUpdate.cargo.value
  metricValues.routes = metricsUpdate.routes.value
  metricValues.vendors = metricsUpdate.vendors.value
}

const updateShipmentStatuses = (statusData) => {
  const totalShipments = Object.values(statusData).reduce((sum, count) => sum + count, 0)
  
  // Clear existing statuses
  shipmentStatuses.splice(0, shipmentStatuses.length)
  
  // Map status names to display information
  const statusMapping = {
    'in transit': {
      status: t('shipments.inTransit'),
      color: 'bg-[#14B8A6]',
      gradientClass: 'bg-gradient-to-r from-[#14B8A6] to-[#0D9488]'
    },
    'delivered': {
      status: t('shipments.delivered'),
      color: 'bg-[#10B981]',
      gradientClass: 'bg-gradient-to-r from-[#10B981] to-[#059669]'
    },
    'picked up': {
      status: t('shipments.pending'),
      color: 'bg-[#3B82F6]',
      gradientClass: 'bg-gradient-to-r from-[#3B82F6] to-[#2563EB]'
    },
    'created': {
      status: t('shipments.pending'),
      color: 'bg-[#6B7280]',
      gradientClass: 'bg-gradient-to-r from-[#6B7280] to-[#4B5563]'
    },
    'delayed': {
      status: 'Delayed',
      color: 'bg-[#F59E0B]',
      gradientClass: 'bg-gradient-to-r from-[#F59E0B] to-[#D97706]'
    }
  }
  
  // Add statuses from API data
  Object.entries(statusData).forEach(([status, count]) => {
    const mapping = statusMapping[status.toLowerCase()] || {
      status: status,
      color: 'bg-[#6B7280]',
      gradientClass: 'bg-gradient-to-r from-[#6B7280] to-[#4B5563]'
    }
    
    shipmentStatuses.push({
      ...mapping,
      count: count,
      percentage: totalShipments > 0 ? (count / totalShipments) * 100 : 0
    })
  })
  
  // If no data, add default empty statuses
  if (shipmentStatuses.length === 0) {
    Object.entries(statusMapping).forEach(([key, mapping]) => {
      shipmentStatuses.push({
        ...mapping,
        count: 0,
        percentage: 0
      })
    })
  }
}

// Handle Quick Actions
const handleQuickAction = (actionId) => {
  switch (actionId) {
    case 'new-shipment':
      router.push('/shipments/create')
      break
    case 'add-cargo':
      router.push('/cargo/create')
      break
    case 'create-route':
      router.push('/routes/create')
      break
    case 'add-vendor':
      router.push('/vendors/create')
      break
    default:
      console.warn('Unknown action:', actionId)
  }
}

// Load data on component mount
// Handle entity update events and add local activities
const handleCargoUpdate = (event) => {
  const { action, cargo } = event.detail || {}
  if (cargo) {
    // Add local activity entry
    const newActivity = {
      id: `cargo-${Date.now()}`,
      cargoId: cargo.cargoId,
      type: 'cargo',
      action: `has been ${action}`,
      timestamp: new Date().toISOString(),
      status: action
    }
    activities.unshift(newActivity)
  }
  loadDashboardData()
}

const handleShipmentUpdate = (event) => {
  const { action, shipment } = event.detail || {}
  if (shipment) {
    const newActivity = {
      id: `shipment-${Date.now()}`,
      shipmentId: shipment.shipmentId,
      type: 'shipment',
      action: `has been ${action}`,
      origin: shipment.origin,
      destination: shipment.destination,
      timestamp: new Date().toISOString(),
      status: action
    }
    activities.unshift(newActivity)
  }
  loadDashboardData()
}

const handleRouteUpdate = (event) => {
  const { action, route } = event.detail || {}
  if (route) {
    const newActivity = {
      id: `route-${Date.now()}`,
      routeId: route.routeId,
      type: 'route',
      action: `has been ${action}`,
      timestamp: new Date().toISOString(),
      status: action
    }
    activities.unshift(newActivity)
  }
  loadDashboardData()
}

const handleVendorUpdate = (event) => {
  const { action, vendor } = event.detail || {}
  if (vendor) {
    const newActivity = {
      id: `vendor-${Date.now()}`,
      vendorId: vendor.vendorId,
      type: 'vendor',
      action: `has been ${action}`,
      timestamp: new Date().toISOString(),
      status: action
    }
    activities.unshift(newActivity)
  }
  loadDashboardData()
}

onMounted(() => {
  loadDashboardData()
  
  // Listen for real-time updates from all entities with detailed handlers
  window.addEventListener('cargo-updated', handleCargoUpdate)
  window.addEventListener('shipments-updated', handleShipmentUpdate)
  window.addEventListener('routes-updated', handleRouteUpdate)
  window.addEventListener('vendors-updated', handleVendorUpdate)
  window.addEventListener('deliveries-updated', loadDashboardData)
})

onUnmounted(() => {
  // Clean up event listeners
  window.removeEventListener('cargo-updated', handleCargoUpdate)
  window.removeEventListener('shipments-updated', handleShipmentUpdate)
  window.removeEventListener('routes-updated', handleRouteUpdate)
  window.removeEventListener('vendors-updated', handleVendorUpdate)
  window.removeEventListener('deliveries-updated', loadDashboardData)
})

// Helper functions for premium styling
const getChangeBadgeClass = (type) => {
  switch (type) {
    case 'positive': return 'bg-[#D1FAE5] text-[#065F46]'
    case 'negative': return 'bg-[#FEE2E2] text-[#991B1B]'
    case 'warning': return 'bg-[#FEF3C7] text-[#92400E]'
    default: return 'bg-[#E0E7FF] text-[#3730A3]'
  }
}

const getChangeIcon = (type) => {
  switch (type) {
    case 'positive': return ArrowUp
    case 'negative': return ArrowDown
    case 'warning': return Minus
    default: return ArrowUp
  }
}

const getActivityDotColor = (type) => {
  switch (type) {
    case 'picked-up':
    case 'shipment':
    case 'in-transit': return 'bg-[#3B82F6]'
    case 'cargo': return 'bg-[#10B981]'
    case 'route': return 'bg-[#F59E0B]'
    case 'delivered':
    case 'delivery': return 'bg-[#8B5CF6]'
    case 'delayed': return 'bg-[#EF4444]'
    case 'vendor': return 'bg-[#0EA5E9]'
    default: return 'bg-[#6B7280]'
  }
}

const getActivityIcon = (type) => {
  switch (type) {
    case 'picked-up':
    case 'in-transit':
    case 'shipment': return CheckCircle
    case 'cargo': return Plus
    case 'route': return Route
    case 'delivered':
    case 'delivery': return Package
    case 'delayed': return AlertCircle
    case 'vendor': return Users
    default: return CheckCircle
  }
}

const getActivityIconColor = (type) => {
  switch (type) {
    case 'picked-up':
    case 'shipment':
    case 'in-transit': return 'text-[#3B82F6]'
    case 'cargo': return 'text-[#10B981]'
    case 'route': return 'text-[#F59E0B]'
    case 'delivered':
    case 'delivery': return 'text-[#8B5CF6]'
    case 'delayed': return 'text-[#EF4444]'
    case 'vendor': return 'text-[#0EA5E9]'
    default: return 'text-[#6B7280]'
  }
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'delayed': return 'bg-[#FEE2E2] text-[#991B1B]'
    case 'picked-up': return 'bg-[#DBEAFE] text-[#1E40AF]'
    case 'in-transit': return 'bg-[#E0F2FE] text-[#0C4A6E]'
    default: return 'bg-[#F3F4F6] text-[#374151]'
  }
}

const getBadgeVariant = (type) => {
  switch (type) {
    case 'positive': return 'default'
    case 'negative': return 'destructive'
    case 'warning': return 'secondary'
    default: return 'outline'
  }
}

const getActivityBadgeVariant = (status) => {
  switch (status) {
    case 'delayed': return 'destructive'
    case 'picked-up': return 'default'
    case 'in-transit': return 'secondary'
    case 'delivered': return 'default'
    default: return 'outline'
  }
}

const getMetricBorderColor = (id) => {
  switch (id) {
    case 'shipments': return '!border-t-blue-400'
    case 'cargo': return '!border-t-emerald-400'
    case 'routes': return '!border-t-amber-400'
    case 'vendors': return '!border-t-purple-400'
    default: return '!border-t-gray-300'
  }
}

// Utilities
const formatRelativeTime = (ts) => {
  try {
    // Handle missing or invalid timestamp
    if (!ts) return 'Just now'
    
    // Parse timestamp
    let d
    if (typeof ts === 'string' || typeof ts === 'number') {
      d = new Date(ts)
    } else if (ts instanceof Date) {
      d = ts
    } else {
      return 'Just now'
    }
    
    // Validate the date
    if (isNaN(d.getTime())) {
      return 'Just now'
    }
    
    // Calculate time difference
    const diff = Date.now() - d.getTime()
    
    // Handle future dates (shouldn't happen, but just in case)
    if (diff < 0) return 'Just now'
    
    const sec = Math.round(diff / 1000)
    
    // Return formatted time
    if (sec < 5) return 'Just now'
    if (sec < 60) return `${sec}s ago`
    
    const min = Math.round(sec / 60)
    if (min < 60) return `${min}m ago`
    
    const hr = Math.round(min / 60)
    if (hr < 24) return `${hr}h ago`
    
    const day = Math.round(hr / 24)
    if (day < 7) return `${day}d ago`
    
    const week = Math.round(day / 7)
    if (week < 4) return `${week}w ago`
    
    const month = Math.round(day / 30)
    return `${month}mo ago`
  } catch (e) {
    console.error('Error formatting time:', e, 'timestamp:', ts)
    return 'Just now'
  }
}

const navigateToActivity = (activity) => {
  // If shipmentId exists, it's a shipment activity - use the numeric id
  if (activity.shipmentId && activity.id) {
    return router.push(`/shipments/${activity.id}`)
  }
  
  // If cargoId exists, it's a cargo activity
  if (activity.cargoId && activity.id) {
    return router.push(`/cargo/${activity.id}`)
  }
  
  // If routeId exists, it's a route activity
  if (activity.routeId && activity.id) {
    return router.push(`/routes/${activity.id}`)
  }
  
  // If vendorId exists, it's a vendor activity
  if (activity.vendorId && activity.id) {
    return router.push(`/vendors/${activity.id}`)
  }
  
  // If deliveryId exists, navigate to deliveries
  if (activity.deliveryId) {
    return router.push(`/deliveries`)
  }
  
  // Fallback: check nested objects
  if (activity.shipment?.id) return router.push(`/shipments/${activity.shipment.id}`)
  if (activity.cargo?.id) return router.push(`/cargo/${activity.cargo.id}`)
  if (activity.route?.id) return router.push(`/routes/${activity.route.id}`)
  if (activity.vendor?.id) return router.push(`/vendors/${activity.vendor.id}`)
}

const getActivityAccent = (type) => {
  switch ((type || '').toLowerCase()) {
    case 'shipment':
    case 'picked-up':
    case 'in-transit':
      return 'border-l-blue-300'
    case 'cargo':
      return 'border-l-emerald-300'
    case 'route':
      return 'border-l-amber-300'
    case 'delivery':
      return 'border-l-violet-300'
    case 'vendor':
      return 'border-l-cyan-300'
    default:
      return 'border-l-gray-200'
  }
}

const formatActivityTitle = (a) => {
  // Use friendly shipmentId/cargoId for display (e.g., SH001, C001)
  const idPart = a.shipmentId || a.shipment?.shipmentId
    ? `Shipment #${a.shipmentId || a.shipment?.shipmentId}`
    : a.cargoId || a.cargo?.cargoId
    ? `Cargo #${a.cargoId || a.cargo?.cargoId}`
    : a.routeId || a.route?.routeId
    ? `Route #${a.routeId || a.route?.routeId}`
    : a.vendorId || a.vendor?.vendorId || a.vendor?.name
    ? `Vendor ${a.vendor?.name || '#' + (a.vendorId || a.vendor?.vendorId)}`
    : a.deliveryId || a.delivery?.deliveryId
    ? `Delivery #${a.deliveryId || a.delivery?.deliveryId}`
    : 'Activity'
  const action = a.action || a.message || 'Updated'
  return `${idPart} ${action}`
}

const formatActivitySubtitle = (a) => {
  if (a.details) return a.details
  if (a.origin && a.destination) return `${a.origin} → ${a.destination}`
  if (a.user) return `by ${a.user}`
  return ''
}

const formatStatus = (status) => {
  if (!status) return ''
  const s = status.toString()
  return s.charAt(0).toUpperCase() + s.slice(1)
}
</script>