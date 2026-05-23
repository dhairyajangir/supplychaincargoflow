<template>
  <div class="w-full min-h-screen flex items-center justify-center px-4 bg-black">
    <!-- Login Card -->
    <Card v-if="activeTab === 'login'" class="mx-auto w-full max-w-sm bg-zinc-900 border-zinc-800">
      <CardHeader>
        <CardTitle class="text-2xl text-white dark:text-white">
          {{ $t('auth.login') }}
        </CardTitle>
        <CardDescription class="text-gray-400">
          Enter your email and password to access your account
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleLogin">
          <div class="grid gap-4">
            <!-- Email Field -->
            <div class="grid gap-2">
              <Label for="login-email" class="text-gray-200 dark:text-gray-200">{{ $t('auth.email') }}</Label>
              <Input
                id="login-email"
                v-model="loginForm.email"
                type="email"
                placeholder="m@example.com"
                required
                class="bg-zinc-800 border-zinc-700 text-white placeholder:text-gray-500"
              />
            </div>

            <!-- Password Field -->
            <div class="grid gap-2">
              <Label for="login-password" class="text-gray-200 dark:text-gray-200">{{ $t('auth.password') }}</Label>
              <Input
                id="login-password"
                v-model="loginForm.password"
                type="password"
                placeholder="••••••••"
                required
                class="bg-zinc-800 border-zinc-700 text-white placeholder:text-gray-500"
              />
            </div>

            <!-- Error Message -->
            <div v-if="error" class="p-3 bg-red-950/50 border border-red-800 rounded-lg text-red-300 text-sm">
              {{ error }}
            </div>

            <!-- Success Message -->
            <div v-if="success" class="p-3 bg-green-950/50 border border-green-800 rounded-lg text-green-300 text-sm">
              {{ success }}
            </div>

            <!-- Submit Button -->
            <Button type="submit" :disabled="loading" class="w-full">
              <span v-if="!loading">{{ $t('auth.login') }}</span>
              <span v-else class="flex items-center justify-center gap-2">
                <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ $t('auth.loggingIn') }}
              </span>
            </Button>

            <!-- Google OAuth Button -->
            <Button 
              variant="outline" 
              type="button"
              @click="loginWithGoogle"
              class="w-full"
            >
              <svg viewBox="-3 0 262 262" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid" class="w-5 h-5 mr-2">
                <path d="M255.878 133.451c0-10.734-.871-18.567-2.756-26.69H130.55v48.448h71.947c-1.45 12.04-9.283 30.172-26.69 42.356l-.244 1.622 38.755 30.023 2.685.268c24.659-22.774 38.875-56.282 38.875-96.027" fill="#4285F4"></path>
                <path d="M130.55 261.1c35.248 0 64.839-11.605 86.453-31.622l-41.196-31.913c-11.024 7.688-25.82 13.055-45.257 13.055-34.523 0-63.824-22.773-74.269-54.25l-1.531.13-40.298 31.187-.527 1.465C35.393 231.798 79.49 261.1 130.55 261.1" fill="#34A853"></path>
                <path d="M56.281 156.37c-2.756-8.123-4.351-16.827-4.351-25.82 0-8.994 1.595-17.697 4.206-25.82l-.073-1.73L15.26 71.312l-1.335.635C5.077 89.644 0 109.517 0 130.55s5.077 40.905 13.925 58.602l42.356-32.782" fill="#FBBC05"></path>
                <path d="M130.55 50.479c24.514 0 41.05 10.589 50.479 19.438l36.844-35.974C195.245 12.91 165.798 0 130.55 0 79.49 0 35.393 29.301 13.925 71.947l42.211 32.783c10.59-31.477 39.891-54.251 74.414-54.251" fill="#EB4335"></path>
              </svg>
              {{ $t('auth.continueWithGoogle') }}
            </Button>
          </div>

          <!-- Switch to Sign Up -->
          <div class="mt-4 text-center text-sm text-gray-400 dark:text-gray-400">
            Don't have an account?
            <button type="button" @click="activeTab = 'signup'" class="underline hover:text-primary text-white dark:text-white">
              {{ $t('auth.signup') }}
            </button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- Sign Up Card -->
    <Card v-if="activeTab === 'signup'" class="mx-auto w-full max-w-sm bg-zinc-900 border-zinc-800 dark:bg-zinc-900 dark:border-zinc-800">
      <CardHeader>
        <CardTitle class="text-2xl text-white dark:text-white">
          {{ $t('auth.signup') }}
        </CardTitle>
        <CardDescription class="text-gray-400">
          Enter your information to create an account
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSignup">
          <div class="grid gap-4">
            <!-- Full Name Field -->
            <div class="grid gap-2">
              <Label for="signup-name" class="text-gray-200 dark:text-gray-200">{{ $t('auth.fullName') }}</Label>
              <Input
                id="signup-name"
                v-model="signupForm.name"
                type="text"
                placeholder="John Doe"
                required
                class="bg-zinc-800 border-zinc-700 text-white placeholder:text-gray-500"
              />
            </div>

            <!-- Email Field -->
            <div class="grid gap-2">
              <Label for="signup-email" class="text-gray-200 dark:text-gray-200">{{ $t('auth.email') }}</Label>
              <Input
                id="signup-email"
                v-model="signupForm.email"
                type="email"
                placeholder="m@example.com"
                required
                class="bg-zinc-800 border-zinc-700 text-white placeholder:text-gray-500 dark:bg-zinc-800 dark:border-zinc-700 dark:text-white"
              />
            </div>

            <!-- Password Field -->
            <div class="grid gap-2">
              <Label for="signup-password" class="text-gray-200 dark:text-gray-200">{{ $t('auth.password') }}</Label>
              <Input
                id="signup-password"
                v-model="signupForm.password"
                type="password"
                placeholder="••••••••"
                minlength="6"
                required
                class="bg-zinc-800 border-zinc-700 text-white placeholder:text-gray-500"
              />
              <p class="text-xs text-gray-400">At least 6 characters</p>
            </div>

            <!-- Error Message -->
            <div v-if="error" class="p-3 bg-red-950/50 border border-red-800 rounded-lg text-red-300 text-sm">
              {{ error }}
            </div>

            <!-- Success Message -->
            <div v-if="success" class="p-3 bg-green-950/50 border border-green-800 rounded-lg text-green-300 text-sm">
              {{ success }}
            </div>

            <!-- Submit Button -->
            <Button type="submit" :disabled="loading" class="w-full">
              <span v-if="!loading">{{ $t('auth.createAccount') }}</span>
              <span v-else class="flex items-center justify-center gap-2">
                <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ $t('auth.creatingAccount') }}
              </span>
            </Button>

            <!-- Google OAuth Button -->
            <Button 
              variant="outline" 
              type="button"
              @click="loginWithGoogle"
              class="w-full"
            >
              <svg viewBox="-3 0 262 262" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid" class="w-5 h-5 mr-2">
                <path d="M255.878 133.451c0-10.734-.871-18.567-2.756-26.69H130.55v48.448h71.947c-1.45 12.04-9.283 30.172-26.69 42.356l-.244 1.622 38.755 30.023 2.685.268c24.659-22.774 38.875-56.282 38.875-96.027" fill="#4285F4"></path>
                <path d="M130.55 261.1c35.248 0 64.839-11.605 86.453-31.622l-41.196-31.913c-11.024 7.688-25.82 13.055-45.257 13.055-34.523 0-63.824-22.773-74.269-54.25l-1.531.13-40.298 31.187-.527 1.465C35.393 231.798 79.49 261.1 130.55 261.1" fill="#34A853"></path>
                <path d="M56.281 156.37c-2.756-8.123-4.351-16.827-4.351-25.82 0-8.994 1.595-17.697 4.206-25.82l-.073-1.73L15.26 71.312l-1.335.635C5.077 89.644 0 109.517 0 130.55s5.077 40.905 13.925 58.602l42.356-32.782" fill="#FBBC05"></path>
                <path d="M130.55 50.479c24.514 0 41.05 10.589 50.479 19.438l36.844-35.974C195.245 12.91 165.798 0 130.55 0 79.49 0 35.393 29.301 13.925 71.947l42.211 32.783c10.59-31.477 39.891-54.251 74.414-54.251" fill="#EB4335"></path>
              </svg>
              {{ $t('auth.continueWithGoogle') }}
            </Button>
          </div>

          <!-- Switch to Sign In -->
          <div class="mt-4 text-center text-sm text-gray-400 dark:text-gray-400">
            Already have an account?
            <button type="button" @click="activeTab = 'login'" class="underline hover:text-primary text-white dark:text-white">
              {{ $t('auth.login') }}
            </button>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../services/auth'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

const router = useRouter()
const route = useRoute()
const auth = useAuth()

const activeTab = ref('login')
const loading = ref(false)
const error = ref('')
const success = ref('')

const loginForm = ref({
  email: '',
  password: ''
})

const signupForm = ref({
  name: '',
  email: '',
  password: ''
})

// Check if user is already authenticated
onMounted(() => {
  if (auth.isAuthenticated.value) {
    router.push('/dashboard')
  }
  
  // Check for OAuth callback success
  if (route.query.oauth === 'success') {
    // Fetch current user after OAuth
    auth.getCurrentUser().then(user => {
      if (user) {
        router.push('/dashboard')
      }
    })
  }
})

const loginWithGoogle = () => {
  auth.loginWithGoogle()
}

const handleLogin = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await auth.login(loginForm.value)
    
    if (response.success) {
      success.value = 'Login successful! Redirecting...'
      
      // Check if user is admin and redirect accordingly
      const user = response.user
      setTimeout(() => {
        if (user?.role === 'ADMIN') {
          router.push('/admin')
        } else {
          router.push('/dashboard')
        }
      }, 500)
    } else if (response.isPending) {
      error.value = 'Your account is pending admin approval. Please wait for approval before logging in.'
    } else {
      error.value = response.message || 'Login failed'
    }
  } catch (err) {
    error.value = err.message || 'An error occurred during login'
  } finally {
    loading.value = false
  }
}

const handleSignup = async () => {
  loading.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await auth.signup(signupForm.value)
    
    if (response.success) {
      if (response.isPending) {
        success.value = 'Account created! Your registration is pending admin approval. You will be able to login once approved.'
      } else {
        success.value = 'Account created successfully! Please login.'
      }
      
      // Store email for convenience
      const email = signupForm.value.email
      
      // Reset form
      signupForm.value = {
        name: '',
        email: '',
        password: ''
      }
      
      // Switch to login tab after 3 seconds
      setTimeout(() => {
        activeTab.value = 'login'
        loginForm.value.email = email
        success.value = ''
      }, 3000)
    } else {
      error.value = response.message || 'Signup failed'
    }
  } catch (err) {
    error.value = err.message || 'An error occurred during signup'
  } finally {
    loading.value = false
  }
}
</script>
