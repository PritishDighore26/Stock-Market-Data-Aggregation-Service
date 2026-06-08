<template>
  <section class="page-shell chart-page">
    <div class="heading">
      <div>
        <span class="eyebrow">Interactive analysis workspace</span>
        <h1 class="section-title">Chart Lab</h1>
      </div>
      <p class="section-copy">
        Use the exact backend parameters, compare different aggregation windows, and inspect the
        raw response rows behind each candlestick block.
      </p>
    </div>

    <QueryForm :initial-query="query" :loading="loading" @submit="runQuery" />

    <StatusBanner
      v-if="loading"
      title="Loading"
      message="Request in progress. Waiting for aggregated candles from the backend."
      tone="info"
    />
    <StatusBanner
      v-else-if="error"
      title="Backend response error"
      :message="error"
      tone="error"
    />

    <template v-if="response">
      <StatsBar
        :stats="stats"
        :symbol="response.symbol"
        :timeframe="response.timeframe"
        :count="response.count"
      />

      <div class="lab-grid">
        <CandleChart :candles="response.candles" :symbol="response.symbol" :timeframe="response.timeframe" />

        <aside class="side-panel panel">
          <div class="side-section">
            <span class="side-label">Request summary</span>
            <p>{{ requestSummary }}</p>
          </div>
          <div class="side-section">
            <span class="side-label">Price move</span>
            <p>{{ moveSummary }}</p>
          </div>
          <div class="side-section">
            <span class="side-label">Backend note</span>
            <p>
              The service aggregates 1-minute candles using first-open, max-high, min-low,
              last-close, and summed volume.
            </p>
          </div>
        </aside>
      </div>

      <CandleTable :candles="response.candles" />
    </template>

    <div v-else-if="!loading" class="empty-state panel">
      <h2>Run a query to inspect candles</h2>
      <p>
        Start with RELIANCE or TCS, then compare 1-minute data against 15-minute or hourly
        aggregations to validate the service behavior.
      </p>
    </div>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue'
import CandleChart from '@/components/CandleChart.vue'
import CandleTable from '@/components/CandleTable.vue'
import QueryForm from '@/components/QueryForm.vue'
import StatsBar from '@/components/StatsBar.vue'
import StatusBanner from '@/components/StatusBanner.vue'
import { candleApi, DEFAULT_QUERY } from '@/services/candleApi'
import { buildStats, formatCompactVolume, formatPrice } from '@/utils/formatters'

const query = ref({ ...DEFAULT_QUERY })
const response = ref(null)
const loading = ref(false)
const error = ref('')

const stats = computed(() => buildStats(response.value?.candles ?? []))

const requestSummary = computed(() => {
  if (!response.value) {
    return ''
  }

  return `${response.value.symbol} returned ${response.value.count} candles at ${response.value.timeframe} granularity.`
})

const moveSummary = computed(() => {
  if (!stats.value) {
    return ''
  }

  return `${stats.value.bullish ? 'Bullish' : 'Bearish'} move of ${formatPrice(stats.value.change)} with ${formatCompactVolume(stats.value.totalVol)} total volume.`
})

async function runQuery(nextQuery) {
  query.value = { ...nextQuery }
  loading.value = true
  error.value = ''

  try {
    response.value = await candleApi.getCandles(nextQuery)
  } catch (requestError) {
    response.value = null
    error.value = requestError.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.chart-page {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.heading {
  display: grid;
  grid-template-columns: minmax(0, 0.8fr) minmax(0, 1fr);
  gap: 1rem;
  align-items: end;
}

.lab-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 0.6fr);
  gap: 1rem;
}

.side-panel {
  display: grid;
  gap: 1rem;
  align-content: start;
  padding: 1rem;
}

.side-section {
  padding: 1rem;
  border-radius: var(--radius-md);
  background: rgba(15, 19, 24, 0.82);
}

.side-label {
  display: inline-block;
  margin-bottom: 0.55rem;
  color: var(--text-muted);
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.side-section p {
  color: var(--text-secondary);
  line-height: 1.65;
}

.empty-state {
  padding: 2rem;
}

.empty-state h2 {
  font-family: var(--font-display);
  margin-bottom: 0.6rem;
}

.empty-state p {
  color: var(--text-secondary);
  line-height: 1.7;
}

@media (max-width: 1080px) {
  .heading,
  .lab-grid {
    grid-template-columns: 1fr;
  }
}
</style>
