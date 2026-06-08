<template>
  <footer class="footer">
    <div class="footer-inner">
      <p class="footer-copy">CandleScope maps directly to the Stock Candle Aggregation Service.</p>
      <p class="footer-copy">Endpoint: <code>GET /api/v1/candles</code></p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  border-top: 1px solid var(--border);
  background: rgba(9, 12, 16, 0.85);
}

.footer-inner {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  max-width: 1280px;
  margin: 0 auto;
  padding: 1rem 2rem;
}

.footer-copy {
  color: var(--text-muted);
  font-size: 0.72rem;
  letter-spacing: 0.03em;
}

code {
  color: var(--accent);
  font-family: var(--font-mono);
}

@media (max-width: 768px) {
  .footer-inner {
    flex-direction: column;
    padding: 1rem 1.2rem 1.4rem;
  }
}
</style>
