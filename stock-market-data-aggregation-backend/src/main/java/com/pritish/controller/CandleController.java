package com.pritish.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pritish.dto.CandleResponseDto;
import com.pritish.dto.ErrorResponseDto;
import com.pritish.exception.InvalidTimeframeException;
import com.pritish.exception.SymbolNotFoundException;
import com.pritish.service.CandleAggregationService;

@RestController
@RequestMapping("/api/v1")
public class CandleController {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CandleAggregationService service;

    public CandleController(CandleAggregationService service) {
        this.service = service;
    }

    @GetMapping("/candles")
    public ResponseEntity<?> getCandles(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String timeframe,
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date",   required = false) String endDate) {

        if (symbol == null || symbol.isBlank()) {
            return badRequest("Missing required parameter: symbol");
        }
        if (timeframe == null || timeframe.isBlank()) {
            return badRequest("Missing required parameter: timeframe");
        }
        if (startDate == null || startDate.isBlank()) {
            return badRequest("Missing required parameter: start_date");
        }
        if (endDate == null || endDate.isBlank()) {
            return badRequest("Missing required parameter: end_date");
        }

        LocalDateTime start;
        LocalDateTime end;

        try {
            start = LocalDateTime.parse(startDate, FORMATTER);
        } catch (DateTimeParseException e) {
            return badRequest(
                    "Invalid start_date format. Expected: yyyy-MM-dd HH:mm:ss");
        }

        try {
            end = LocalDateTime.parse(endDate, FORMATTER);
        } catch (DateTimeParseException e) {
            return badRequest(
                    "Invalid end_date format. Expected: yyyy-MM-dd HH:mm:ss");
        }

        try {
            CandleResponseDto response =
                    service.getCandles(symbol.toUpperCase(), timeframe, start, end);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (InvalidTimeframeException e) {
            return badRequest(e.getMessage());
        } catch (SymbolNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponseDto(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ErrorResponseDto(500, "Internal server error: " + e.getMessage()));
        }
    }

    private ResponseEntity<ErrorResponseDto> badRequest(String message) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto(400, message));
    }
}