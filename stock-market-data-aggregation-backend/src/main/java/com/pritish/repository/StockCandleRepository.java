package com.pritish.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.pritish.entity.StockCandle;
import com.pritish.entity.StockCandleKey;

@Repository
public interface StockCandleRepository
        extends CassandraRepository<StockCandle, StockCandleKey> {

    List<StockCandle> findByKeySymbolAndKeyTradeDate(
            String symbol,
            LocalDate tradeDate);
    
    @Query("SELECT * FROM stock_candles WHERE symbol = ?0 AND trade_date = ?1")
    List<StockCandle> findCandles(
            String symbol,
            LocalDate tradeDate);
}