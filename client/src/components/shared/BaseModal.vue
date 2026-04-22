<template>
  <!-- 
    The `teleport` tag moves the modal's DOM to the end of the `<body>`.
    This is a best practice to avoid z-index and positioning issues with parent elements.
  -->
  <teleport to="body">
    <!-- 
      Backdrop Transition: Fades in/out the semi-transparent background.
      The `v-if="show"` controls the visibility of the entire modal.
    -->
    <transition name="modal-backdrop-fade">
      <div 
        v-if="show" 
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50 backdrop-blur-sm"
        @click.self="close"
      >
        <!-- 
          Modal Panel Transition: Fades and scales the main modal panel.
          `v-if="show"` is used again to trigger the transition after the backdrop is ready.
        -->
        <transition name="modal-panel-fade">
          <div v-if="show" class="relative bg-white rounded-2xl shadow-xl w-full flex flex-col max-h-[90vh]" :class="maxWidthClass">
            
            <!-- Modal Header -->
            <header class="flex items-center justify-between p-6 border-b border-gray-200">
              <!-- The #header slot allows parent components to inject custom header content -->
              <slot name="header">
                <h2 class="text-xl font-bold text-gray-900">Modal Title</h2>
              </slot>
              <button 
                @click="close" 
                class="p-2 text-gray-400 rounded-full hover:bg-gray-100 hover:text-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
                aria-label="Close modal"
              >
                <X class="w-6 h-6" />
              </button>
            </header>

            <!-- Modal Body -->
            <section class="flex-1 p-6 overflow-y-auto">
              <!-- The #body slot is the main content area for forms or other information -->
              <slot name="body"></slot>
            </section>

            <!-- 
              Modal Footer
              This section only renders if the parent component provides content for the #footer slot.
            -->
            <footer v-if="$slots.footer" class="flex justify-end items-center gap-3 p-6 border-t border-gray-200 bg-gray-50 rounded-b-2xl">
              <slot name="footer"></slot>
            </footer>

          </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { onMounted, onUnmounted, computed } from 'vue';
import { X } from 'lucide-vue-next';

// --- PROPS ---
// Defines the inputs this component accepts from its parent.
const props = defineProps({
  // `show` is a boolean that controls the modal's visibility. It's required.
  show: {
    type: Boolean,
    required: true,
  },
  // `maxWidth` allows customizing the modal's width for different use cases.
  maxWidth: {
    type: String,
    default: 'md', // Default size
  }
});

// --- EMITS ---
// Defines the custom events this component can send to its parent.
const emit = defineEmits(['close']);

// --- METHODS ---
// Closes the modal by emitting a 'close' event for the parent to handle.
const close = () => {
  emit('close');
};

// Closes the modal if the 'Escape' key is pressed.
const handleEscapeKey = (event) => {
  if (event.key === 'Escape' && props.show) {
    close();
  }
};

// --- COMPUTED ---
// Dynamically sets the Tailwind CSS class for the modal's max-width based on the prop.
const maxWidthClass = computed(() => {
  return {
    'sm': 'max-w-sm',
    'md': 'max-w-md',
    'lg': 'max-w-lg',
    'xl': 'max-w-xl',
    '2xl': 'max-w-2xl',
  }[props.maxWidth];
});

// --- LIFECYCLE HOOKS ---
// Adds the 'keydown' event listener when the component is mounted.
onMounted(() => {
  document.addEventListener('keydown', handleEscapeKey);
});

// Cleans up the event listener when the component is unmounted to prevent memory leaks.
onUnmounted(() => {
  document.removeEventListener('keydown', handleEscapeKey);
});
</script>

<style scoped>
/* Transition for the modal backdrop */
.modal-backdrop-fade-enter-active,
.modal-backdrop-fade-leave-active {
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-backdrop-fade-enter-from,
.modal-backdrop-fade-leave-to {
  opacity: 0;
}

/* Transition for the modal panel itself */
.modal-panel-fade-enter-active,
.modal-panel-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-panel-fade-enter-from,
.modal-panel-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}
</style>
