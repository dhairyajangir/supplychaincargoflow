    <template>
  <!-- 
    This component is built on top of our reusable BaseModal.
    It passes the `show` prop down and listens for the `close` event, which it treats as a 'cancel' action.
  -->
  <BaseModal :show="show" @close="cancel" max-width="sm">
    <!-- 
      We are not using the #header slot from BaseModal to create a more integrated look 
      for the confirmation dialog, placing the icon and title directly in the body.
    -->
    <template #body>
      <div class="flex items-start">
        <!-- Icon: Changes color based on the 'variant' prop (e.g., 'danger' variant will be red) -->
        <div 
          class="flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full sm:mx-0 sm:h-10 sm:w-10"
          :class="variantClass.iconBg"
        >
          <component :is="AlertTriangle" class="h-6 w-6" :class="variantClass.iconColor" aria-hidden="true" />
        </div>
        <div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
          <!-- Title Prop -->
          <h3 class="text-lg leading-6 font-bold text-gray-900" id="modal-title">
            {{ title }}
          </h3>
          <!-- Message Prop -->
          <div class="mt-2">
            <p class="text-sm text-gray-600">
              {{ message }}
            </p>
          </div>
        </div>
      </div>
    </template>

    <template #footer>
      <!-- Cancel Button -->
      <button 
        type="button" 
        @click="cancel" 
        class="w-full inline-flex justify-center rounded-lg border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:w-auto sm:text-sm transition-colors"
      >
        {{ cancelText }}
      </button>
      <!-- Confirm Button: Changes color based on the 'variant' prop -->
      <button 
        type="button" 
        @click="confirm" 
        class="w-full inline-flex justify-center rounded-lg border border-transparent shadow-sm px-4 py-2 text-base font-medium text-white sm:ml-3 sm:w-auto sm:text-sm transition-colors"
        :class="variantClass.button"
      >
        {{ confirmText }}
      </button>
    </template>
  </BaseModal>
</template>

<script setup>
import { computed } from 'vue';
import { AlertTriangle } from 'lucide-vue-next';
import BaseModal from './BaseModal.vue';

// --- PROPS ---
const props = defineProps({
  show: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    default: 'Are you sure?',
  },
  message: {
    type: String,
    required: true,
  },
  confirmText: {
    type: String,
    default: 'Confirm',
  },
  cancelText: {
    type: String,
    default: 'Cancel',
  },
  // 'variant' controls the color scheme, defaults to 'danger' for destructive actions.
  variant: {
    type: String,
    default: 'danger', // can be 'danger', 'warning', etc.
  },
});

// --- EMITS ---
const emit = defineEmits(['confirm', 'cancel']);

// --- METHODS ---
const confirm = () => {
  emit('confirm');
};

const cancel = () => {
  emit('cancel');
};

// --- COMPUTED ---
// This computed property returns the appropriate Tailwind CSS classes based on the `variant` prop.
// This makes it easy to add other styles (like 'warning' or 'info') in the future.
const variantClass = computed(() => {
  if (props.variant === 'danger') {
    return {
      iconBg: 'bg-red-100',
      iconColor: 'text-red-600',
      button: 'bg-red-600 hover:bg-red-700 focus:ring-2 focus:ring-offset-2 focus:ring-red-500',
    };
  }
  // Default/fallback styles (can be expanded for other variants)
  return {
    iconBg: 'bg-blue-100',
    iconColor: 'text-blue-600',
    button: 'bg-blue-600 hover:bg-blue-700 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500',
  };
});
</script>
