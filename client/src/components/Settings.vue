<script setup>
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useColorMode } from '@vueuse/core'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import Button from '@/components/ui/button/Button.vue'
import ModeToggle from '@/components/shared/ModeToggle.vue'
import { notificationApi } from '@/services/api'

const { locale, t } = useI18n()

// Get color mode for dark mode
const mode = useColorMode()

// Settings state
const notifications = ref(true)
const language = ref(localStorage.getItem('language') || 'en')
const testEmail = ref('')
const emailStatus = ref(null)
const isSendingEmail = ref(false)

// Set initial locale
locale.value = language.value

// Computed property to show current theme
const currentTheme = computed(() => {
  return mode.value.charAt(0).toUpperCase() + mode.value.slice(1)
})

function toggleNotifications() {
  notifications.value = !notifications.value
}

// Watch language changes and update locale
watch(language, (newLang) => {
  locale.value = newLang
  localStorage.setItem('language', newLang)
})

async function sendTestEmail() {
  if (isSendingEmail.value) return

  isSendingEmail.value = true
  emailStatus.value = null

  try {
    const payload = {
      subject: 'Test Notification',
      message: 'This is a test email from the Supply Chain system to verify notification settings.'
    }

    const recipients = testEmail.value
      .split(',')
      .map((email) => email.trim())
      .filter(Boolean)

    if (recipients.length) {
      payload.recipients = recipients
    }

    await notificationApi.sendEmail(payload)
    emailStatus.value = {
      type: 'success',
      message: 'Notification email queued successfully.'
    }
  } catch (error) {
    emailStatus.value = {
      type: 'error',
      message: error.message || 'Failed to send notification email.'
    }
  } finally {
    isSendingEmail.value = false
  }
}
</script>

<template>
  <div class="flex justify-center py-10">
    <Card class="w-full max-w-xl">
      <CardHeader>
        <CardTitle>{{ $t('settings.title') }}</CardTitle>
      </CardHeader>
      <CardContent class="flex flex-col gap-6">
        <!-- Dark Mode / Theme Toggle -->
        <div class="flex items-center justify-between">
          <div>
            <span class="font-medium">{{ $t('settings.darkMode') }}</span>
            <p class="text-sm text-muted-foreground mt-1">
              {{ $t('settings.switchTheme') }}
            </p>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-sm text-muted-foreground">{{ currentTheme }}</span>
            <ModeToggle />
          </div>
        </div>

        <!-- Notifications Toggle -->
        <div class="flex items-center justify-between">
          <div>
            <span class="font-medium">{{ $t('settings.notifications') }}</span>
            <p class="text-sm text-muted-foreground mt-1">
              {{ $t('settings.receiveUpdates') }}
            </p>
          </div>
          <Button @click="toggleNotifications" :variant="notifications ? 'default' : 'outline'">
            {{ notifications ? $t('settings.enabled') : $t('settings.disabled') }}
          </Button>
        </div>

        <!-- Language Selector -->
        <div class="flex items-center justify-between">
          <div>
            <span class="font-medium">{{ $t('settings.language') }}</span>
            <p class="text-sm text-muted-foreground mt-1">
              {{ $t('settings.selectLanguage') }}
            </p>
          </div>
          <select v-model="language" class="border rounded px-3 py-2 bg-background text-foreground border-input focus:ring-2 focus:ring-ring">
            <option value="en">English</option>
            <option value="es">Español</option>
            <option value="fr">Français</option>
          </select>
        </div>
        <div class="flex flex-col gap-2">
          <div class="flex items-center justify-between">
            <span class="font-medium">{{ $t('settings.sendTestNotification') }}</span>
            <Button
              @click="sendTestEmail"
              :variant="isSendingEmail ? 'outline' : 'default'"
              :disabled="isSendingEmail"
            >
              {{ isSendingEmail ? $t('settings.sending') : $t('settings.sendEmail') }}
            </Button>
          </div>
          <p class="text-sm text-muted-foreground">
            {{ $t('settings.emailFieldNote') }}
          </p>
          <input
            v-model="testEmail"
            type="text"
            :placeholder="$t('settings.emailPlaceholder')"
            class="border rounded px-3 py-2 bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
          <p
            v-if="emailStatus"
            :class="['text-sm', emailStatus.type === 'success' ? 'text-green-600' : 'text-red-500']"
          >
            {{ emailStatus.message }}
          </p>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
select:focus,
input:focus {
  outline: 2px solid var(--primary);
}
</style>
