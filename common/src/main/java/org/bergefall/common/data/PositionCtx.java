package org.bergefall.common.data;

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

	public void addLongQty(long longQty) {
		this.longQty += longQty;
	}
	
	public void removeLongQty(long longQty) {
		this.longQty -= longQty;
	}
	
	public void setLongQty(long longQty) {
		this.longQty = longQty;
	}

	public long getShortQty() {
		return shortQty;
	}

	public void setShortQty(long shortQty) {
		this.shortQty = shortQty;
	}
	
	public void addShortQty(long shortQty) {
		this.shortQty += shortQty;
	}
	
	public void removeShortQty(long shortQty) {
		this.shortQty -= shortQty;
	}

	public long getAvgLongPrice() {
		return avgLongPrice;
	}

	public void setAvgLongPrice(long avgLongPrice) {
		this.avgLongPrice = avgLongPrice;
	}

	public long getAvgShortPrice() {
		return avgShortPrice;
	}

	public void setAvgShortPrice(long avgShortPrice) {
		this.avgShortPrice = avgShortPrice;
	}

}
