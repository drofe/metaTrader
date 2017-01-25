package org.bergefall.dbstorage.trade;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import org.bergefall.common.data.TradeCtx;

public class TradeWriter extends TradeAccess {

	private static final String ctxInsert = "INSERT INTO TRADES (" +
			cSymbolColName + "," + 
			cDateTimeColName + "," + 
			cAccountColName + "," +
			cQtyColName + "," + 
			cPriceColName + "," + 
			cIsEntryColName + "," +
			cNetProfitColName + "," +
			cGrossProfitColName + "," +
			cCommissionColName + ") " + 
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	


	public boolean storeTradeCtx(TradeCtx tradeCtx) {
		if(!init()) {
			return false;
		}
		try {
			preparedStatement = connect.prepareStatement(ctxInsert);
			preparedStatement.setString(cSymbolIdx, tradeCtx.getSymbol());
			preparedStatement.setTimestamp(cDateTimeIdx, Timestamp.valueOf(tradeCtx.getDate()));
			preparedStatement.setInt(cAccountIdx, tradeCtx.getAccountId());
			preparedStatement.setLong(cPriceIdx, tradeCtx.getPrice());
			preparedStatement.setLong(cQtyIdx, tradeCtx.getQty());
			preparedStatement.setLong(cNetProfitIdx, tradeCtx.getNetProfit());
			preparedStatement.setLong(cGrossProfitIdx, tradeCtx.getGrossProfit());
			preparedStatement.setLong(cCommissionIdx, tradeCtx.getCommission());
			preparedStatement.setBoolean(cIsEntryIdx, tradeCtx.getIsEntry());
			return preparedStatement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean storeTradesSet(Set<TradeCtx> trades) {
		if (null == trades || trades.isEmpty()) {
			return false;
		}
		for (TradeCtx tradeCtx : trades) {
			if(!storeTradeCtx(tradeCtx)) {
				return false;
			}
		}
		return true;
	}
}
