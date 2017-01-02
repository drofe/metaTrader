package org.bergefall.dbstorage.tools;

import java.util.Set;

import org.bergefall.common.data.MarketDataCtx;

public class ConvertCsvToMysqldb {

	public static void main(String[] pArgs) {

		CsvReader tCsvRead = new CsvReader("/home/ola/projects/tmp/CINN-2010-11-18-2016-12-19.csv", "CINN");
		MySQLWriter tW = null;

		Set<MarketDataCtx> tPrices = tCsvRead.getFileContents();
		try {
			tW = new MySQLWriter();
			for (MarketDataCtx ctx : tPrices) {
				tW.storePriceCtx(ctx);
			}
		} finally {
			if (null != tW) {
				tW.close();
			}
		}
	}
}