package org.bergefall.dbstorage.trade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TradeAccess {

	protected static final int cSymbolIdx = 1;
	protected static final String cSymbolColName = "SYMBOL";
	protected static final int cDateTimeIdx = 2;
	protected static final String cDateTimeColName = "DATE_TIME";
	protected static final int cAccountIdx = 3;
	protected static final String cAccountColName = "ACCOUNTID";
	protected static final int cQtyIdx = 4;
	protected static final String cQtyColName = "QTY";
	protected static final int cPriceIdx = 5;
	protected static final String cPriceColName = "PRICE";
	protected static final int cIsEntryIdx = 6;
	protected static final String cIsEntryColName = "IS_ENTRY";
	protected static final int cNetProfitIdx = 7;
	protected static final String cNetProfitColName = "NET_PROFIT";
	protected static final int cGrossProfitIdx = 8;
	protected static final String cGrossProfitColName = "GROSS_PROFIT";
	protected static final int cCommissionIdx = 9;
	protected static final String cCommissionColName = "COMMISSION";
	protected static String cDB = "META_TRADER";
	protected static String cTable = "TRADES";
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
		select.append(criteria.exAccount ? "" : cAccountColName + ",");
		select.append(criteria.exQty ? "" : cQtyColName + ",");
		select.append(criteria.exPrice ? "" : cPriceColName + ",");
		select.append(criteria.exIsEntry ? "" : cIsEntryColName + ",");
		select.append(criteria.exNetProfit ? "" : cNetProfitColName + ",");
		select.append(criteria.exGrossProfit ? "" : cGrossProfitColName + ",");
		select.append(criteria.exCommission ? "" : cCommissionColName + ",");
		if (',' == select.charAt(select.length() - 1 )) {
			select.replace(select.length() - 1, select.length(), "");
		}
		select.append(" FROM META_TRADER.TRADES as TRADES where TRADES.SYMBOL = \'");
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
		public Timestamp from;
		public Timestamp to;
		public String symbol;
		public boolean exAccount;
		public boolean exQty;
		public boolean exPrice;
		public boolean exIsEntry;
		public boolean exNetProfit;
		public boolean exGrossProfit;
		public boolean exCommission;
		
		public DataCriterias(LocalDateTime from, LocalDateTime to, String symbol) {
			this.from = Timestamp.valueOf(from);
			this.to = Timestamp.valueOf(to);
			this.symbol = symbol;
		}
		
		public DataCriterias excludeAccount() {
			exAccount = true;
			return this;
		}
		
		public DataCriterias excludeQty() {
			exQty = true;
			return this;
		}
		
		public DataCriterias excludePrice() {
			exPrice = true;
			return this;
		}
		
		public DataCriterias excludeIsEntry() {
			exIsEntry = true;
			return this;
		}
		
		public DataCriterias excludeNetProfit() {
			exNetProfit = true;
			return this;
		}
		
		public DataCriterias excludeGrossProfit() {
			exGrossProfit = true;
			return this;
		}
		
		public DataCriterias excludeCommission() {
			exCommission = true;
			return this;
		}
	}
}

