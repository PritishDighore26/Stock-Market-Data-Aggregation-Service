package com.pritish.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("stock_candles")
public class StockCandle {

    @PrimaryKey
    private StockCandleKey key;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;

    public StockCandle() {
    }
    
    public StockCandle(StockCandleKey key,
            BigDecimal open,
            BigDecimal high,
            BigDecimal low,
            BigDecimal close,
            Long volume) {
this.key = key;
this.open = open;
this.high = high;
this.low = low;
this.close = close;
this.volume = volume;
}

    public StockCandleKey getKey() {
        return key;
    }

    public void setKey(StockCandleKey key) {
        this.key = key;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    
}