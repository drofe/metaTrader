package org.bergefall.common.data;

public class InstrumentCtx {
	private final String name;
	private final int id;
	
	public InstrumentCtx(final String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getSymbol() {
		return name;
	}
}
