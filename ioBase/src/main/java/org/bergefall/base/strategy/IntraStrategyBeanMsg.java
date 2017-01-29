package org.bergefall.base.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.OrderCtx;

public class IntraStrategyBeanMsg {

	//The MarketDataCtx class guarantees that a dateTime is set and has Comparable implemented
	// based on this dateTime
	private SortedSet<MarketDataCtx> timeSortedMarketData;
	private List<OrderCtx> orders;
	
	public IntraStrategyBeanMsg() {
		timeSortedMarketData = new TreeSet<>();
		orders = new ArrayList<>();
	}
	
	public void addMarketData(MarketDataCtx marketData) {
		timeSortedMarketData.add(marketData);
	}
	
	public void addOrder(OrderCtx order) {
		orders.add(order);
	}
	
	public List<OrderCtx> getOrders() {
		return orders;
	}
}
