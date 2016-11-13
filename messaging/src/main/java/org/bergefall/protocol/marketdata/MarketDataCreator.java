package org.bergefall.protocol.marketdata;

import org.bergefall.protocol.marketdata.MarketDataProtos.marketData;

public class MarketDataCreator {

	private static final long DIVISOR = 1_000_000L;
	
	public static marketData createMD(String pDate,
			Double pClose) {
		marketData tMD = marketData.newBuilder()
				.setDate(pDate)
				.setClose(Long.valueOf((long) (pClose.doubleValue() * DIVISOR)))
				.build();
		
		return tMD;
	}
}
