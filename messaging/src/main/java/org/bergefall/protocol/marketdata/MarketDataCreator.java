package org.bergefall.protocol.marketdata;
import static org.bergefall.common.MetaTraderConstants.DIVISOR;
import org.bergefall.protocol.marketdata.MarketDataProtos.MarketData;

public class MarketDataCreator {
	
	public static MarketData createMD(String pDate, Double pClose) {
		MarketData tMD = MarketData.newBuilder()
				.setDate(pDate)
				.setClose(Long.valueOf((long) (pClose.doubleValue() * DIVISOR)))
				.build();

		return tMD;
	}
}
