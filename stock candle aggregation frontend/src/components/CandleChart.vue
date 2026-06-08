<template>
  <div class="chart-container panel">
    <div ref="chartElement" class="chart-root"></div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import Highcharts from 'highcharts/highstock'

const props = defineProps({
  candles: {
    type: Array,
    default: () => []
  },
  symbol: {
    type: String,
    default: ''
  },
  timeframe: {
    type: String,
    default: ''
  }
})

const chartElement = ref(null)
let chart

Highcharts.setOptions({
  lang: {
    thousandsSep: ','
  }
})

function buildSeries(candles) {
  const ohlc = []
  const volume = []

  candles.forEach((candle) => {
    const timestamp = new Date(candle.datetime.replace(' ', 'T')).getTime()
    const open = Number(candle.open)
    const close = Number(candle.close)
    ohlc.push([timestamp, open, Number(candle.high), Number(candle.low), close])
    volume.push({
      x: timestamp,
      y: Number(candle.volume),
      color: close >= open ? 'rgba(0, 201, 167, 0.28)' : 'rgba(255, 77, 106, 0.28)'
    })
  })

  return { ohlc, volume }
}

function renderChart() {
  if (!chartElement.value || !props.candles.length) {
    return
  }

  const { ohlc, volume } = buildSeries(props.candles)

  if (chart) {
    chart.destroy()
  }

  chart = Highcharts.stockChart(chartElement.value, {
    chart: {
      backgroundColor: '#0f1318',
      spacing: [14, 14, 14, 14],
      style: {
        fontFamily: "'DM Mono', monospace"
      }
    },
    credits: {
      enabled: false
    },
    exporting: {
      enabled: false
    },
    legend: {
      enabled: false
    },
    title: {
      text: props.symbol ? `${props.symbol} / ${props.timeframe}` : '',
      style: {
        color: '#e8edf2',
        fontFamily: "'Syne', sans-serif",
        fontWeight: '700',
        fontSize: '16px'
      }
    },
    rangeSelector: {
      selected: 2,
      inputEnabled: false,
      buttonTheme: {
        fill: '#141920',
        stroke: '#1f2a35',
        r: 4,
        style: {
          color: '#7a8fa0'
        },
        states: {
          hover: {
            fill: '#1c242e',
            style: {
              color: '#e8edf2'
            }
          },
          select: {
            fill: 'rgba(0, 201, 167, 0.12)',
            stroke: '#00c9a7',
            style: {
              color: '#00c9a7'
            }
          }
        }
      }
    },
    navigator: {
      outlineColor: '#1f2a35',
      handles: {
        backgroundColor: '#1c242e',
        borderColor: '#2a3f52'
      },
      xAxis: {
        labels: {
          style: {
            color: '#445566'
          }
        }
      }
    },
    scrollbar: {
      enabled: false
    },
    xAxis: {
      lineColor: '#1f2a35',
      gridLineColor: '#1a222c',
      tickColor: '#1f2a35',
      labels: {
        style: {
          color: '#7a8fa0',
          fontSize: '11px'
        }
      },
      crosshair: {
        color: '#2a3f52',
        dashStyle: 'Dash'
      }
    },
    yAxis: [
      {
        height: '72%',
        gridLineColor: '#1a222c',
        labels: {
          align: 'left',
          x: 6,
          style: {
            color: '#7a8fa0',
            fontSize: '11px'
          },
          formatter() {
            return `INR ${Highcharts.numberFormat(this.value, 2, '.', ',')}`
          }
        },
        resize: {
          enabled: true
        }
      },
      {
        top: '75%',
        height: '25%',
        offset: 0,
        gridLineColor: '#1a222c',
        labels: {
          align: 'left',
          x: 6,
          style: {
            color: '#445566',
            fontSize: '10px'
          }
        }
      }
    ],
    tooltip: {
      backgroundColor: '#141920',
      borderColor: '#2a3f52',
      borderRadius: 8,
      borderWidth: 1,
      style: {
        color: '#e8edf2',
        fontSize: '12px'
      },
      formatter() {
        const date = new Date(this.x)
        const stamp = date.toLocaleString('en-IN', {
          day: '2-digit',
          month: 'short',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        })

        if (this.series.type === 'candlestick') {
          const open = Highcharts.numberFormat(this.point.open, 2, '.', ',')
          const high = Highcharts.numberFormat(this.point.high, 2, '.', ',')
          const low = Highcharts.numberFormat(this.point.low, 2, '.', ',')
          const close = Highcharts.numberFormat(this.point.close, 2, '.', ',')
          return `
            <span style="color:#7a8fa0">${stamp}</span><br />
            <b>${props.symbol}</b><br />
            O <b>INR ${open}</b> &nbsp;
            H <b style="color:#00c9a7">INR ${high}</b> &nbsp;
            L <b style="color:#ff4d6a">INR ${low}</b> &nbsp;
            C <b>INR ${close}</b>
          `
        }

        return `Volume <b>${Highcharts.numberFormat(this.y, 0, '.', ',')}</b>`
      },
      useHTML: true
    },
    series: [
      {
        type: 'candlestick',
        name: props.symbol,
        data: ohlc,
        yAxis: 0,
        color: '#ff4d6a',
        upColor: '#00c9a7',
        lineColor: '#ff4d6a',
        upLineColor: '#00c9a7',
        dataGrouping: {
          enabled: false
        }
      },
      {
        type: 'column',
        name: 'Volume',
        data: volume,
        yAxis: 1,
        borderWidth: 0,
        dataGrouping: {
          enabled: false
        }
      }
    ]
  })
}

watch(
  () => [props.candles, props.symbol, props.timeframe],
  () => {
    renderChart()
  },
  { deep: true }
)

onMounted(() => {
  renderChart()
})

onUnmounted(() => {
  if (chart) {
    chart.destroy()
  }
})
</script>

<style scoped>
.chart-container {
  padding: 1rem;
  animation: fadeUp 0.4s ease;
}

.chart-root {
  width: 100%;
  height: 540px;
}

@media (max-width: 640px) {
  .chart-root {
    height: 420px;
  }
}
</style>
