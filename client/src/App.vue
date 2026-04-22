<template>
  <div class="app-container">
    <!-- Public routes without sidebar -->
    <template v-if="!shouldShowSidebar">
      <router-view v-slot="{ Component }">
        <transition 
          name="page-fade" 
          mode="out-in"
          appear
        >
          <component :is="Component" />
        </transition>
      </router-view>
    </template>

    <!-- Authenticated routes with sidebar -->
    <SidebarProvider v-else>
      <AppSidebar />
      <SidebarInset>
        <div class="flex flex-1 flex-col min-h-screen bg-gradient-to-br from-[#F8FAFC] via-[#F1F5F9] to-[#E2E8F0] dark:from-background dark:via-background dark:to-background dark:bg-background">
          <header class="flex h-16 shrink-0 items-center gap-2 border-b bg-white/50 dark:bg-sidebar/80 backdrop-blur-sm px-4 border-gray-200 dark:border-sidebar-border">
            <SidebarTrigger class="-ml-1" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem class="hidden md:block">
                  <BreadcrumbLink :href="currentBreadcrumb.parent?.url || '/dashboard'">
                    {{ currentBreadcrumb.parent?.title || $t('nav.dashboard') }}
                  </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator v-if="currentBreadcrumb.current" class="hidden md:block" />
                <BreadcrumbItem v-if="currentBreadcrumb.current">
                  <BreadcrumbPage>{{ currentBreadcrumb.current }}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
            <div class="ml-auto">
              <ModeToggle />
            </div>
          </header>
          <main class="flex-1 p-6 bg-background">
            <router-view v-slot="{ Component }">
              <transition 
                name="page-fade" 
                mode="out-in"
                appear
              >
                <component :is="Component" />
              </transition>
            </router-view>
          </main>
        </div>
      </SidebarInset>
    </SidebarProvider>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { SidebarProvider, SidebarInset, SidebarTrigger } from './components/ui/sidebar'
import { 
  Breadcrumb, 
  BreadcrumbItem, 
  BreadcrumbLink, 
  BreadcrumbList, 
  BreadcrumbPage, 
  BreadcrumbSeparator 
} from './components/ui/breadcrumb'
import AppSidebar from './components/AppSidebar.vue'
import ModeToggle from './components/shared/ModeToggle.vue'
import { useAuth } from './services/auth'

const route = useRoute()
const auth = useAuth()
const { t } = useI18n()

// Determine if sidebar should be shown
const shouldShowSidebar = computed(() => {
  // Don't show sidebar on landing page, login, oauth callback, or admin panel
  const excludedRoutes = ['/', '/login', '/oauth-callback', '/admin']
  return !excludedRoutes.includes(route.path) && auth.isAuthenticated.value
})

// Breadcrumb logic
const currentBreadcrumb = computed(() => {
  const path = route.path
  const segments = path.split('/').filter(Boolean)
  
  const breadcrumbMap = {
    '/dashboard': { current: t('nav.dashboard') },
    '/cargo': { current: t('nav.cargoManagement') },
    '/shipments': { current: t('nav.shipmentTracking') },
    '/routes': { current: t('nav.routeManagement') },
    '/vendors': { current: t('nav.vendorCoordination') },
    '/deliveries': { current: t('nav.deliveries') },
    '/analytics': { current: t('nav.analytics') },
    '/settings': { current: t('nav.settings') },
  }

  // Handle nested routes
  if (segments.length > 1) {
    const parentPath = `/${segments[0]}`
    const parentTitle = breadcrumbMap[parentPath]?.current || segments[0]
    return {
      parent: { title: parentTitle, url: parentPath },
      current: segments[1] === 'create' ? t('common.create') + ' ' + t('common.new') : t('common.details')
    }
  }

  return breadcrumbMap[path] || { current: t('common.page') || 'Page' }
})

// Watch for route changes and scroll to top
watch(route, () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');

* {
  box-sizing: border-box;
}

body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-container {
  min-height: 100vh;
  position: relative;
}

.main-content {
  position: relative;
  z-index: 1;
}

.content-wrapper {
  padding: 2rem;
  max-width: 1920px;
  margin: 0 auto;
}

/* Page transitions */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .content-wrapper {
    padding: 1.5rem;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 1rem;
  }
  
  .main-content {
    margin-left: 0 !important;
  }
}

/* Custom scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.dark ::-webkit-scrollbar-track {
  background: var(--sidebar);
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.dark ::-webkit-scrollbar-thumb {
  background: var(--border);
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: var(--ring);
}

/* Smooth transitions for interactive elements */
button, a, input, select, textarea {
  transition: all 0.2s ease-in-out;
}

/* Focus visible styles for accessibility */
*:focus-visible {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* Toast animation */
@keyframes slide-up {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.animate-slide-up {
  animation: slide-up 0.3s ease-out;
}
</style>
