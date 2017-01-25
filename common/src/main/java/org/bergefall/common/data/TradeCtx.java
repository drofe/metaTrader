package org.bergefall.common.data;

import java.time.LocalDateTime;

public class TradeCtx implements Comparable<TradeCtx> {
	
	private final LocalDateTime date;
	private final String symbol;
	private final int accountId;
	private final boolean isEntry;
	private final Long price;
	private final Long qty;
	private final Long netProfit;
	private final Long grossProfit;
	private final Long commission;
	
	public TradeCtx(final String symbol, 
			final LocalDateTime dateTime,
			final int accId,
			final boolean isEntry,
			final Long price,
			final Long qty,
			final Long netProfit,
			final Long grossProfit,
			final Long commission) {
		if (null == symbol) {
			throw new RuntimeException("Symbol is mandatory field for TradeCtx!");
		}
		this.symbol = symbol;
		if (null == dateTime) {
			throw new RuntimeException("dateTime is mandatory field for TradeCtx!");
		}
		this.isEntry = isEntry;
		this.date = dateTime;
		this.accountId = accId;
		this.price = price;
		this.qty = qty;
		this.netProfit = netProfit;
		this.grossProfit = grossProfit;
		this.commission = commission;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public int getAccountId() {
		return accountId;
	}
	
	public Long getPrice() {
		return price;
	}
	
	public Long getQty() {
		return qty;
	}
	
	public Long getNetProfit() {
		return netProfit;
	}
	
	public Long getGrossProfit() {
		return grossProfit;
	}
	
	public Long getCommission() {
		return commission;
	}
	
	public boolean getIsEntry() {
		return isEntry;
	}
	
	@Override
	public int compareTo(TradeCtx o) {
		return date.compareTo(o.getDate());
	}

	
}
