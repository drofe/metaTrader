package org.bergefall.dbstorage.tools;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import org.bergefall.dbstorage.HistoricalPriceCtx;
import org.bergefall.dbstorage.MySQLAccess;

public class MySQLWriter extends MySQLAccess {

	private static final String ctxInsert = "INSERT INTO EQ_HS (SYMBOL, DATE_TIME, OPEN_P, CLOSE_P,"
			+ "HIGH_P, LOW_P, AVG_P, TOT_VOL, TRADES_NR) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final int cSymbolIdx = 1;
	private static final int cDateTimeIdx = 2;
	private static final int cOpenPIdx = 3;
	private static final int cClosePIdx = 4;
	private static final int cHighPIdx = 5;
	private static final int cLowPIdx = 6;
	private static final int cAvgPIdx = 7;
	private static final int cTotVolIdx = 8;
	private static final int cTradesNrIdx = 9;

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
