<template>
  <div class="query-form panel">
    <div class="form-grid">
      <div class="field">
        <label class="label" for="symbol">Symbol</label>
        <div class="input-wrap">
          <input
            id="symbol"
            v-model="form.symbol"
            type="text"
            class="input"
            list="symbol-suggestions"
            placeholder="e.g. RELIANCE"
            :class="{ error: errors.symbol }"
            @input="form.symbol = form.symbol.toUpperCase()"
          />
          <datalist id="symbol-suggestions">
            <option v-for="symbol in COMMON_SYMBOLS" :key="symbol" :value="symbol" />
          </datalist>
        </div>
        <span v-if="errors.symbol" class="field-error">{{ errors.symbol }}</span>
      </div>

      <div class="field">
        <label class="label">Timeframe</label>
        <div class="tf-grid">
          <button
            v-for="timeframe in TIMEFRAMES"
            :key="timeframe.value"
            type="button"
            class="tf-btn"
            :class="{ active: form.timeframe === timeframe.value }"
            @click="form.timeframe = timeframe.value"
          >
            {{ timeframe.short }}
          </button>
        </div>
        <span v-if="errors.timeframe" class="field-error">{{ errors.timeframe }}</span>
      </div>

      <div class="field">
        <label class="label" for="startDate">Start Date</label>
        <input
          id="startDate"
          v-model="form.startDate"
          type="datetime-local"
          class="input"
          :class="{ error: errors.startDate }"
        />
        <span v-if="errors.startDate" class="field-error">{{ errors.startDate }}</span>
      </div>

      <div class="field">
        <label class="label" for="endDate">End Date</label>
        <input
          id="endDate"
          v-model="form.endDate"
          type="datetime-local"
          class="input"
          :class="{ error: errors.endDate }"
        />
        <span v-if="errors.endDate" class="field-error">{{ errors.endDate }}</span>
      </div>
    </div>

    <div class="toolbar">
      <div class="presets">
        <span class="presets-label">Quick range</span>
        <button
          v-for="preset in datePresets"
          :key="preset.label"
          type="button"
          class="preset-btn"
          @click="applyPreset(preset)"
        >
          {{ preset.label }}
        </button>
      </div>

      <button type="button" class="submit-btn" :disabled="loading" @click="handleSubmit">
        <span v-if="loading" class="spinner"></span>
        <span v-else>Fetch Candles</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { COMMON_SYMBOLS, TIMEFRAMES } from '@/services/candleApi'
import { toApiDate, toInputDate } from '@/utils/formatters'

const props = defineProps({
  initialQuery: {
    type: Object,
    required: true
  },
  loading: Boolean
})

const emit = defineEmits(['submit'])

const form = reactive({
  symbol: '',
  timeframe: '',
  startDate: '',
  endDate: ''
})

const errors = reactive({
  symbol: '',
  timeframe: '',
  startDate: '',
  endDate: ''
})

const datePresets = [
  {
    label: 'Opening Hour',
    start: '2026-01-01T09:15',
    end: '2026-01-01T10:15'
  },
  {
    label: 'Morning Session',
    start: '2026-01-01T09:15',
    end: '2026-01-01T12:00'
  },
  {
    label: 'Full Day',
    start: '2026-01-01T09:15',
    end: '2026-01-01T15:29'
  }
]

function syncFromProps() {
  form.symbol = props.initialQuery.symbol
  form.timeframe = props.initialQuery.timeframe
  form.startDate = toInputDate(props.initialQuery.start_date)
  form.endDate = toInputDate(props.initialQuery.end_date)
}

watch(
  () => props.initialQuery,
  () => {
    syncFromProps()
  },
  { deep: true, immediate: true }
)

function applyPreset(preset) {
  form.startDate = preset.start
  form.endDate = preset.end
}

function validate() {
  let valid = true
  errors.symbol = ''
  errors.timeframe = ''
  errors.startDate = ''
  errors.endDate = ''

  if (!form.symbol.trim()) {
    errors.symbol = 'Symbol is required.'
    valid = false
  }
  if (!form.timeframe) {
    errors.timeframe = 'Select a timeframe.'
    valid = false
  }
  if (!form.startDate) {
    errors.startDate = 'Start date is required.'
    valid = false
  }
  if (!form.endDate) {
    errors.endDate = 'End date is required.'
    valid = false
  }
  if (form.startDate && form.endDate && form.startDate >= form.endDate) {
    errors.endDate = 'End date must be after start date.'
    valid = false
  }

  return valid
}

function handleSubmit() {
  if (!validate()) {
    return
  }

  emit('submit', {
    symbol: form.symbol.trim().toUpperCase(),
    timeframe: form.timeframe,
    start_date: toApiDate(form.startDate),
    end_date: toApiDate(form.endDate)
  })
}
</script>

<style scoped>
.query-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1.5rem;
  animation: fadeUp 0.3s ease;
}

.form-grid {
  display: grid;
  grid-template-columns: 1.2fr 1.6fr 1fr 1fr;
  gap: 1.25rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.label {
  color: var(--text-secondary);
  font-size: 0.68rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.input {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-surface);
  color: var(--text-primary);
  padding: 0.8rem 0.9rem;
  outline: none;
  transition: border-color var(--transition), box-shadow var(--transition);
  color-scheme: dark;
}

.input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-dim);
}

.input.error {
  border-color: var(--red);
}

.field-error {
  color: var(--red);
  font-size: 0.72rem;
}

.tf-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.tf-btn {
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-surface);
  color: var(--text-secondary);
  padding: 0.65rem 0.85rem;
  cursor: pointer;
  transition: var(--transition);
}

.tf-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.tf-btn.active {
  border-color: var(--accent);
  background: var(--accent-dim);
  color: var(--accent);
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.presets {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.presets-label {
  color: var(--text-muted);
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.preset-btn {
  border: 1px solid var(--border);
  border-radius: 999px;
  background: transparent;
  color: var(--text-secondary);
  padding: 0.4rem 0.8rem;
  cursor: pointer;
  transition: var(--transition);
}

.preset-btn:hover {
  border-color: var(--border-glow);
  background: var(--bg-hover);
  color: var(--text-primary);
}

.submit-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 150px;
  border: none;
  border-radius: var(--radius-sm);
  background: var(--accent);
  color: #04110f;
  padding: 0.9rem 1.5rem;
  cursor: pointer;
  font-family: var(--font-display);
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  transition: filter var(--transition), box-shadow var(--transition);
}

.submit-btn:hover:not(:disabled) {
  filter: brightness(1.08);
  box-shadow: 0 10px 30px var(--accent-glow);
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(4, 17, 15, 0.22);
  border-top-color: #04110f;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@media (max-width: 980px) {
  .form-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    align-items: stretch;
  }

  .submit-btn {
    width: 100%;
  }
}
</style>
