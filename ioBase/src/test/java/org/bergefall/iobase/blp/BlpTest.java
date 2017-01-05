package org.bergefall.iobase.blp;

import java.time.LocalDateTime;
import java.util.SortedSet;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.iobase.BlpTestBase;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BlpTest extends BlpTestBase {

	TestBlp blp;
	
	@Before
	public void setup() {
		blp = new TestBlp();
	}
	@Test
	public void test() {
		MetaTraderMessage msg = 
				MetaTraderMessageCreator.createMTMsg(createMdCtx(LocalDateTime.now()));
		blp.fireAway(msg);
		msg = MetaTraderMessageCreator.createMTMsg(createMdCtx(LocalDateTime.now()));
		blp.fireAway(msg);
		CommonStrategyData csd = blp.getOrCreateCSD();
		SortedSet<MarketDataCtx> mds = csd.getMarketDataForSymbol("TEST");
		Assert.assertEquals(2, mds.size());
	}

	private static class TestBlp extends BusinessLogicPipelineImpl {

		public TestBlp() {
			super(null);
		}
		
		public void fireAway(MetaTraderMessage msg) {
			fireHandlers(msg);
		}

		@Override
		protected void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleBeats(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleOrders(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
