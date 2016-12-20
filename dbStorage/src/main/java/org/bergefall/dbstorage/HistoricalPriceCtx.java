package org.bergefall.dbstorage;

import java.time.LocalDateTime;

public class HistoricalPriceCtx {

	private String symbol;
	private LocalDateTime date;
	private long openPrice;
	private long closePrice;
	private long avgPrice;
	private long highPrice;
	private long lowPrice;
	private long nrTrades;
	private long totVol;

	public HistoricalPriceCtx(String symbol, LocalDateTime dateTime, long openPrice, long closePrice,
			long avgPrice, long highPrice, long lowPrice, long nrTrades, long totVol) {
		this.openPrice = openPrice;
		this.avgPrice = avgPrice;
		this.closePrice = closePrice;
		this.date = dateTime;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.nrTrades = nrTrades;
		this.totVol = totVol;
		this.symbol = symbol;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public long getOpenPrice() {
		return openPrice;
	}

	public long getClosePrice() {
		return closePrice;
	}

	public long getAvgPrice() {
		return avgPrice;
	}

	public long getHighPrice() {
		return highPrice;
	}

	public long getLowPrice() {
		return lowPrice;
	}

	public long getNrTrades() {
		return nrTrades;
	}

	public long getTotVol() {
		return totVol;
	}

	public String getSymbol() {
		return symbol;
	}


}
