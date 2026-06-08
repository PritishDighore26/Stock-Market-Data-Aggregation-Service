<template>
  <div v-if="stats" class="stats-bar panel">
    <div class="stat-chip chip-featured">
      <span class="chip-label">Symbol</span>
      <span class="chip-value">{{ symbol }}</span>
    </div>
    <div class="stat-chip chip-featured">
      <span class="chip-label">Timeframe</span>
      <span class="chip-value">{{ timeframe }}</span>
    </div>
    <div class="stat-chip chip-gold">
      <span class="chip-label">Candles</span>
      <span class="chip-value">{{ count }}</span>
    </div>
    <div class="divider"></div>
    <div class="stat-chip">
      <span class="chip-label">Open</span>
      <span class="chip-value">{{ formatPrice(stats.openPrice) }}</span>
    </div>
    <div class="stat-chip chip-up">
      <span class="chip-label">High</span>
      <span class="chip-value">{{ formatPrice(stats.high) }}</span>
    </div>
    <div class="stat-chip chip-down">
      <span class="chip-label">Low</span>
      <span class="chip-value">{{ formatPrice(stats.low) }}</span>
    </div>
    <div class="stat-chip">
      <span class="chip-label">Close</span>
      <span class="chip-value">{{ formatPrice(stats.closePrice) }}</span>
    </div>
    <div class="divider"></div>
    <div class="stat-chip" :class="stats.bullish ? 'chip-up' : 'chip-down'">
      <span class="chip-label">Change</span>
      <span class="chip-value">
        {{ stats.bullish ? '+' : '' }}{{ formatPrice(stats.change) }}
        ({{ stats.bullish ? '+' : '' }}{{ stats.changePct.toFixed(2) }}%)
      </span>
    </div>
    <div class="stat-chip">
      <span class="chip-label">Volume</span>
      <span class="chip-value">{{ formatCompactVolume(stats.totalVol) }}</span>
    </div>
  </div>
</template>

<script setup>
import { formatCompactVolume, formatPrice } from '@/utils/formatters'

defineProps({
  stats: Object,
  symbol: String,
  timeframe: String,
  count: Number
})
</script>

<style scoped>
.stats-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.45rem;
  padding: 0.9rem 1rem;
  animation: fadeUp 0.35s ease;
}

.stat-chip {
  display: flex;
  flex-direction: column;
  gap: 0.18rem;
  padding: 0.45rem 0.75rem;
  border-radius: var(--radius-sm);
  background: var(--bg-surface);
}

.chip-label {
  color: var(--text-muted);
  font-size: 0.62rem;
  letter-spacing: 0.09em;
  text-transform: uppercase;
}

.chip-value {
  color: var(--text-primary);
  font-size: 0.82rem;
  font-weight: 500;
}

.chip-featured .chip-value {
  color: var(--accent);
  font-family: var(--font-display);
  font-weight: 700;
}

.chip-gold .chip-value {
  color: var(--gold);
}

.chip-up .chip-value {
  color: var(--accent);
}

.chip-down .chip-value {
  color: var(--red);
}

.divider {
  width: 1px;
  height: 30px;
  background: var(--border);
}

@media (max-width: 640px) {
  .divider {
    display: none;
  }
}
</style>
