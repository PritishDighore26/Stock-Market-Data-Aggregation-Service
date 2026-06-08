<template>
  <div class="status-banner" :class="tone">
    <strong>{{ title }}</strong>
    <span>{{ message }}</span>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  tone: {
    type: String,
    default: 'info'
  }
})
</script>

<style scoped>
.status-banner {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0.9rem 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--bg-card);
  font-size: 0.82rem;
}

.status-banner strong {
  font-family: var(--font-display);
  font-size: 0.92rem;
}

.status-banner.info {
  border-color: var(--border-glow);
}

.status-banner.success {
  border-color: rgba(0, 201, 167, 0.45);
  background: rgba(0, 201, 167, 0.08);
}

.status-banner.error {
  border-color: rgba(255, 77, 106, 0.45);
  background: rgba(255, 77, 106, 0.08);
}
</style>
