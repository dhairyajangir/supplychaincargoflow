<template>
  <div class="page-header space-y-6 mb-8">
    <!-- Breadcrumbs -->
    <Breadcrumb v-if="breadcrumbs && breadcrumbs.length > 0">
      <BreadcrumbList>
        <BreadcrumbItem v-for="(crumb, index) in breadcrumbs" :key="index">
          <BreadcrumbLink v-if="crumb.href" :href="crumb.href" class="text-sm">
            {{ crumb.label }}
          </BreadcrumbLink>
          <BreadcrumbPage v-else class="text-sm">
            {{ crumb.label }}
          </BreadcrumbPage>
          <BreadcrumbSeparator v-if="index < breadcrumbs.length - 1" />
        </BreadcrumbItem>
      </BreadcrumbList>
    </Breadcrumb>

    <!-- Header Content -->
    <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
      <!-- Title Section -->
      <div class="flex items-start gap-4">
        <!-- Icon -->
        <div 
          v-if="icon" 
          :class="[
            'flex items-center justify-center rounded-2xl p-4 shadow-sm',
            iconBgColor || 'bg-gradient-to-br from-gray-50 to-gray-100 dark:from-sidebar-accent dark:to-sidebar'
          ]"
        >
          <component 
            :is="icon" 
            :class="[
              'w-7 h-7',
              iconColor || 'text-gray-900 dark:text-sidebar-foreground'
            ]" 
          />
        </div>

        <!-- Title and Description -->
        <div class="flex-1">
          <h1 class="text-4xl font-bold text-gray-900 dark:text-sidebar-foreground tracking-tight leading-tight">
            {{ title }}
          </h1>
          <p v-if="description" class="text-base text-gray-600 dark:text-sidebar-foreground/70 mt-2 leading-relaxed max-w-3xl">
            {{ description }}
          </p>
        </div>
      </div>

      <!-- Actions -->
      <div v-if="$slots.actions" class="flex items-center gap-3 flex-shrink-0">
        <slot name="actions" />
      </div>
    </div>

    <!-- Optional Tabs or Additional Content -->
    <div v-if="$slots.tabs" class="border-b border-gray-200 dark:border-sidebar-border">
      <slot name="tabs" />
    </div>

    <Separator v-if="showSeparator" class="mt-6" />
  </div>
</template>

<script setup>
import { 
  Breadcrumb, 
  BreadcrumbList, 
  BreadcrumbItem, 
  BreadcrumbLink, 
  BreadcrumbPage, 
  BreadcrumbSeparator 
} from '@/components/ui/breadcrumb'
import { Separator } from '@/components/ui/separator'

defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  icon: {
    type: Object,
    default: null
  },
  iconColor: {
    type: String,
    default: ''
  },
  iconBgColor: {
    type: String,
    default: ''
  },
  breadcrumbs: {
    type: Array,
    default: () => []
  },
  showSeparator: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.page-header {
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
