package com.pritish.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.pritish.dto.CandleDto;
import com.pritish.dto.CandleResponseDto;
import com.pritish.entity.StockCandle;
import com.pritish.exception.InvalidTimeframeException;
import com.pritish.exception.SymbolNotFoundException;
import com.pritish.repository.StockCandleRepository;
import com.pritish.util.TimeFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CandleAggregationService {

    private static final Logger log =
            LoggerFactory.getLogger(CandleAggregationService.class);

    private final StockCandleRepository repository;

    public CandleAggregationService(StockCandleRepository repository) {
        this.repository = repository;
    }

    public CandleResponseDto getCandles(
            String symbol,
            String timeframe,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "start_date cannot be after end_date");
        }

        // Validate timeframe (throws InvalidTimeframeException if bad)
        int tfMinutes = getTimeFrameMinutes(timeframe);

        // Fetch all 1-minute candles day by day (efficient partition-key queries)
        List<StockCandle> rawCandles = new ArrayList<>();
        LocalDate currentDate = startDate.toLocalDate();
        LocalDate lastDate = endDate.toLocalDate();

        while (!currentDate.isAfter(lastDate)) {
            rawCandles.addAll(
                    repository.findByKeySymbolAndKeyTradeDate(symbol, currentDate));
            currentDate = currentDate.plusDays(1);
        }

        log.info("Total records fetched from Cassandra: {}", rawCandles.size());

        if (rawCandles.isEmpty()) {
            throw new SymbolNotFoundException(
                    "No data found for symbol: " + symbol);
        }

        // Sort by candle time ascending
        rawCandles.sort(Comparator.comparing(c -> c.getKey().getCandleTime()));

        // Filter to exact requested datetime window
        rawCandles = rawCandles.stream()
                .filter(c -> {
                    LocalDateTime candleTime = toLocalDateTime(c.getKey().getCandleTime());
                    return !candleTime.isBefore(startDate) && !candleTime.isAfter(endDate);
                })
                .collect(Collectors.toList());

        log.info("Records after date filter: {}", rawCandles.size());

        if (rawCandles.isEmpty()) {
            throw new SymbolNotFoundException(
                    "No data found for symbol '" + symbol
                    + "' in the requested date range");
        }

        // Aggregate candles into requested timeframe
        List<CandleDto> aggregated = aggregate(rawCandles, tfMinutes);

        CandleResponseDto response = new CandleResponseDto();
        response.setSymbol(symbol);
        response.setTimeframe(timeframe);
        response.setCandles(aggregated);
        response.setCount(aggregated.size());

        return response;
    }

    /**
     * Groups 1-minute candles into buckets of `tfMinutes` minutes and
     * applies OHLCV aggregation rules:
     *   Open  = first open in window
     *   High  = max high  in window
     *   Low   = min low   in window
     *   Close = last close in window
     *   Volume = sum of volumes in window
     */
    private List<CandleDto> aggregate(List<StockCandle> candles, int tfMinutes) {

        if (tfMinutes == 1) {
            // Raw 1-minute data – no aggregation needed
            return candles.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Use a LinkedHashMap to preserve insertion (time) order
        Map<LocalDateTime, List<StockCandle>> buckets = new LinkedHashMap<>();

        for (StockCandle candle : candles) {
            LocalDateTime candleTime =
                    toLocalDateTime(candle.getKey().getCandleTime());
            LocalDateTime bucketStart = getBucketStart(candleTime, tfMinutes);
            buckets.computeIfAbsent(bucketStart, k -> new ArrayList<>()).add(candle);
        }

        List<CandleDto> result = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<StockCandle>> entry : buckets.entrySet()) {
            List<StockCandle> group = entry.getValue();

            BigDecimal open  = group.get(0).getOpen();
            BigDecimal high  = group.stream()
                                    .map(StockCandle::getHigh)
                                    .max(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);
            BigDecimal low   = group.stream()
                                    .map(StockCandle::getLow)
                                    .min(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO);
            BigDecimal close = group.get(group.size() - 1).getClose();
            long volume      = group.stream()
                                    .mapToLong(StockCandle::getVolume)
                                    .sum();

            String datetime = entry.getKey().format(formatter);
            result.add(new CandleDto(datetime, open, high, low, close, volume));
        }

        return result;
    }

    /**
     * Floors a timestamp to the nearest bucket boundary.
     * e.g., 09:17 with tfMinutes=5 → 09:15
     */
    private LocalDateTime getBucketStart(LocalDateTime time, int tfMinutes) {
        int minute = time.getHour() * 60 + time.getMinute();
        int bucketMinute = (minute / tfMinutes) * tfMinutes;
        int hour = bucketMinute / 60;
        int min  = bucketMinute % 60;
        return time.toLocalDate().atTime(hour, min, 0);
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private CandleDto toDto(StockCandle candle) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = toLocalDateTime(candle.getKey().getCandleTime())
                .format(formatter);
        return new CandleDto(
                datetime,
                candle.getOpen(),
                candle.getHigh(),
                candle.getLow(),
                candle.getClose(),
                candle.getVolume());
    }

    private int getTimeFrameMinutes(String timeframe) {
        switch (timeframe.toLowerCase()) {
            case "1m":  return TimeFrame.M1.getMinutes();
            case "5m":  return TimeFrame.M5.getMinutes();
            case "15m": return TimeFrame.M15.getMinutes();
            case "30m": return TimeFrame.M30.getMinutes();
            case "1h":  return TimeFrame.H1.getMinutes();
            case "1d":  return TimeFrame.D1.getMinutes();
            default:
                throw new InvalidTimeframeException(
                        "Unsupported timeframe '" + timeframe
                        + "'. Valid values: 1m, 5m, 15m, 30m, 1h, 1d");
        }
    }
}