import { createI18n } from 'vue-i18n'
import en from './locales/en'
import es from './locales/es'
import fr from './locales/fr'

// Organize translations by language
const messages = {
  en,
  es,
  fr,
}

// Create i18n instance
const i18n = createI18n({
  legacy: false, // Use Composition API mode
  locale: localStorage.getItem('language') || 'en', // Get saved language or default to English
  fallbackLocale: 'en',
  messages,
  globalInjection: true, // Enable global $t
  missingWarn: false, // Disable missing translation warnings
  fallbackWarn: false,
})

export default i18n
