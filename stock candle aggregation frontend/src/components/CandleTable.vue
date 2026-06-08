<template>
  <div class="table-card panel">
    <div class="table-header">
      <div>
        <h3>Aggregated candles</h3>
        <p>Each row reflects the backend response after timeframe aggregation.</p>
      </div>
      <span class="count-pill">{{ candles.length }} rows</span>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Date & time</th>
            <th>Open</th>
            <th>High</th>
            <th>Low</th>
            <th>Close</th>
            <th>Volume</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="candle in candles" :key="`${candle.datetime}-${candle.volume}`">
            <td>{{ formatDateTime(candle.datetime) }}</td>
            <td>{{ formatPrice(candle.open) }}</td>
            <td class="price-up">{{ formatPrice(candle.high) }}</td>
            <td class="price-down">{{ formatPrice(candle.low) }}</td>
            <td>{{ formatPrice(candle.close) }}</td>
            <td>{{ formatVolume(candle.volume) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { formatDateTime, formatPrice, formatVolume } from '@/utils/formatters'

defineProps({
  candles: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.table-card {
  overflow: hidden;
  animation: fadeUp 0.45s ease;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.1rem 1.2rem;
  border-bottom: 1px solid var(--border);
}

.table-header h3 {
  font-family: var(--font-display);
  font-size: 1.05rem;
  letter-spacing: -0.03em;
}

.table-header p {
  color: var(--text-secondary);
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

.count-pill {
  border: 1px solid var(--border);
  border-radius: 999px;
  padding: 0.4rem 0.75rem;
  color: var(--text-secondary);
  font-size: 0.72rem;
  white-space: nowrap;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 0.95rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid rgba(31, 42, 53, 0.65);
  white-space: nowrap;
}

th {
  color: var(--text-secondary);
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

td {
  font-size: 0.82rem;
}

tbody tr:hover {
  background: rgba(28, 36, 46, 0.42);
}

.price-up {
  color: var(--accent);
}

.price-down {
  color: var(--red);
}
</style>
