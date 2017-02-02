package org.bergefall.common.data;

public class OrderCtx {

	private long price;
	private long qty;
	private int accountId;
	private String symbol;
	private boolean isBid;
	
	public OrderCtx(String symbol, 
			long qty, boolean isBid) {
		this.qty = qty;
		this.symbol = symbol;
		this.isBid = isBid;
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
	
	public void setAccountId(int id) {
		accountId = id;
	}
	
	public boolean isBid() {
		return isBid;
	}

	public int getAccountId() {
		return accountId;
	}
}
