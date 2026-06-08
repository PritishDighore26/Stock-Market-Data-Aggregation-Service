export function formatPrice(value) {
  const numeric = Number(value ?? 0)
  return numeric.toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

export function formatVolume(value) {
  const numeric = Number(value ?? 0)
  return numeric.toLocaleString('en-IN')
}

export function formatCompactVolume(value) {
  const numeric = Number(value ?? 0)
  if (numeric >= 1e7) {
    return `${(numeric / 1e7).toFixed(2)}Cr`
  }
  if (numeric >= 1e5) {
    return `${(numeric / 1e5).toFixed(2)}L`
  }
  if (numeric >= 1e3) {
    return `${(numeric / 1e3).toFixed(2)}K`
  }
  return `${numeric}`
}

export function toInputDate(dateTimeString) {
  return dateTimeString.slice(0, 16).replace(' ', 'T')
}

export function toApiDate(dateTimeString) {
  return `${dateTimeString.replace('T', ' ')}:00`
}

export function formatDateTime(dateTimeString) {
  const date = new Date(dateTimeString.replace(' ', 'T'))
  return date.toLocaleString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  })
}

export function buildStats(candles = []) {
  if (!candles.length) {
    return null
  }

  const first = candles[0]
  const last = candles[candles.length - 1]
  const high = Math.max(...candles.map((candle) => Number(candle.high)))
  const low = Math.min(...candles.map((candle) => Number(candle.low)))
  const totalVol = candles.reduce((total, candle) => total + Number(candle.volume), 0)
  const openPrice = Number(first.open)
  const closePrice = Number(last.close)
  const change = closePrice - openPrice
  const changePct = openPrice === 0 ? 0 : (change / openPrice) * 100

  return {
    openPrice,
    closePrice,
    high,
    low,
    totalVol,
    change,
    changePct,
    bullish: change >= 0,
    range: high - low,
    firstDate: first.datetime,
    lastDate: last.datetime
  }
}
