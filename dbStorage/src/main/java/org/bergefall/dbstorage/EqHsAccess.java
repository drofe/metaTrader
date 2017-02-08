package org.bergefall.dbstorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EqHsAccess {

	protected static final int cSymbolIdx = 1;
	protected static final String cSymbolColName = "SYMBOL";
	protected static final int cDateTimeIdx = 2;
	protected static final String cDateTimeColName = "DATE_TIME";
	protected static final int cOpenPIdx = 3;
	protected static final String cOpenColName = "OPEN_P";
	protected static final int cClosePIdx = 4;
	protected static final String cCloseColName = "CLOSE_P";
	protected static final int cHighPIdx = 5;
	protected static final String cHighColName = "HIGH_P";
	protected static final int cLowPIdx = 6;
	protected static final String cLowColName = "LOW_P";
	protected static final int cAvgPIdx = 7;
	protected static final String cAvgColName = "AVG_P";
	protected static final int cTotVolIdx = 8;
	protected static final String cTotVolColName = "TOT_VOL";
	protected static final int cTradesNrIdx = 9;
	protected static final String cTradesNrColName = "TRADES_NR";
	protected static final int cTurnoverIdx = 10;
	protected static final String cTurnoverColName = "TURNOVER";
	protected static final int cBidPriceIdx = 11;
	protected static final String cBidColName = "BID_P";
	protected static final int cAskPriceIdx = 12;
	protected static final String cAskColName = "ASK_P";
	protected static String cDB = "META_TRADER";
	protected static String cTable = "EQ_HS";
	protected static String cUser = "espresso";
	protected static String cPasswd = "espresso";

	
	
	protected Connection connect = null;
	private Statement statement = null;
	protected PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	protected boolean init() {
		boolean retVal = true;

		try {
			if (null != connect && !connect.isClosed()) {
				return retVal;
			}
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/" + cDB + "?" + "user=" + cUser + "&password=" + cPasswd);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			retVal = false;
		}
		return (null == connect) ? false : retVal;
	}


	// You need to close the resultSet
	public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}
	
	protected String getSelectQueryFromCriteria(DataCriterias criteria) {
		StringBuilder select = new StringBuilder();
		
		select.append("SELECT ");
		select.append(cSymbolColName + "," + cDateTimeColName + ",");
		select.append(criteria.exCloseP ? "" : cCloseColName + ",");
		select.append(criteria.exOpenP ? "" : cOpenColName + ",");
		select.append(criteria.exHighP ? "" : cHighColName + ",");
		select.append(criteria.exLowP ? "" : cLowColName + ",");
		select.append(criteria.exAsk ? "" : cAskColName + ",");
		select.append(criteria.exBid ? "" : cBidColName + ",");
		select.append(criteria.exAvgP ? "" : cAvgColName + ",");
		select.append(criteria.exTrades ? "" : cTradesNrColName + ",");
		select.append(criteria.exTotVol ? "" : cTotVolColName + ",");
		select.append(criteria.exTurnover ? "" : cTurnoverColName + ",");
		if (',' == select.charAt(select.length() - 1 )) {
			select.replace(select.length() - 1, select.length(), "");
		}
		select.append(" FROM META_TRADER.EQ_HS as EQ where EQ.SYMBOL = \'");
		select.append(criteria.symbol);
		select.append("\'");
		select.append(" AND " + cDateTimeColName + " >=  ? ");
		select.append(" AND " + cDateTimeColName + " <= ? ");
		return select.toString();
	}
	
	public static boolean hasColumn(ResultSet rs, String columnName) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int x = 1; x <= columns; x++) {
				if (columnName.equals(rsmd.getColumnName(x))) {
					return true;
				}
			}
		} catch (SQLException e) {
			return false;
		}
	    return false;
	}
	
	public static class DataCriterias {
		public final Timestamp from;
		public final Timestamp to;
		public String symbol;
		public boolean exCloseP;
		public boolean exOpenP;
		public boolean exHighP;
		public boolean exLowP;
		public boolean exAvgP;
		public boolean exTotVol;
		public boolean exTurnover;
		public boolean exTrades;
		public boolean exBid = true;
		public boolean exAsk = true;
		
		public DataCriterias(LocalDateTime from, LocalDateTime to, String symbol) {
			if (null != from) {
				this.from = Timestamp.valueOf(from);
			} else {
				this.from = Timestamp.valueOf(LocalDateTime.MIN);
			}
			if (null != to) {
				this.to = Timestamp.valueOf(to);
			} else {
				this.to= Timestamp.valueOf(LocalDateTime.MAX);
			}
			this.symbol = symbol;
		}
		
		public DataCriterias excludeClosePrice() {
			exCloseP = true;
			return this;
		}
		
		public DataCriterias excludeOpenPrice() {
			exOpenP = true;
			return this;
		}
		
		public DataCriterias excludeHighPrice() {
			exHighP = true;
			return this;
		}
		
		public DataCriterias excludeLowPrice() {
			exLowP = true;
			return this;
		}
		
		public DataCriterias excludeAvgPrice() {
			exAvgP = true;
			return this;
		}
		
		public DataCriterias excludeTotalVolume() {
			exTotVol = true;
			return this;
		}
		
		public DataCriterias excludeTurnover() {
			exTurnover = true;
			return this;
		}
		
		public DataCriterias excludeNrTrades() {
			exTrades = true;
			return this;
		}
	}
}

