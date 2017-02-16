package org.bergefall.se.server.common;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.PositionCtx;

public class PortfolioValueDataMangler extends DataManglerBase {

	
	
	public PortfolioValueDataMangler() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static String formatRecord(CommonStrategyData csd, String date) {
		StringBuilder builder = new StringBuilder();
		for (AccountCtx acc : csd.getAllAccounts()) {
			builder.append(date + cItemSep + acc.getName());
			for (PositionCtx pos : acc.getAllPositions()) {
				String symbol = pos.getSymbol();
				builder.append(cBlockSep + symbol);
				long currPrice = csd.getLatestMarketDataForSymbol(symbol).getClosePrice(); 
				builder.append(cItemSep + currPrice);
				builder.append(cItemSep + pos.getLongQty());
				builder.append(cItemSep + ((currPrice	* pos.getLongQty()) / MetaTraderConstants.DIVISOR));
			}
			builder.append(System.lineSeparator());
			csd.addNewPaL(acc.getId(), builder.toString());
		}
		String entry = null;
		if (System.lineSeparator().equals(builder.substring(builder.length() - 1 , builder.length()))) {
			entry = builder.substring(0, builder.length() - 1);
		} else {
			entry = builder.toString();
		}
		return entry;
	}

}
