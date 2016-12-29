package org.bergefall.common.data;

import java.time.LocalDateTime;

public class HistoricalPriceCtx implements Comparable<HistoricalPriceCtx>{

	private final String symbol;
	private final LocalDateTime date;
	private final long openPrice;
	private final long closePrice;
	private final long avgPrice;
	private final long highPrice;
	private final long lowPrice;
	private final long bidPrice;
	private final long askPrice;
	private final long nrTrades;
	private final long totVol;
	private final long turnover;

	public HistoricalPriceCtx(final String symbol, final LocalDateTime dateTime, long openPrice, long closePrice,
			long avgPrice, long highPrice, long lowPrice, long askPrice, long bidPrice, long nrTrades, long totVol, 
			long turnover) {
		this.openPrice = openPrice;
		this.avgPrice = avgPrice;
		this.closePrice = closePrice;
		this.date = dateTime;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.nrTrades = nrTrades;
		this.totVol = totVol;
		this.symbol = symbol;
		this.turnover = turnover;
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
	
	public long getBidPrice() {
		return bidPrice;
	}
	
	public long getAskPrice() {
		return askPrice;
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
	
	public long getTurnover() {
		return turnover;
	}

	@Override
	public int compareTo(HistoricalPriceCtx o) {
		return date.compareTo(o.getDate());
	}

	@Override
	public String toString() {
		return date.toString() + " " 
				+ symbol + " "
				+ openPrice + " "
				+ closePrice + " "
				+ highPrice + " "
				+ lowPrice + " "
				+ bidPrice + " "
				+ askPrice + " "
				+ turnover + " "
				+ totVol + " "
				+ nrTrades + " "
				+ avgPrice;
	}

}
