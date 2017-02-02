package org.bergefall.dbstorage.tools;

import java.util.Set;

import org.bergefall.common.data.MarketDataCtx;

public class ConvertCsvToMysqldb {

	public static void main(String[] pArgs) {

		CsvReader tCsvRead = new CsvReader("/home/ola/tmp/ERIC-B-2010-01-01-2017-02-01.csv", "ERIC");
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