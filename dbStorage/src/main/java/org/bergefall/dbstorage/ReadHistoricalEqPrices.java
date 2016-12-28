package org.bergefall.dbstorage;

import java.util.Set;
import java.util.TreeSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Use this class to read historical prices from MySQL DB.
 * @author ola
 *
 */
public class ReadHistoricalEqPrices extends EqHsAccess {

	private static final String cSymbSelect = "SELECT * FROM META_TRADER.EQ_HS as EQ where EQ.SYMBOL = \'";
	private static final long cDefaultEqPrice = -1L;
	private static final long cDefaultVal = 0L;
	
	public Set<HistoricalPriceCtx> getAllPricesForSymb(String symbol) {
		Set<HistoricalPriceCtx> historicalPrices = new TreeSet<>();
		if (!init()) {
			return historicalPrices;
		}
		
		Statement statement = null;
		try {
			statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery(cSymbSelect + symbol + "\'");
			addPricesFromResultSet(resultSet, historicalPrices);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return historicalPrices;
	}
	
	protected void addPricesFromResultSet(ResultSet resultSet, Set<HistoricalPriceCtx> prices) {
		if (null == resultSet || null == prices) {
			return;
		}
		
		try {
			while(resultSet.next()) {
				String symb = resultSet.getString(cSymbolColName);
				Timestamp timestamp = resultSet.getTimestamp(cDateTimeColName);
				LocalDateTime ldt = null == timestamp ? null : timestamp.toLocalDateTime();

				HistoricalPriceCtx ctx = new HistoricalPriceCtx(symb, ldt, 
						getPrice(resultSet.getLong(cOpenColName)), 
						getPrice(resultSet.getLong(cCloseColName)), 
					    getPrice(resultSet.getLong(cAvgColName)), 
					    getPrice(resultSet.getLong(cHighColName)), 
					    getPrice(resultSet.getLong(cLowColName)), 
					    getPrice(resultSet.getLong(cAskColName)), 
					    getPrice(resultSet.getLong(cBidColName)), 
					    getValWithDefault(resultSet.getLong(cTradesNrColName)), 
					    getValWithDefault(resultSet.getLong(cTotVolColName)), 
					    getValWithDefault(resultSet.getLong(cTurnoverColName)));
				prices.add(ctx);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private long getPrice(Long price) {
		return null == price ? cDefaultEqPrice : price.longValue();
	}
	
	private long getValWithDefault(Long value) {
		return null == value ? cDefaultVal : value.longValue();
	}
}

