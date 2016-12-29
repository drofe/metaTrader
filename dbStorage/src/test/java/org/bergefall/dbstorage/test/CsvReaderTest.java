package org.bergefall.dbstorage.test;

import org.bergefall.common.data.HistoricalPriceCtx;
import org.bergefall.dbstorage.tools.CsvReader;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import java.util.Set;

public class CsvReaderTest {

	private final static long cFac = 1_000_000L;
	
	CsvReader mReader;
	@Before
	public void setup() {
		mReader = new CsvReader("resources/CsvTest.csv", "CINN");
	}
	
	@Test
	public void testGetFileContents() {
	
		Set<HistoricalPriceCtx> tRes = mReader.getFileContents();
		
		Assert.assertEquals(1, tRes.size());
		HistoricalPriceCtx tPriceCtx = tRes.iterator().next();
		assertEquals("Wrong bid price", 193 * cFac, tPriceCtx.getBidPrice());
		assertEquals("Wrong ask price", 196 * cFac, tPriceCtx.getAskPrice());
		assertEquals("Wrong open price", 200 * cFac, tPriceCtx.getOpenPrice());
		assertEquals("Wrong high price", 202 * cFac, tPriceCtx.getHighPrice());
		assertEquals("Wrong low price", 189 * cFac, tPriceCtx.getLowPrice());
		assertEquals("Wrong close price", 196 * cFac, tPriceCtx.getClosePrice());
		assertEquals("Wrong average price", (long)(195.09 * cFac), tPriceCtx.getAvgPrice());
		assertEquals("Wrong total volume", 1470 * cFac, tPriceCtx.getTotVol());
		assertEquals("Wrong turnover", (long)(286779.5 * cFac), tPriceCtx.getTurnover());
		assertEquals("Wrong number of trades", 25 * cFac, tPriceCtx.getNrTrades());
		
		
	}

}
