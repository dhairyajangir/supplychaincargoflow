<template>
  <div class="landing-page">
    <!-- Navigation Bar -->
    <header>
      <nav 
        ref="navbar"
        :data-state="menuState && 'active'"
        :class="[
          'fixed top-0 left-0 right-0 z-50 w-full border-b transition-colors duration-150',
          scrolled ? 'bg-white/50 dark:bg-gray-900/50 backdrop-blur-3xl border-gray-200 dark:border-gray-800' : 'bg-white dark:bg-gray-900 border-gray-200 dark:border-gray-800'
        ]"
      >
        <div class="mx-auto max-w-5xl px-6 transition-all duration-300">
          <div class="relative flex flex-wrap items-center justify-between gap-6 py-3 lg:gap-0 lg:py-4">
            <!-- Logo and Mobile Toggle -->
            <div class="flex w-full items-center justify-between gap-12 lg:w-auto">
              <a href="/" class="flex items-center space-x-2">
                <div class="w-8 h-8 rounded-lg bg-white dark:bg-gray-800 flex items-center justify-center p-1">
                  <img src="@/assets/cargoflow.svg" alt="CargoFlow Logo" class="w-full h-full object-contain" />
                </div>
                <span class="text-lg font-semibold text-gray-900 dark:text-gray-100">CargoFlow</span>
              </a>

              <!-- Mobile Menu Toggle -->
              <button
                @click="menuState = !menuState"
                :aria-label="menuState ? 'Close Menu' : 'Open Menu'"
                class="relative z-20 -m-2.5 -mr-4 cursor-pointer p-2.5 lg:hidden"
              >
                <svg 
                  :class="[
                    'm-auto size-6 duration-200',
                    menuState ? 'rotate-180 scale-0 opacity-0' : ''
                  ]"
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
                </svg>
                <svg 
                  :class="[
                    'absolute inset-0 m-auto size-6 -rotate-180 scale-0 opacity-0 duration-200',
                    menuState ? 'rotate-0 scale-100 opacity-100' : ''
                  ]"
                  fill="none" 
                  stroke="currentColor" 
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
              </button>

              <!-- Desktop Navigation Links -->
              <div class="hidden lg:block">
                <ul class="flex gap-8 text-sm">
                  <li v-for="item in menuItems" :key="item.name">
                    <a 
                      :href="item.href" 
                      @click.prevent="scrollToSection(item.href)"
                      class="text-muted-foreground hover:text-accent-foreground block duration-150 text-gray-600 hover:text-gray-900 cursor-pointer"
                    >
                      <span>{{ item.name }}</span>
                    </a>
                  </li>
                </ul>
              </div>
            </div>

            <!-- Mobile Menu & CTA Buttons -->
            <div 
              :class="[
                'bg-background mb-6 hidden w-full flex-wrap items-center justify-end space-y-8 rounded-3xl border p-6 shadow-2xl shadow-zinc-300/20 md:flex-nowrap lg:m-0 lg:flex lg:w-fit lg:gap-6 lg:space-y-0 lg:border-transparent lg:bg-transparent lg:p-0 lg:shadow-none',
                menuState ? 'block lg:flex' : ''
              ]"
            >
              <!-- Mobile Navigation Links -->
              <div class="lg:hidden">
                <ul class="space-y-6 text-base">
                  <li v-for="item in menuItems" :key="item.name">
                    <a 
                      :href="item.href" 
                      @click.prevent="scrollToSection(item.href); menuState = false"
                      class="text-muted-foreground hover:text-accent-foreground block duration-150 text-gray-600 hover:text-gray-900 cursor-pointer"
                    >
                      <span>{{ item.name }}</span>
                    </a>
                  </li>
                </ul>
              </div>

              <!-- Action Buttons -->
              <div class="flex w-full flex-col space-y-3 sm:flex-row sm:gap-3 sm:space-y-0 md:w-fit">
                <button
                  v-if="!auth.isAuthenticated.value"
                  @click="navigateToLogin"
                  class="px-4 py-2 text-sm font-medium border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Login
                </button>
                <button
                  v-if="!auth.isAuthenticated.value"
                  @click="navigateToSignup"
                  class="px-4 py-2 text-sm font-medium text-white bg-gray-900 rounded-lg hover:bg-gray-800 transition-colors"
                >
                  Sign Up
                </button>
                <button
                  v-if="auth.isAuthenticated.value"
                  @click="navigateToDashboard"
                  class="px-4 py-2 text-sm font-medium text-white bg-gray-900 rounded-lg hover:bg-gray-800 transition-colors"
                >
                  Go to Dashboard
                </button>
              </div>
            </div>
          </div>
        </div>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="overflow-hidden">
      <!-- Background Gradient Blobs -->
      <div aria-hidden="true" class="absolute inset-0 isolate hidden lg:block">
        <div class="absolute left-0 top-0 w-[560px] h-[1280px] -translate-y-[350px] -rotate-45 rounded-full bg-[radial-gradient(68.54%_68.72%_at_55.02%_31.46%,hsla(0,0%,85%,.08)_0,hsla(0,0%,55%,.02)_50%,hsla(0,0%,45%,0)_80%)]"></div>
        <div class="absolute left-0 top-0 w-60 h-[1280px] -rotate-45 rounded-full bg-[radial-gradient(50%_50%_at_50%_50%,hsla(0,0%,85%,.06)_0,hsla(0,0%,45%,.02)_80%,transparent_100%)] translate-x-[5%] -translate-y-[50%]"></div>
        <div class="absolute left-0 top-0 w-60 h-[1280px] -translate-y-[350px] -rotate-45 bg-[radial-gradient(50%_50%_at_50%_50%,hsla(0,0%,85%,.04)_0,hsla(0,0%,45%,.02)_80%,transparent_100%)]"></div>
      </div>

      <!-- Hero Section -->
      <section>
        <div class="relative pt-24">
          <div class="absolute inset-0 -z-10 w-full h-full bg-[radial-gradient(125%_125%_at_50%_100%,transparent_0%,white_75%)]"></div>
          <div class="mx-auto max-w-5xl px-6">
            <div class="sm:mx-auto lg:mr-auto lg:mt-0">
              <!-- Hero Title with Animation -->
              <h1 class="mt-8 max-w-2xl text-balance text-5xl font-medium md:text-6xl lg:mt-16 animate-fade-in">
                Streamline Your Supply Chain Operations
              </h1>
              
              <!-- Hero Description -->
              <p class="mt-8 max-w-2xl text-pretty text-lg text-gray-600 animate-fade-in" style="animation-delay: 0.2s;">
                Manage cargo, track shipments, optimize routes, and coordinate with vendors—all in one powerful platform designed for modern logistics.
              </p>

              <!-- CTA Buttons -->
              <div class="mt-12 flex items-center gap-2 animate-fade-in" style="animation-delay: 0.4s;">
                <div class="bg-gray-900/5 rounded-2xl border border-gray-200 p-0.5">
                  <button
                    @click="navigateToDashboard"
                    class="px-5 py-3 text-base font-medium text-white bg-gray-900 rounded-xl hover:bg-gray-800 transition-colors"
                  >
                    <span class="whitespace-nowrap">{{ auth.isAuthenticated.value ? 'Go to Dashboard' : 'Get Started' }}</span>
                  </button>
                </div>
                <button
                  @click="scrollToSection('#features')"
                  class="px-5 py-3 text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 rounded-xl transition-colors"
                >
                  <span class="whitespace-nowrap">View Features</span>
                </button>
              </div>
            </div>
          </div>

          <!-- Dashboard Preview Image -->
          <div class="relative -mr-56 mt-8 overflow-hidden px-2 sm:mr-0 sm:mt-12 md:mt-20 animate-fade-in" style="animation-delay: 0.6s; -webkit-mask-image: linear-gradient(to bottom, black 55%, transparent 100%); mask-image: linear-gradient(to bottom, black 55%, transparent 100%);">
            <div class="relative mx-auto max-w-5xl overflow-hidden rounded-2xl border border-gray-200 shadow-lg shadow-zinc-950/15 bg-white ring-1 ring-gray-100 p-4">
              <!-- Hero Image -->
              <div class="relative rounded-2xl overflow-hidden border border-gray-100">
                <img 
                  src="../assets/hero.png" 
                  alt="CargoFlow Dashboard Preview" 
                  class="w-full h-auto object-cover rounded-2xl aspect-[15/8]"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section id="features" class="py-12 md:py-20">
        <div class="mx-auto max-w-5xl space-y-8 px-6 md:space-y-16">
          <div class="relative z-10 mx-auto max-w-xl space-y-6 text-center md:space-y-12">
            <h2 class="text-balance text-4xl font-medium lg:text-5xl">
              The foundation for modern supply chain management
            </h2>
            <p class="text-lg text-gray-600">
              CargoFlow is evolving to be more than just tracking. It supports an entire ecosystem of tools helping businesses and logistics teams innovate.
            </p>
          </div>

          <div class="relative mx-auto grid max-w-4xl divide-x divide-y border sm:grid-cols-2 lg:grid-cols-3">
            <!-- Feature 1: Fast -->
            <div class="space-y-3 p-12">
              <div class="flex items-center gap-2">
                <Zap class="w-4 h-4" />
                <h3 class="text-sm font-medium">Lightning Fast</h3>
              </div>
              <p class="text-sm text-gray-600">Real-time tracking and instant updates across your entire supply chain network.</p>
            </div>

            <!-- Feature 2: Powerful -->
            <div class="space-y-2 p-12">
              <div class="flex items-center gap-2">
                <Cpu class="w-4 h-4" />
                <h3 class="text-sm font-medium">Powerful Analytics</h3>
              </div>
              <p class="text-sm text-gray-600">Advanced insights and reporting to optimize routes and reduce costs.</p>
            </div>

            <!-- Feature 3: Security -->
            <div class="space-y-2 p-12">
              <div class="flex items-center gap-2">
                <Fingerprint class="w-4 h-4" />
                <h3 class="text-sm font-medium">Secure & Reliable</h3>
              </div>
              <p class="text-sm text-gray-600">Enterprise-grade security protecting your sensitive cargo and shipment data.</p>
            </div>

            <!-- Feature 4: Customization -->
            <div class="space-y-2 p-12">
              <div class="flex items-center gap-2">
                <Pencil class="w-4 h-4" />
                <h3 class="text-sm font-medium">Fully Customizable</h3>
              </div>
              <p class="text-sm text-gray-600">Tailor workflows and dashboards to match your unique business needs.</p>
            </div>

            <!-- Feature 5: Control -->
            <div class="space-y-2 p-12">
              <div class="flex items-center gap-2">
                <Settings2 class="w-4 h-4" />
                <h3 class="text-sm font-medium">Complete Control</h3>
              </div>
              <p class="text-sm text-gray-600">Manage vendors, routes, and shipments from a single unified platform.</p>
            </div>

            <!-- Feature 6: Built for Scale -->
            <div class="space-y-2 p-12">
              <div class="flex items-center gap-2">
                <Sparkles class="w-4 h-4" />
                <h3 class="text-sm font-medium">Built to Scale</h3>
              </div>
              <p class="text-sm text-gray-600">Seamlessly handle growth from small operations to enterprise logistics.</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Content Section -->
      <section id="solutions" class="py-16 md:py-32">
        <div class="mx-auto max-w-5xl space-y-8 px-6 md:space-y-16">
          <h2 class="relative z-10 max-w-xl text-4xl font-medium lg:text-5xl">
            One platform for your entire supply chain.
          </h2>
          <div class="relative">
            <div class="relative z-10 space-y-4 md:w-1/2">
              <p>
                CargoFlow is more than just tracking. <span class="font-medium">It unifies your entire operation</span> — from cargo management to shipment optimization and vendor coordination.
              </p>
              <p>
                Leverage real-time tracking, advanced analytics, and intelligent automation to help your logistics team move faster and scale with confidence.
              </p>

              <div class="grid grid-cols-2 gap-3 pt-6 sm:gap-4">
                <div class="space-y-3">
                  <div class="flex items-center gap-2">
                    <Zap class="w-4 h-4" />
                    <h3 class="text-sm font-medium">Real-Time Updates</h3>
                  </div>
                  <p class="text-gray-600 text-sm">
                    Instant notifications and live tracking across your entire supply chain network.
                  </p>
                </div>
                <div class="space-y-2">
                  <div class="flex items-center gap-2">
                    <Cpu class="w-4 h-4" />
                    <h3 class="text-sm font-medium">Smart Analytics</h3>
                  </div>
                  <p class="text-gray-600 text-sm">
                    AI-powered insights to optimize routes, reduce costs, and improve efficiency.
                  </p>
                </div>
              </div>
            </div>
            <div 
              class="mt-12 h-fit md:absolute md:-inset-y-12 md:inset-x-0 md:mt-0"
              style="-webkit-mask-image: linear-gradient(to left, black 35%, transparent 55%); mask-image: linear-gradient(to left, black 35%, transparent 55%);"
            >
              <div class="relative rounded-2xl border border-dotted border-gray-300 p-2">
                <img
                  src="../assets/charts-light.png"
                  class="rounded-xl shadow-lg"
                  alt="CargoFlow analytics dashboard"
                  loading="lazy"
                />
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- Footer -->
    <footer id="footer" class="border-t bg-white pt-20">
      <div class="mx-auto max-w-5xl px-6">
        <div class="grid gap-12 md:grid-cols-5">
          <div class="md:col-span-2">
            <a href="/" class="flex items-center space-x-2">
              <div class="w-8 h-8 rounded-lg bg-white flex items-center justify-center p-1">
                <img src="@/assets/cargoflow.svg" alt="CargoFlow Logo" class="w-full h-full object-contain" />
              </div>
              <span class="text-lg font-semibold text-gray-900">CargoFlow</span>
            </a>
          </div>

          <div class="grid grid-cols-2 gap-6 sm:grid-cols-4 md:col-span-3">
            <div 
              v-for="linkGroup in footerLinks" 
              :key="linkGroup.group"
              class="space-y-4 text-sm"
            >
              <span class="block font-medium">{{ linkGroup.group }}</span>
              <a
                v-for="item in linkGroup.items"
                :key="item.title"
                :href="item.href"
                :target="item.href.startsWith('http') ? '_blank' : null"
                :rel="item.href.startsWith('http') ? 'noreferrer noopener' : null"
                class="text-gray-600 hover:text-gray-900 block duration-150"
              >
                <span>{{ item.title }}</span>
              </a>
            </div>
          </div>
        </div>
        <div class="mt-12 flex flex-wrap items-end justify-between gap-6 border-t py-6">
          <span class="order-last block text-center text-sm text-gray-600 md:order-first">
            © {{ new Date().getFullYear() }} CargoFlow. All rights reserved
          </span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Zap, Cpu, Fingerprint, Pencil, Settings2, Sparkles } from 'lucide-vue-next'
import { useAuth } from '../services/auth'

// Router setup
const router = useRouter()
const auth = useAuth()

// Reactive state
const menuState = ref(false)
const scrolled = ref(false)
const navbar = ref(null)

// Menu items
const menuItems = ref([
  { name: 'Features', href: '#features' },
  { name: 'Solutions', href: '#solutions' },
  { name: 'Contact', href: '#footer' }
])

// Footer links
const footerLinks = ref([
  {
    group: 'Product',
    items: [
      { title: 'Features', href: '#features' },
      { title: 'Cargo Management', href: '#features' },
      { title: 'Shipment Tracking', href: '#features' },
      { title: 'Route Optimization', href: '#features' },
      { title: 'Vendor Coordination', href: '#features' },
      { title: 'Analytics', href: '#solutions' }
    ]
  },
  {
    group: 'Solutions',
    items: [
      { title: 'For Logistics', href: '#solutions' },
      { title: 'For Freight', href: '#solutions' },
      { title: 'For E-commerce', href: '#solutions' },
      { title: 'For Manufacturing', href: '#solutions' },
      { title: 'For Retail', href: '#solutions' },
      { title: 'Enterprise', href: '#solutions' }
    ]
  },
  {
    group: 'Company',
    items: [
      { title: 'About Us', href: '#solutions' },
      { title: 'Careers', href: '#footer' },
      { title: 'Blog', href: '#footer' },
      { title: 'Partners', href: '#footer' },
      { title: 'Contact', href: '#footer' },
      { title: 'Support', href: '#footer' }
    ]
  },
  {
    group: 'Resources',
    items: [
      { title: 'Documentation', href: 'https://github.com/dhairyajangir/cargoflow/tree/main/docs' },
      { title: 'API Reference', href: 'https://github.com/dhairyajangir/cargoflow/blob/main/docs/lld_api_outline.md' },
      { title: 'Guides', href: 'https://github.com/dhairyajangir/cargoflow/blob/main/docs/backend_workflow.md' },
      { title: 'Privacy Policy', href: 'https://github.com/dhairyajangir/cargoflow/blob/main/docs/non_functional_reqs.md' },
      { title: 'Terms of Service', href: 'https://github.com/dhairyajangir/cargoflow/blob/main/docs/non_functional_reqs.md' },
      { title: 'Security', href: 'https://github.com/dhairyajangir/cargoflow/blob/main/docs/non_functional_reqs.md' }
    ]
  }
])

// Floating particles data
const particles = ref([])

// Features data
const features = ref([
  {
    id: 1,
    title: 'Cargo Management',
    description: 'Track and manage all your cargo with advanced inventory systems',
    iconPath: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    iconBg: 'bg-gradient-to-br from-blue-500 to-blue-600'
  },
  {
    id: 2,
    title: 'Route Optimization',
    description: 'Optimize delivery routes for efficiency and cost reduction',
    iconPath: 'M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7',
    iconBg: 'bg-gradient-to-br from-green-500 to-green-600'
  },
  {
    id: 3,
    title: 'Real-time Tracking',
    description: 'Monitor shipments in real-time with GPS and IoT integration',
    iconPath: 'M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z M15 11a3 3 0 11-6 0 3 3 0 016 0z',
    iconBg: 'bg-gradient-to-br from-orange-500 to-orange-600'
  },
  {
    id: 4,
    title: 'Vendor Coordination',
    description: 'Seamlessly coordinate with vendors and suppliers',
    iconPath: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z',
    iconBg: 'bg-gradient-to-br from-purple-500 to-purple-600'
  }
])

// Workflow steps data
const workflowSteps = ref([
  {
    id: 1,
    title: 'Import & Setup',
    description: 'Import your existing data and configure your supply chain parameters'
  },
  {
    id: 2,
    title: 'Optimize & Plan',
    description: 'Let our AI optimize routes and create efficient delivery plans'
  },
  {
    id: 3,
    title: 'Track & Manage',
    description: 'Monitor shipments in real-time and manage all operations from one dashboard'
  }
])

// Stats data
const stats = ref([
  { value: '500+', label: 'Companies' },
  { value: '50K+', label: 'Shipments Tracked' },
  { value: '99.9%', label: 'Uptime' },
  { value: '24/7', label: 'Support' }
])

// Methods
const navigateToDashboard = () => {
  // Check if user is authenticated
  if (auth.isAuthenticated.value) {
    router.push('/dashboard')
  } else {
    // Redirect to login page if not authenticated
    router.push('/login')
  }
}

const navigateToLogin = () => {
  router.push('/login')
}

const navigateToSignup = () => {
  router.push('/login')
}

const scrollToSection = (sectionId) => {
  const element = document.querySelector(sectionId)
  if (element) {
    const navbarHeight = navbar.value?.offsetHeight || 80
    const elementPosition = element.getBoundingClientRect().top + window.pageYOffset
    const offsetPosition = elementPosition - navbarHeight
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}

const generateParticles = () => {
  particles.value = Array.from({ length: 20 }, (_, i) => ({
    id: i,
    x: Math.random() * 100,
    y: Math.random() * 100,
    delay: Math.random() * 5
  }))
}

let scrollThrottle = null
const handleScroll = () => {
  if (scrollThrottle) return
  scrollThrottle = requestAnimationFrame(() => {
    scrolled.value = window.scrollY > 50
    scrollThrottle = null
  })
}

// Lifecycle hooks
onMounted(() => {
  generateParticles()
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  if (scrollThrottle) cancelAnimationFrame(scrollThrottle)
})
</script>

<style scoped>
/* Smooth scrolling */
html {
  scroll-behavior: smooth;
}

/* Custom animations */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fade-in 1s ease-out forwards;
  opacity: 0;
}

/* Animation classes */
.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

/* Smooth scrolling for anchor links */
html {
  scroll-behavior: smooth;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #3b82f6;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #2563eb;
}
</style>
