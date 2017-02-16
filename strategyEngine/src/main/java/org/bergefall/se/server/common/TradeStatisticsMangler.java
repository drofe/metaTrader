package org.bergefall.se.server.common;

import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

public class TradeStatisticsMangler extends DataManglerBase {

	public TradeStatisticsMangler() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getTitle() {
		return "Date" + cItemSep + 
				"AccountName" + cItemSep +
				"Instrument" + cItemSep + 
				"IsEntry" + cItemSep + 
				"Price" + cItemSep + 
				"Qty" + cItemSep + 
				"Net profit" + cItemSep + 
				"Gross profit" + cItemSep + 
				"Commission";
	}

	public static String formatLogEntry(Trade trade) {
		StringBuilder buf = new StringBuilder(256);
		buf.append(trade.getDate());
		buf.append(cItemSep);
		buf.append("ACC");
		buf.append(cItemSep);
		buf.append(trade.getInstrument().getName());
		buf.append(cItemSep);
		buf.append(trade.getIsEntry());
		buf.append(cItemSep);
		buf.append(trade.getPrice());
		buf.append(cItemSep);
		buf.append(trade.getQty());
		buf.append(cItemSep);
		buf.append(trade.getNetProfit());
		buf.append(cItemSep);
		buf.append(trade.getGrossProfit());
		buf.append(cItemSep);
		buf.append(trade.getCommission());
		return buf.toString();
	}
}
