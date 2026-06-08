package com.pritish.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;

@PrimaryKeyClass
public class StockCandleKey {

    @PrimaryKeyColumn(
            name = "symbol",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(
            name = "trade_date",
            ordinal = 1,
            type = PrimaryKeyType.PARTITIONED)
    private LocalDate tradeDate;

    @PrimaryKeyColumn(
            name = "candle_time",
            ordinal = 2,
            type = PrimaryKeyType.CLUSTERED)
    private Instant candleTime;

    public StockCandleKey() {
    	
    }
    
    public StockCandleKey(String symbol,
            LocalDate tradeDate,
            Instant candleTime) {
	this.symbol = symbol;
	this.tradeDate = tradeDate;
	this.candleTime = candleTime;
}
    

	@Override
	public int hashCode() {
		return Objects.hash(candleTime, symbol, tradeDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockCandleKey other = (StockCandleKey) obj;
		return Objects.equals(candleTime, other.candleTime) && Objects.equals(symbol, other.symbol)
				&& Objects.equals(tradeDate, other.tradeDate);
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Instant getCandleTime() {
		return candleTime;
	}

	public void setCandleTime(Instant candleTime) {
		this.candleTime = candleTime;
	}
    
}