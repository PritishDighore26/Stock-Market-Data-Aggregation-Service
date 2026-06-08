<template>
  <section class="page-shell dashboard">
    <div class="hero">
      <div class="hero-copy">
        <span class="eyebrow">Vue frontend for Spring candle aggregation</span>
        <h1 class="section-title">Explore aggregated market candles with live backend queries.</h1>
        <p class="section-copy">
          CandleScope connects to your Spring Boot service, lets you request aggregated candles by
          symbol, timeframe, and range, then visualizes the returned OHLCV data in a trading-style
          workspace.
        </p>
        <div class="hero-actions">
          <router-link class="primary-link" to="/chart">Open Chart Lab</router-link>
        </div>
      </div>

      <div class="hero-metrics panel">
        <div class="metric-card">
          <span class="metric-label">Symbols in dataset</span>
          <strong class="metric-value">2</strong>
          <span class="metric-hint">RELIANCE and TCS</span>
        </div>
        <div class="metric-card">
          <span class="metric-label">Sample window</span>
          <strong class="metric-value">5 days</strong>
          <span class="metric-hint">2026-01-01 to 2026-01-05</span>
        </div>
        <div class="metric-card">
          <span class="metric-label">Timeframes</span>
          <strong class="metric-value">6</strong>
          <span class="metric-hint">1m through 1d</span>
        </div>
      </div>
    </div>

    <div class="overview-grid">
      <article class="overview-card panel">
        <h2>What this frontend covers</h2>
        <ul>
          <li>Query symbol, timeframe, start date, and end date against <code>/api/v1/candles</code>.</li>
          <li>Render aggregated candlesticks with volume using Highcharts Stock.</li>
          <li>Surface backend validation errors and empty ranges in a friendly way.</li>
          <li>Show response metrics so reviewers can verify aggregation output quickly.</li>
        </ul>
      </article>

      <article class="overview-card panel">
        <h2>Backend contract alignment</h2>
        <ul>
          <li>Uses exact parameter names: <code>symbol</code>, <code>timeframe</code>, <code>start_date</code>, <code>end_date</code>.</li>
          <li>Formats dates as <code>yyyy-MM-dd HH:mm:ss</code> before each request.</li>
          <li>Accepts the backend response shape: <code>symbol</code>, <code>timeframe</code>, <code>candles</code>, <code>count</code>.</li>
          <li>Handles backend error payloads containing <code>status</code>, <code>message</code>, and <code>timestamp</code>.</li>
        </ul>
      </article>
    </div>

    <div class="workspace">
      <div class="workspace-heading">
        <div>
          <span class="eyebrow">Live preview</span>
          <h2 class="workspace-title">Default market snapshot</h2>
        </div>
        <p class="section-copy">
          The dashboard auto-loads a RELIANCE 15-minute view for the first trading day in the
          shared CSV so the app has a meaningful landing state.
        </p>
      </div>

      <QueryForm :initial-query="query" :loading="loading" @submit="runQuery" />

      <StatusBanner
        v-if="loading"
        title="Loading"
        message="Fetching aggregated candles from the backend service."
        tone="info"
      />
      <StatusBanner
        v-else-if="error"
        title="Request failed"
        :message="error"
        tone="error"
      />
      <StatusBanner
        v-else-if="response"
        title="Request ready"
        :message="`Loaded ${response.count} candles for ${response.symbol} at ${response.timeframe}.`"
        tone="success"
      />

      <StatsBar
        v-if="response"
        :stats="stats"
        :symbol="response.symbol"
        :timeframe="response.timeframe"
        :count="response.count"
      />

      <div v-if="response" class="result-grid">
        <CandleChart :candles="response.candles" :symbol="response.symbol" :timeframe="response.timeframe" />
        <div class="insight-stack">
          <article class="insight-card panel">
            <h3>Range summary</h3>
            <p>{{ rangeSummary }}</p>
          </article>
          <article class="insight-card panel">
            <h3>Volume summary</h3>
            <p>{{ volumeSummary }}</p>
          </article>
          <article class="insight-card panel">
            <h3>Coverage</h3>
            <p>{{ coverageSummary }}</p>
          </article>
        </div>
      </div>

      <CandleTable v-if="response" :candles="response.candles.slice(0, 12)" />
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import CandleChart from '@/components/CandleChart.vue'
import CandleTable from '@/components/CandleTable.vue'
import QueryForm from '@/components/QueryForm.vue'
import StatsBar from '@/components/StatsBar.vue'
import StatusBanner from '@/components/StatusBanner.vue'
import { candleApi, DEFAULT_QUERY } from '@/services/candleApi'
import { buildStats, formatCompactVolume, formatDateTime, formatPrice } from '@/utils/formatters'

const query = ref({ ...DEFAULT_QUERY })
const response = ref(null)
const loading = ref(false)
const error = ref('')

const stats = computed(() => buildStats(response.value?.candles ?? []))

const rangeSummary = computed(() => {
  if (!stats.value) {
    return ''
  }

  return `High ${formatPrice(stats.value.high)} and low ${formatPrice(stats.value.low)} produced an intrarange move of ${formatPrice(stats.value.range)}.`
})

const volumeSummary = computed(() => {
  if (!stats.value) {
    return ''
  }

  return `The returned candles combine to ${formatCompactVolume(stats.value.totalVol)} volume across the selected interval.`
})

const coverageSummary = computed(() => {
  if (!stats.value) {
    return ''
  }

  return `The response spans from ${formatDateTime(stats.value.firstDate)} to ${formatDateTime(stats.value.lastDate)}.`
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

onMounted(() => {
  runQuery(query.value)
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.9fr);
  gap: 1.5rem;
  align-items: stretch;
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 1.1rem;
  padding-top: 0.35rem;
}

.hero-actions {
  display: flex;
  gap: 0.9rem;
  flex-wrap: wrap;
}

.primary-link,
.secondary-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  padding: 0.8rem 1.2rem;
  border-radius: var(--radius-md);
  text-decoration: none;
  transition: var(--transition);
}

.primary-link {
  background: var(--accent);
  color: #04110f;
  font-family: var(--font-display);
  font-weight: 700;
}

.primary-link:hover {
  filter: brightness(1.08);
  box-shadow: 0 12px 30px var(--accent-glow);
}

.secondary-link {
  border: 1px solid var(--border);
  background: rgba(20, 25, 32, 0.8);
  color: var(--text-primary);
}

.secondary-link:hover {
  border-color: var(--border-glow);
  background: var(--bg-hover);
}

.hero-metrics {
  display: grid;
  gap: 1rem;
  padding: 1.25rem;
}

.metric-card {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  padding: 1rem;
  border-radius: var(--radius-md);
  background: rgba(15, 19, 24, 0.78);
}

.metric-label {
  color: var(--text-muted);
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.metric-value {
  font-family: var(--font-display);
  font-size: 1.8rem;
  letter-spacing: -0.05em;
}

.metric-hint {
  color: var(--text-secondary);
  font-size: 0.82rem;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.25rem;
}

.overview-card {
  padding: 1.25rem;
}

.overview-card h2 {
  font-family: var(--font-display);
  font-size: 1.05rem;
  margin-bottom: 0.8rem;
}

.overview-card ul {
  display: grid;
  gap: 0.7rem;
  padding-left: 1.1rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

.workspace {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.workspace-heading {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 1rem;
  align-items: end;
}

.workspace-title {
  margin-top: 0.7rem;
  font-family: var(--font-display);
  font-size: 1.8rem;
  letter-spacing: -0.04em;
}

.result-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 0.7fr);
  gap: 1rem;
}

.insight-stack {
  display: grid;
  gap: 1rem;
}

.insight-card {
  padding: 1.15rem;
}

.insight-card h3 {
  font-family: var(--font-display);
  font-size: 1rem;
  margin-bottom: 0.55rem;
}

.insight-card p {
  color: var(--text-secondary);
  line-height: 1.7;
}

@media (max-width: 1080px) {
  .hero,
  .workspace-heading,
  .result-grid,
  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
