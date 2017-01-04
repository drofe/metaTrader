package org.bergefall.base.strategy;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bergefall.common.data.MarketDataCtx;

public class IntraStrategyBeanMsg {

	//The MarketDataCtx class guarantees that a dateTime is set and has Comparable implemented
	// based on this dateTime
	private SortedSet<MarketDataCtx> timeSortedMarketData;
	
	public IntraStrategyBeanMsg() {
		timeSortedMarketData = new TreeSet<>();
	}
	
	public void addMarketData(MarketDataCtx marketData) {
		timeSortedMarketData.add(marketData);
	}
}
