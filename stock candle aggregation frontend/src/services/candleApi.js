import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
})

export const TIMEFRAMES = [
  { value: '1m', label: '1 Minute', short: '1m' },
  { value: '5m', label: '5 Minutes', short: '5m' },
  { value: '15m', label: '15 Minutes', short: '15m' },
  { value: '30m', label: '30 Minutes', short: '30m' },
  { value: '1h', label: '1 Hour', short: '1h' },
  { value: '1d', label: '1 Day', short: '1d' }
]

export const COMMON_SYMBOLS = ['RELIANCE', 'TCS']

export const DEFAULT_QUERY = {
  symbol: 'RELIANCE',
  timeframe: '15m',
  start_date: '2026-01-01 09:15:00',
  end_date: '2026-01-01 15:29:00'
}

function normalizeApiError(error) {
  const fallback = 'Unable to reach the candle service.'
  if (error.response?.data?.message) {
    return error.response.data.message
  }
  if (error.message) {
    return error.message
  }
  return fallback
}

export const candleApi = {
  async getCandles(params) {
    try {
      const response = await api.get('/candles', { params })
      return response.data
    } catch (error) {
      throw new Error(normalizeApiError(error))
    }
  },

  async ping() {
    try {
      const response = await axios.get('/test', { timeout: 3000 })
      return response.data
    } catch (error) {
      throw new Error(normalizeApiError(error))
    }
  }
}
