<template>
  <div ref="wrapperRef" class="relative">
    <Input
      :id="resolvedInputId"
      :model-value="inputValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :required="required"
      :name="name"
      :autocomplete="autocomplete"
      role="combobox"
      :aria-expanded="showDropdown ? 'true' : 'false'"
      :aria-controls="dropdownId"
      aria-autocomplete="list"
      :aria-activedescendant="activeDescendantId"
      @update:modelValue="handleInputUpdate"
      @focus="handleFocus"
      @keydown="handleKeydown"
    />
    <transition name="fade" appear>
      <ul
        v-if="showDropdown"
        :id="dropdownId"
        role="listbox"
        class="absolute z-20 mt-1 w-full rounded-md border border-border bg-popover shadow-md"
      >
        <li
          v-for="(option, index) in filteredSuggestions"
          :id="optionId(index)"
          :key="option"
          role="option"
          :aria-selected="highlightedIndex === index ? 'true' : 'false'"
          class="cursor-pointer px-3 py-2 text-sm text-foreground hover:bg-muted"
          :class="{ 'bg-muted': highlightedIndex === index }"
          @mousedown.prevent="selectSuggestion(option)"
          @mouseenter="highlightedIndex = index"
        >
          {{ option }}
        </li>
        <li v-if="filteredSuggestions.length === 0" class="px-3 py-2 text-sm text-muted-foreground">
          {{ emptyStateText }}
        </li>
      </ul>
    </transition>
  </div>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, ref, watch } from 'vue'
import { Input } from '@/components/ui/input'
import { getAllLocationNames } from '@/services/coordinateProvider'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  name: {
    type: String,
    default: undefined
  },
  autocomplete: {
    type: String,
    default: 'off'
  },
  inputId: {
    type: String,
    default: undefined
  },
  minChars: {
    type: Number,
    default: 1
  },
  maxSuggestions: {
    type: Number,
    default: 8
  },
  emptyStateText: {
    type: String,
    default: 'No matches found'
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const wrapperRef = ref(null)
const inputValue = ref(props.modelValue || '')
const isOpen = ref(false)
const highlightedIndex = ref(-1)
const internalId = `location-autocomplete-${Math.random().toString(36).slice(2)}`

const resolvedInputId = computed(() => props.inputId || `${internalId}-input`)
const dropdownId = computed(() => `${internalId}-listbox`)
const activeDescendantId = computed(() => {
  if (highlightedIndex.value < 0) return undefined
  return optionId(highlightedIndex.value)
})

const locationOptions = getAllLocationNames()

const allLocations = computed(() => locationOptions)

const filteredSuggestions = computed(() => {
  const term = inputValue.value.trim().toLowerCase()
  if (term.length < props.minChars) {
    return []
  }

  return allLocations.value
    .filter((location) => location.toLowerCase().startsWith(term))
    .slice(0, props.maxSuggestions)
})

const showDropdown = computed(() => isOpen.value && filteredSuggestions.value.length > 0)

const optionId = (index) => `${internalId}-option-${index}`

const openDropdown = () => {
  if (!props.disabled && filteredSuggestions.value.length > 0) {
    isOpen.value = true
  }
}

const closeDropdown = () => {
  isOpen.value = false
  highlightedIndex.value = -1
}

const selectSuggestion = (value) => {
  inputValue.value = value
  emit('update:modelValue', value)
  emit('select', value)
  closeDropdown()
}

const handleInputUpdate = (value) => {
  inputValue.value = value
  emit('update:modelValue', value)
  if (!isOpen.value) {
    openDropdown()
  }
}

const handleFocus = () => {
  if (filteredSuggestions.value.length > 0) {
    isOpen.value = true
  }
}

const handleKeydown = (event) => {
  if (event.key === 'ArrowDown') {
    if (!isOpen.value) {
      openDropdown()
    } else if (filteredSuggestions.value.length > 0) {
      highlightedIndex.value = (highlightedIndex.value + 1) % filteredSuggestions.value.length
    }
    event.preventDefault()
  } else if (event.key === 'ArrowUp') {
    if (!isOpen.value) {
      openDropdown()
    } else if (filteredSuggestions.value.length > 0) {
      highlightedIndex.value =
        highlightedIndex.value <= 0
          ? filteredSuggestions.value.length - 1
          : highlightedIndex.value - 1
    }
    event.preventDefault()
  } else if (event.key === 'Enter') {
    if (showDropdown.value && highlightedIndex.value >= 0) {
      event.preventDefault()
      const option = filteredSuggestions.value[highlightedIndex.value]
      if (option) {
        selectSuggestion(option)
      }
    }
  } else if (event.key === 'Escape') {
    closeDropdown()
  }
}

const handleClickOutside = (event) => {
  if (!wrapperRef.value) return
  if (!wrapperRef.value.contains(event.target)) {
    closeDropdown()
  }
}

watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue !== inputValue.value) {
      inputValue.value = newValue || ''
    }
  }
)

watch(filteredSuggestions, (suggestions) => {
  if (!isOpen.value) return
  if (suggestions.length === 0) {
    highlightedIndex.value = -1
  } else if (highlightedIndex.value === -1) {
    highlightedIndex.value = 0
  } else if (highlightedIndex.value >= suggestions.length) {
    highlightedIndex.value = suggestions.length - 1
  }
})

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleClickOutside)
})

</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.12s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
