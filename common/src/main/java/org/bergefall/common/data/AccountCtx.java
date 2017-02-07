package org.bergefall.common.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AccountCtx {

	private String name;
	private int id;
	private String broker;
	private String user;
	private Map<String, PositionCtx> positions;
	
	public AccountCtx(String name, int id, String broker, String user) {
		this.name = name;
		this.id = id;
		this.broker = broker;
		this.user = user;
		positions = new HashMap<>();
	}
	
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public String getBroker() {
		return broker;
	}
	public String getUser() {
		return user;
	}
	
	public Collection<PositionCtx> getAllPositions() {
		return positions.values();
	}
	
	public PositionCtx getPosition(final String symbol) {
		PositionCtx ctx = positions.get(symbol);
		if (null == ctx) {
			ctx = new PositionCtx(symbol);
			positions.put(symbol, ctx);
		}
		return ctx;
	}
	
}
