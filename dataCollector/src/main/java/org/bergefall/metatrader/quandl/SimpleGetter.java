package org.bergefall.metatrader.quandl;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.QuandlSession;
import com.jimmoores.quandl.TabularResult;

public class SimpleGetter {
	
	public void getStuff() {
		// Example1.java
		QuandlSession session = QuandlSession.create();
		TabularResult tabularResult = session.getDataSet(
		  DataSetRequest.Builder.of("WIKI/AAPL").build());
		System.out.println(tabularResult.toPrettyPrintedString());
	}

	public static void main(String[] args) {
		SimpleGetter sg = new SimpleGetter();
		
		sg.getStuff();
	}

}
