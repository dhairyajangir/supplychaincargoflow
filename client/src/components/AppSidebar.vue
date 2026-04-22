<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/services/auth'
import { useI18n } from 'vue-i18n'

import { 
  Home, 
  Package, 
  Truck, 
  Map, 
  Users,
  BarChart3,
  Settings,
  LogOut,
  PackageCheck
} from "lucide-vue-next"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail,
} from '@/components/ui/sidebar'
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

interface Props {
  side?: "left" | "right"
  variant?: "sidebar" | "floating" | "inset"
  collapsible?: "offcanvas" | "icon" | "none"
}

const props = withDefaults(defineProps<Props>(), {
  variant: "sidebar",
  collapsible: "icon"
})

const route = useRoute()
const router = useRouter()
const auth = useAuth()
const { t } = useI18n()

// Navigation data with translations
const data = computed(() => ({
  navMain: [
    {
      title: t('nav.dashboard'),
      url: "/dashboard",
      icon: Home,
    },
    {
      title: t('nav.cargoManagement'),
      url: "/cargo",
      icon: Package,
    },
    {
      title: t('nav.shipmentTracking'),
      url: "/shipments",
      icon: Truck,
    },
    {
      title: t('nav.deliveries'),
      url: "/deliveries",
      icon: PackageCheck,
    },
    {
      title: t('nav.routeManagement'),
      url: "/routes",
      icon: Map,
    },
    {
      title: t('nav.vendorCoordination'),
      url: "/vendors",
      icon: Users,
    },
  ],
  navTools: [
    {
      title: t('nav.settings'),
      url: "/settings",
      icon: Settings,
    },
  ]
}))

const isActive = (url: string) => {
  return route.path === url
}

const displayName = computed(() => {
  return auth.userName.value || 'User'
})

const displayEmail = computed(() => {
  return auth.userEmail.value || 'user@example.com'
})

const displayInitials = computed(() => {
  return auth.userInitials.value || 'U'
})

const handleLogout = async () => {
  await auth.logout()
  router.push('/login')
}
</script>

<template>
  <Sidebar v-bind="props">
    <SidebarHeader>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton size="lg" as-child>
            <router-link to="/dashboard">
              <div class="flex aspect-square size-8 items-center justify-center rounded-lg bg-white dark:bg-sidebar p-1">
                <img src="@/assets/cargoflow.svg" alt="CargoFlow Logo" class="w-full h-full object-contain" />
              </div>
              <div class="flex flex-col gap-0.5 leading-none">
                <span class="font-semibold">CargoFlow</span>
                <span class="text-xs text-muted-foreground">Supply Chain</span>
              </div>
            </router-link>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarHeader>
    
    <SidebarContent>
      <!-- Navigation Section -->
      <SidebarGroup>
        <SidebarGroupLabel>{{ $t('nav.navigation') }}</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in data.navMain" :key="item.title">
              <SidebarMenuButton 
                as-child 
                :is-active="isActive(item.url)"
                :tooltip="item.title"
              >
                <router-link :to="item.url">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </router-link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <!-- Tools Section -->
      <SidebarGroup>
        <SidebarGroupLabel>{{ $t('nav.tools') }}</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in data.navTools" :key="item.title">
              <SidebarMenuButton 
                as-child 
                :is-active="isActive(item.url)"
                :tooltip="item.title"
              >
                <router-link :to="item.url">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </router-link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>

    <!-- User Footer -->
    <SidebarFooter>
      <SidebarMenu>
        <SidebarMenuItem>
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <SidebarMenuButton size="lg" class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground">
                <Avatar class="h-8 w-8 rounded-lg">
                  <AvatarImage :src="auth.userPicture.value" :alt="displayName" />
                  <AvatarFallback class="rounded-lg">{{ displayInitials }}</AvatarFallback>
                </Avatar>
                <div class="grid flex-1 text-left text-sm leading-tight">
                  <span class="truncate font-semibold">{{ displayName }}</span>
                  <span class="truncate text-xs">{{ displayEmail }}</span>
                </div>
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              class="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
              side="bottom"
              align="end"
              :side-offset="4"
            >
              <DropdownMenuItem @click="handleLogout" class="cursor-pointer">
                <LogOut class="mr-2 h-4 w-4" />
                {{ t('auth.logout') }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
    
    <SidebarRail />
  </Sidebar>
</template>
