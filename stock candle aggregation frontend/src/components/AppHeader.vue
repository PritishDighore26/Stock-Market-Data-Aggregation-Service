<template>
  <header class="header">
    <div class="header-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">
          <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
            <rect x="3" y="14" width="3" height="5" rx="1" fill="var(--accent)" />
            <rect x="3" y="10" width="3" height="1" fill="var(--accent)" opacity="0.4" />
            <rect x="9.5" y="8" width="3" height="11" rx="1" fill="var(--accent)" />
            <rect x="9.5" y="4" width="3" height="1" fill="var(--accent)" opacity="0.4" />
            <rect x="16" y="11" width="3" height="8" rx="1" fill="var(--red)" />
            <rect x="16" y="7" width="3" height="1" fill="var(--red)" opacity="0.4" />
          </svg>
        </span>
        <span class="logo-text">CandleScope</span>
      </router-link>

      <nav class="nav">
        <router-link to="/" class="nav-link">Dashboard</router-link>
        <router-link to="/chart" class="nav-link">Chart Lab</router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { candleApi } from '@/services/candleApi'

const apiStatus = ref('unknown')

async function checkApi() {
  try {
    await candleApi.ping()
    apiStatus.value = 'online'
  } catch {
    apiStatus.value = 'offline'
  }
}

onMounted(() => {
  checkApi()
})
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(9, 12, 16, 0.88);
  border-bottom: 1px solid rgba(31, 42, 53, 0.9);
  backdrop-filter: blur(16px);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1280px;
  height: 64px;
  margin: 0 auto;
  padding: 0 2rem;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: var(--text-primary);
}

.logo-icon {
  padding: 6px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-card);
  transition: border-color var(--transition), box-shadow var(--transition);
}

.logo-text {
  font-family: var(--font-display);
  font-size: 1.1rem;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.logo:hover .logo-icon {
  border-color: var(--accent);
  box-shadow: 0 0 12px var(--accent-glow);
}

.nav {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.55rem 0.9rem;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  transition: var(--transition);
}

.nav-link:hover,
.nav-link.router-link-active {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.nav-link.router-link-active {
  color: var(--accent);
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--text-muted);
}

.status-dot.online {
  background: var(--accent);
  animation: pulseGlow 2s infinite;
}

.status-dot.offline {
  background: var(--red);
}

@media (max-width: 768px) {
  .header-inner {
    height: auto;
    flex-direction: column;
    align-items: stretch;
    gap: 0.9rem;
    padding: 1rem 1.2rem;
  }

  .nav {
    justify-content: space-between;
    flex-wrap: wrap;
  }
}
</style>
