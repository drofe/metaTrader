package org.bergefall.dbstorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

}