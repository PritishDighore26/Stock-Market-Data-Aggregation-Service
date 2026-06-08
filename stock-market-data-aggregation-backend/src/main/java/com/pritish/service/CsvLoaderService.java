package com.pritish.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pritish.entity.StockCandle;
import com.pritish.entity.StockCandleKey;
import com.pritish.repository.StockCandleRepository;

@Service
public class CsvLoaderService implements CommandLineRunner {

    private final StockCandleRepository repository;

    public CsvLoaderService(StockCandleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
    	
    	if(repository.count() > 0) {
            System.out.println("Data already loaded.");
            return;
        }

        DateTimeFormatter formatter =
        		 DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                new ClassPathResource("stock_data.csv")
                                        .getInputStream()));

        String line;

        // Skip header
        reader.readLine();

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",");

            String symbol = data[0];

            LocalDateTime dateTime =
                    LocalDateTime.parse(data[1], formatter);

            LocalDate tradeDate = dateTime.toLocalDate();

            Instant candleTime =
                    dateTime.atZone(ZoneId.systemDefault())
                            .toInstant();

            StockCandleKey key =
                    new StockCandleKey(
                            symbol,
                            tradeDate,
                            candleTime);

            StockCandle candle = new StockCandle();

            candle.setKey(key);
            candle.setOpen(new BigDecimal(data[2]));
            candle.setHigh(new BigDecimal(data[3]));
            candle.setLow(new BigDecimal(data[4]));
            candle.setClose(new BigDecimal(data[5]));
            candle.setVolume(Long.parseLong(data[6]));

            repository.save(candle);
        }

        System.out.println("CSV Data Imported Successfully");
    }
}