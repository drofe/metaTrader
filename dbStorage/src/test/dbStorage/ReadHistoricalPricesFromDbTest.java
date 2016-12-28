package dbStorage;



import java.util.Set;

import org.bergefall.dbstorage.HistoricalPriceCtx;
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
		Set<HistoricalPriceCtx> prices = mReader.getAllPricesForSymb("CINN");
		
		for (HistoricalPriceCtx price : prices) {
			System.out.println(price);
		}
	}

}
