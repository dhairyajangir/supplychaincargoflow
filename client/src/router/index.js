import { createRouter, createWebHistory } from 'vue-router'

import LandingPage from '../components/LandingPage.vue'
import Dashboard from '../components/Dashboard.vue'
import RouteList from '../components/routes/RouteList.vue'
import RouteForm from '../components/routes/RouteForm.vue'
import RouteDetails from '../components/routes/RouteDetails.vue'
import VendorList from '../components/vendors/VendorList.vue'
import VendorForm from '../components/vendors/VendorForm.vue'
import VendorDetails from '../components/vendors/VendorDetails.vue'
import ShipmentList from '../components/shipments/ShipmentList.vue'
import ShipmentForm from '../components/shipments/ShipmentForm.vue'
import ShipmentDetails from '../components/shipments/ShipmentDetails.vue'
import CargoList from '../components/cargo/CargoList.vue'
import CargoForm from '../components/cargo/CargoForm.vue'
import CargoDetails from '../components/cargo/CargoDetails.vue'
import Deliveries from '../components/Deliveries.vue'
import Login from '../components/Login.vue'
import OAuthCallback from '../components/OAuthCallback.vue'
import Settings from '../components/Settings.vue'
import AdminPanel from '../components/AdminPanel.vue'
import { useAuth } from '../services/auth'
// Create placeholder components for other routes
const Analytics = { template: '<div class="p-8"><h1 class="text-2xl font-bold mb-4">Analytics</h1><p>View analytics and reports.</p></div>' }

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: 'Login',
      requiresAuth: false,
      hideForAuth: true // Hide from authenticated users
    }
  },
  {
    path: '/oauth-callback',
    name: 'OAuthCallback',
    component: OAuthCallback,
    meta: {
      title: 'Completing Sign In',
      requiresAuth: false
    }
  },
  {
    path: '/admin',
    name: 'AdminPanel',
    component: AdminPanel,
    meta: {
      title: 'Admin Panel',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/',
    name: 'LandingPage',
    component: LandingPage,
    meta: {
      title: 'CargoFlow - Supply Chain Management',
      hideNavbar: true // Hide main navbar on landing page since it has its own navigation
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: {
      title: 'Dashboard',
      subtitle: 'Welcome back! Here\'s your supply chain overview.',
      requiresAuth: true
    }
  },
  {
    path: '/cargo',
    name: 'Cargo',
    component: CargoList,
    meta: {
      title: 'Cargo Management',
      subtitle: 'Manage and track your cargo inventory',
      requiresAuth: true
    }
  },
  {
    path: '/cargo/create',
    name: 'CargoCreate',
    component: CargoForm,
    meta: {
      title: 'Create Cargo',
      subtitle: 'Add new cargo to your inventory',
      requiresAuth: true
    }
  },
  {
    path: '/cargo/:id',
    name: 'CargoDetails',
    component: CargoDetails,
    meta: {
      title: 'Cargo Details',
      subtitle: 'View cargo information',
      requiresAuth: true
    }
  },
  {
    path: '/cargo/:id/edit',
    name: 'CargoEdit',
    component: CargoForm,
    meta: {
      title: 'Edit Cargo',
      subtitle: 'Update cargo information',
      requiresAuth: true
    }
  },
  {
    path: '/shipments',
    name: 'Shipments',
    component: ShipmentList,
    meta: {
      title: 'Shipment Tracking',
      subtitle: 'Monitor shipment status and delivery progress',
      requiresAuth: true
    }
  },
  {
    path: '/shipments/create',
    name: 'ShipmentCreate',
    component: ShipmentForm,
    meta: {
      title: 'Create Shipment',
      subtitle: 'Add a new shipment to your supply chain',
      requiresAuth: true
    }
  },
  {
    path: '/shipments/:id',
    name: 'ShipmentDetails',
    component: ShipmentDetails,
    meta: {
      title: 'Shipment Details',
      subtitle: 'View shipment information',
      requiresAuth: true
    }
  },
  {
    path: '/shipments/:id/edit',
    name: 'ShipmentEdit',
    component: ShipmentForm,
    meta: {
      title: 'Edit Shipment',
      subtitle: 'Update shipment information',
      requiresAuth: true
    }
  },
  {
    path: '/routes',
    name: 'Routes',
    component: RouteList,
    meta: {
      title: 'Route Management',
      subtitle: 'Plan and optimize shipping routes',
      requiresAuth: true
    }
  },
  {
    path: '/routes/create',
    name: 'RouteCreate',
    component: RouteForm,
    meta: {
      title: 'Create Route',
      subtitle: 'Set up a new shipping route',
      requiresAuth: true
    }
  },
  {
    path: '/routes/:id',
    name: 'RouteDetails',
    component: RouteDetails,
    meta: {
      title: 'Route Details',
      subtitle: 'View route information',
      requiresAuth: true
    }
  },
  {
    path: '/routes/:id/edit',
    name: 'RouteEdit',
    component: RouteForm,
    meta: {
      title: 'Edit Route',
      subtitle: 'Update route information',
      requiresAuth: true
    }
  },
  {
    path: '/deliveries',
    name: 'Deliveries',
    component: Deliveries,
    meta: {
      title: 'Deliveries',
      subtitle: 'Track your delivery status',
      requiresAuth: true
    }
  },
  {
    path: '/vendors',
    name: 'Vendors',
    component: VendorList,
    meta: {
      title: 'Vendor Management',
      subtitle: 'Manage vendor relationships and partnerships',
      requiresAuth: true
    }
  },
  {
    path: '/vendors/create',
    name: 'VendorCreate',
    component: VendorForm,
    meta: {
      title: 'Create Vendor',
      subtitle: 'Add a new vendor to your supply chain',
      requiresAuth: true
    }
  },
  {
    path: '/vendors/:id',
    name: 'VendorDetails',
    component: VendorDetails,
    meta: {
      title: 'Vendor Details',
      subtitle: 'View vendor information',
      requiresAuth: true
    }
  },
  {
    path: '/vendors/:id/edit',
    name: 'VendorEdit',
    component: VendorForm,
    meta: {
      title: 'Edit Vendor',
      subtitle: 'Update vendor information',
      requiresAuth: true
    }
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: Analytics,
    meta: {
      title: 'Analytics',
      subtitle: 'View performance metrics and insights',
      requiresAuth: true
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: {
      title: 'Settings',
      subtitle: 'Configure your application preferences',
      requiresAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // Scroll behavior for smooth navigation
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// Navigation guards for authentication and tracking
router.beforeEach(async (to, from, next) => {
  // Update document title
  if (to.meta.title) {
    document.title = `${to.meta.title} - CargoFlow`
  }
  
  const auth = useAuth()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const hideForAuth = to.matched.some(record => record.meta.hideForAuth)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  
  // If not authenticated and trying to fetch current user hasn't been done yet
  if (!auth.isAuthenticated.value && !auth.user.value) {
    try {
      // Try to get current user from session (for page refreshes)
      await auth.getCurrentUser()
    } catch (err) {
      // User not authenticated
      console.log('No active session')
    }
  }
  
  // Check if route requires authentication
  if (requiresAuth && !auth.isAuthenticated.value) {
    // Redirect to login page
    next('/login')
  } 
  // Check if route requires admin role
  else if (requiresAdmin && auth.user.value?.role !== 'ADMIN') {
    // Non-admin trying to access admin route
    next('/dashboard')
  }
  // Check if authenticated user trying to access login page
  else if (hideForAuth && auth.isAuthenticated.value) {
    // If admin, redirect to admin panel; otherwise dashboard
    if (auth.user.value?.role === 'ADMIN') {
      next('/admin')
    } else {
      next('/dashboard')
    }
  } 
  // Redirect admin to admin panel if they try to access regular routes
  else if (auth.isAuthenticated.value && auth.user.value?.role === 'ADMIN' && to.path !== '/admin' && to.path !== '/login') {
    next('/admin')
  }
  else {
    next()
  }
})

export default router