package org.bergefall.dbstorage.test;


import java.util.Set;

import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.dbstorage.ReadHistoricalEqPrices;
import org.junit.Before;
import org.junit.Test;

public class ReadHistoricalPricesFromDbTest {

	public ReadHistoricalEqPrices mReader;
	
	@Before
	public void setup() {
		mReader = new ReadHistoricalEqPrices();
	}
	
	@Test
	public void testGetAllPricesForSymb() {
		Set<MarketDataCtx> prices = mReader.getAllPricesForSymb("CINN");
		
		for (MarketDataCtx price : prices) {
			System.out.println(price);
		}
	}

}
