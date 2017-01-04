package org.bergefall.dbstorage.test;


import java.time.LocalDateTime;
import java.util.Set;

import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.dbstorage.EqHsAccess.DataCriterias;
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
	
	@Test
	public void testGetPricesWithCriteria() {
		DataCriterias criteria = new DataCriterias(LocalDateTime.MIN, LocalDateTime.now(), "CINN");
		Set<MarketDataCtx> prices = mReader.getPricesByCriteria(criteria);
		
		for (MarketDataCtx md : prices) {
			System.out.println(md);
		}
		
	}

	@Test
	public void testGetPricesWithCriteriaOnlyDec2016() {
		DataCriterias criteria = new DataCriterias(LocalDateTime.of(2016, 12, 1, 0, 0), 
				LocalDateTime.of(2016, 12, 31, 0, 0), "CINN");
		Set<MarketDataCtx> prices = mReader.getPricesByCriteria(criteria);
		
		for (MarketDataCtx md : prices) {
			System.out.println(md);
		}
		
	}
	
	@Test
	public void testGetPricesWithLimitedCriteriaOnlyDec2016() {
		DataCriterias criteria = new DataCriterias(LocalDateTime.of(2016, 12, 1, 0, 0), 
				LocalDateTime.of(2016, 12, 31, 0, 0), "CINN");
		criteria.excludeAvgPrice()
			.excludeNrTrades()
			.excludeOpenPrice()
			.excludeLowPrice()
			.excludeTurnover()
			.excludeTotalVolume()
			.excludeHighPrice();
		Set<MarketDataCtx> prices = mReader.getPricesByCriteria(criteria);
		
		for (MarketDataCtx md : prices) {
			System.out.println(md);
		}
		
	}
	
}
