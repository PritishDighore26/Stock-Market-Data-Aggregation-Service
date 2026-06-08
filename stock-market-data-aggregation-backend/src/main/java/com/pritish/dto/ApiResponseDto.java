package com.pritish.dto;

import java.util.List;

public class ApiResponseDto {

    private String symbol;
    private String timeframe;
    private List<CandleResponseDto> candles;
    private int count;

    public ApiResponseDto() {
    }

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTimeframe() {
		return timeframe;
	}

	public void setTimeframe(String timeframe) {
		this.timeframe = timeframe;
	}

	public List<CandleResponseDto> getCandles() {
		return candles;
	}

	public void setCandles(List<CandleResponseDto> candles) {
		this.candles = candles;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


}