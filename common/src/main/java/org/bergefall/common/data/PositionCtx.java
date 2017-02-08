package org.bergefall.common.data;

import org.bergefall.common.MetaTraderConstants;

public class PositionCtx {

	private String symbol;
	private long longQty;
	private long shortQty;
	private long avgLongPrice;
	private long avgShortPrice;
	
	public PositionCtx(final String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public long getLongQty() {
		return longQty;
	}

	public void addLongQty(long addLongQty, long addLongPrice) {
		if (0L == addLongQty) {
			return;
		}
		long totNewValue = (avgLongPrice * longQty) / MetaTraderConstants.DIVISOR + 
				(addLongPrice * addLongQty) / MetaTraderConstants.DIVISOR;
		this.longQty += addLongQty;
		this.avgLongPrice = (totNewValue * MetaTraderConstants.DIVISOR)/ this.longQty;
	}
	
	public void removeLongQty(long removeLongQty, long removeLongPrice) {
		if (0L == removeLongQty) {
			return;
		}
		long totNewValue = (avgLongPrice * longQty) / MetaTraderConstants.DIVISOR - 
				(removeLongPrice * removeLongQty) / MetaTraderConstants.DIVISOR;
		this.longQty -= removeLongQty;
		this.avgLongPrice = 0 == this.longQty ? 0L : (totNewValue * MetaTraderConstants.DIVISOR) / this.longQty;
	}
	
	public long getShortQty() {
		return shortQty;
	}

	public void addShortQty(long addShortQty, long addShortPrice) {
		if (0L == addShortQty) {
			return;
		}
		long totNewValue = (avgShortPrice * shortQty) / MetaTraderConstants.DIVISOR + 
				(addShortPrice * addShortQty) / MetaTraderConstants.DIVISOR;
		this.shortQty += addShortQty;
		this.avgShortPrice = (totNewValue * MetaTraderConstants.DIVISOR)/ this.shortQty;
	}
	
	public void removeShortQty(long removeShortQty, long removeShortPrice) {
		if (0L == removeShortQty) {
			return;
		}
		long totNewValue = (avgShortPrice * shortQty) / MetaTraderConstants.DIVISOR - 
				(removeShortPrice * removeShortQty) / MetaTraderConstants.DIVISOR;
		this.shortQty -= removeShortQty;
		this.avgShortPrice = 0 == this.shortQty ? 0L : (totNewValue * MetaTraderConstants.DIVISOR) / this.shortQty;
	}

	public long getAvgLongPrice() {
		return avgLongPrice;
	}

	public long getAvgShortPrice() {
		return avgShortPrice;
	}

	public void setAvgLongPrice(long avgLongPrice) {
		this.avgLongPrice = avgLongPrice;
	}
	
	public void setAvgShortPrice(long avgShortPrice) {
		this.avgShortPrice = avgShortPrice;
	}
	
	public void setLongQty(long longQty) {
		this.longQty = longQty;
	}
	
	public void setShortQty(long shortQty) {
		this.shortQty = shortQty;
	}
	
	@Override
	public String toString() {
		return symbol + ", longQty: " + longQty +
				", shortQty: " + shortQty + 
				", avgLongPrice: " + avgLongPrice +
				", avgShortPrice: " + avgShortPrice;
	}
}
