package org.bergefall.dbstorage.tools;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import org.bergefall.dbstorage.HistoricalPriceCtx;
import org.bergefall.dbstorage.EqHsAccess;

public class MySQLWriter extends EqHsAccess {

	private static final String ctxInsert = "INSERT INTO EQ_HS (" +
			cSymbolColName + "," + 
			cDateTimeColName + ", OPEN_P, " + 
			cCloseColName + ","
			+ "HIGH_P, LOW_P, AVG_P, TOT_VOL, TRADES_NR, TURNOVER, BID_P, ASK_P) " + 
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	

	public boolean storePriceCtx(HistoricalPriceCtx priceCtx) {
		if(!init()) {
			return false;
		}
		try {
			preparedStatement = connect.prepareStatement(ctxInsert);
			preparedStatement.setString(cSymbolIdx, priceCtx.getSymbol());
			preparedStatement.setTimestamp(cDateTimeIdx, Timestamp.valueOf(priceCtx.getDate()));
			preparedStatement.setLong(cOpenPIdx, priceCtx.getOpenPrice());
			preparedStatement.setLong(cClosePIdx, priceCtx.getClosePrice());
			preparedStatement.setLong(cHighPIdx, priceCtx.getHighPrice());
			preparedStatement.setLong(cLowPIdx, priceCtx.getLowPrice());
			preparedStatement.setLong(cAvgPIdx, priceCtx.getAvgPrice());
			preparedStatement.setLong(cTotVolIdx, priceCtx.getTotVol());
			preparedStatement.setLong(cTradesNrIdx, priceCtx.getNrTrades());
			preparedStatement.setLong(cTurnoverIdx, priceCtx.getTurnover());
			preparedStatement.setLong(cAskPriceIdx, priceCtx.getAskPrice());
			preparedStatement.setLong(cBidPriceIdx, priceCtx.getBidPrice());

			return preparedStatement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean storePricesSet(Set<HistoricalPriceCtx> prices) {
		if (null == prices || prices.isEmpty()) {
			return false;
		}
		for (HistoricalPriceCtx priceCtx : prices) {
			if(!storePriceCtx(priceCtx)) {
				return false;
			}
		}
		return true;
	}
}
