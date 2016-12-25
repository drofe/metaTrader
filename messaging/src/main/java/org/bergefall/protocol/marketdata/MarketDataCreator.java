package org.bergefall.protocol.marketdata;

import org.bergefall.protocol.marketdata.MarketDataProtos.MarketData;

public class MarketDataCreator {

	private static final long DIVISOR = 1_000_000L;
	
	public static MarketData createMD(String pDate, Double pClose) {
		MarketData tMD = MarketData.newBuilder()
				.setDate(pDate)
				.setClose(Long.valueOf((long) (pClose.doubleValue() * DIVISOR)))
				.build();

		return tMD;
	}
}
