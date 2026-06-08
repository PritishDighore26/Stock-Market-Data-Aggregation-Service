-- schema.cql
-- Run this in cqlsh before starting the application

-- 1. Create keyspace
CREATE KEYSPACE IF NOT EXISTS stock_keyspace
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
    };

USE stock_keyspace;

-- 2. Create table
-- Partition key: (symbol, trade_date)
--   → All 1-minute candles for a given symbol on a given day live in one
--     partition. This keeps partitions small (~375 rows/day for market hours)
--     and allows efficient range queries within a day.
-- Clustering key: candle_time ASC
--   → Rows within a partition are stored in chronological order, enabling
--     fast range scans when filtering start_date..end_date.

CREATE TABLE IF NOT EXISTS stock_candles (
    symbol      text,
    trade_date  date,
    candle_time timestamp,
    open        decimal,
    high        decimal,
    low         decimal,
    close       decimal,
    volume      bigint,
    PRIMARY KEY ((symbol, trade_date), candle_time)
) WITH CLUSTERING ORDER BY (candle_time ASC)
  AND default_time_to_live = 0
  AND comment = 'Stores 1-minute OHLCV candlestick data partitioned by symbol and trading date';
