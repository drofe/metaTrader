package org.bergefall.common.data;

public class OrderCtx {

	private long price;
	private long qty;
	private String symbol;
	
	public OrderCtx(String symbol, long qty) {
		this.qty = qty;
		this.symbol = symbol;
	}
	
	public long getPrice() {
		return price;
	}
	
	public long getQty() {
		return qty;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setPrice(long price) {
		this.price = price;
	}
}
