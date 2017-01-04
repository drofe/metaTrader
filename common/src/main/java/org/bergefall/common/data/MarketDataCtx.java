package org.bergefall.common.data;

import java.time.LocalDateTime;

public class MarketDataCtx implements Comparable<MarketDataCtx>{

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

	public MarketDataCtx(final String symbol, final LocalDateTime dateTime, long openPrice, long closePrice,
			long avgPrice, long highPrice, long lowPrice, long askPrice, long bidPrice, long nrTrades, long totVol, 
			long turnover) {
		if (null == dateTime) {
			throw new RuntimeException("Date is mandatory field for MarketDataCtx!");
		}
		if (null == symbol) {
			throw new RuntimeException("Symbol is mandatory field for MarketDataCtx!");
		}
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
	public int compareTo(MarketDataCtx o) {
		return date.compareTo(o.getDate());
	}

	@Override
	public String toString() {
		return date.toString() + " " 
				+ symbol + ", "
				+ (openPrice == Long.MIN_VALUE ? "" : "Open: " + openPrice) + " "
				+ (closePrice == Long.MIN_VALUE ? "" : "Close: " + closePrice) + " "
				+ (highPrice == Long.MIN_VALUE ? "" : "High: " + highPrice) + " "
				+ (lowPrice == Long.MIN_VALUE ? "" : "Low: " + lowPrice) + " "
				+ (bidPrice == Long.MIN_VALUE ? "" : "Bid: " + bidPrice) + " "
				+ (askPrice == Long.MIN_VALUE ? "" : "Ask: " + askPrice) + " "
				+ (turnover == 0 ? "" : "Turnover: " + turnover) + " "
				+ (totVol == 0 ? "" : "Total vol: " + totVol) + " "
				+ (nrTrades == 0 ? "" : "Nr of Trades: " + nrTrades) + " "
				+ (avgPrice == Long.MIN_VALUE ? "" : "Avg: " +avgPrice);
	}

}
