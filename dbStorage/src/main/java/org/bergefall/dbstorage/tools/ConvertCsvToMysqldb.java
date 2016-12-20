package org.bergefall.dbstorage.tools;

import java.util.Set;

import org.bergefall.dbstorage.HistoricalPriceCtx;

public class ConvertCsvToMysqldb {

	public static void main(String[] pArgs) {

		CsvReader tCsvRead = new CsvReader("/home/ola/projects/tmp/CINN-2010-11-18-2016-12-19.csv", "CINN");
		MySQLWriter tW = null;

		Set<HistoricalPriceCtx> tPrices = tCsvRead.getFileContents();
		try {
			tW = new MySQLWriter();
			for (HistoricalPriceCtx ctx : tPrices) {
				tW.storePriceCtx(ctx);
			}
		} finally {
			if (null != tW) {
				tW.close();
			}
		}
	}
}