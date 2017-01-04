package org.bergefall.dbstorage;

import static org.bergefall.common.MetaTraderConstants.DefaultEqPrice;
import static org.bergefall.common.MetaTraderConstants.DefaultVal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.bergefall.common.data.MarketDataCtx;

/**
 * Use this class to read historical prices from MySQL DB.
 * @author ola
 *
 */
public class ReadHistoricalEqPrices extends EqHsAccess {

	private static final String cSymbSelect = "SELECT * FROM META_TRADER.EQ_HS as EQ where EQ.SYMBOL = \'";
	
	public Set<MarketDataCtx> getPricesByCriteria(DataCriterias criteria) {
		if (!init()) {
			return new TreeSet<>();
		}
		//Build select statement.
		String selectQ = getSelectQueryFromCriteria(criteria);
		PreparedStatement statement = null;
		try {
			statement = connect.prepareStatement(selectQ);
			statement.setTimestamp(1, criteria.from);
			statement.setTimestamp(2, criteria.to);
		} catch (SQLException e) {

		}

		return issueStatement(statement);
	}
		
	public Set<MarketDataCtx> getAllPricesForSymb(String symbol) {
		if (!init()) {
			return new TreeSet<>();
		}
		String query = cSymbSelect + symbol + "\'";
		
		PreparedStatement statement = null;
		try {
			statement = connect.prepareStatement(query);
		} catch (SQLException e) {

		}

		return issueStatement(statement);
	}
	
	private Set<MarketDataCtx> issueStatement(PreparedStatement statement) {
		Set<MarketDataCtx> historicalPrices = new TreeSet<>();
		if (!init()) {
			return historicalPrices;
		}
		

		try {
			ResultSet resultSet = statement.executeQuery();
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
	
	protected void addPricesFromResultSet(ResultSet resultSet, Set<MarketDataCtx> prices) {
		if (null == resultSet || null == prices) {
			return;
		}
		
		try {
			while(resultSet.next()) {
				String symb = resultSet.getString(cSymbolColName);
				Timestamp timestamp = resultSet.getTimestamp(cDateTimeColName);
				LocalDateTime ldt = null == timestamp ? null : timestamp.toLocalDateTime();

				MarketDataCtx ctx = new MarketDataCtx(symb, ldt, 
						getPrice(getLongCol(resultSet, cOpenColName)), 
						getPrice(getLongCol(resultSet, cCloseColName)), 
					    getPrice(getLongCol(resultSet, cAvgColName)), 
					    getPrice(getLongCol(resultSet, cHighColName)), 
					    getPrice(getLongCol(resultSet, cLowColName)), 
					    getPrice(getLongCol(resultSet, cAskColName)), 
					    getPrice(getLongCol(resultSet, cBidColName)), 
					    getValWithDefault(getLongCol(resultSet, cTradesNrColName)), 
					    getValWithDefault(getLongCol(resultSet, cTotVolColName)), 
					    getValWithDefault(getLongCol(resultSet, cTurnoverColName)));
				prices.add(ctx);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Long getLongCol(ResultSet result, String col) throws SQLException {
		if (hasColumn(result, col)) {
			return result.getLong(col);
		}
		return null;
	}
	
	private long getPrice(Long price) {
		return null == price ? DefaultEqPrice : price.longValue();
	}
	
	private long getValWithDefault(Long value) {
		return null == value ? DefaultVal : value.longValue();
	}
	
}

