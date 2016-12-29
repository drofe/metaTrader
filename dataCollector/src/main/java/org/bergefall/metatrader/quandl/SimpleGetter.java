package org.bergefall.metatrader.quandl;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.protocol.marketdata.MarketDataCreator;
import org.bergefall.protocol.marketdata.MarketDataProtos.MarketData;
import org.threeten.bp.LocalDate;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.Row;
import com.jimmoores.quandl.TabularResult;

public class SimpleGetter {
	
	private static final int OPENIdx = 1;
	private static final String OPEN = "Open";
	private static final int HIGHIdx = 2;
	private static final String HIGH = "High";
	private static final int LOWIdx = 3;
	private static final String LOW = "Low";
	private static final int CLOSEIdx = 4;
	private static final String CLOSE = "Close";	
	
	public void getStuff() {
		// Example1.java
		QuandlSession session = QuandlSession.create();
		TabularResult tabularResult = session.getDataSet(
		  DataSetRequest.Builder.of("WIKI/AAPL")
		  .withColumn(OPENIdx)
		  .withColumn(CLOSEIdx)
		  .build());
		System.out.println(tabularResult.toPrettyPrintedString());
		
		List<MarketData> tMDList = new ArrayList<>(tabularResult.size());
		
		for (final Row tRow : tabularResult) {
			LocalDate tDate = tRow.getLocalDate("Date");
			Double tClose = tRow.getDouble("Close");
			tMDList.add(MarketDataCreator.createMD(tDate.toString(), tClose));
		}
	}
	
	public static void main(String[] args) {
		SimpleGetter sg = new SimpleGetter();
		
		sg.getStuff();
	}

}
