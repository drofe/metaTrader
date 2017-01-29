package org.bergefall.iobase.blp;

import java.time.LocalDateTime;
import java.util.SortedSet;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.config.MetaTraderBaseConfigureeImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.iobase.BlpTestBase;
import org.bergefall.iobase.routing.RoutingPipeline;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BlpTest extends BlpTestBase {

	TestBlp blp;
	
	@Before
	public void setup() {
		MetaTraderConfig config = new MetaTraderBaseConfigureeImpl(null);
		blp = new TestBlp(config);
	}
	@Test
	public void testCSD() {
		MetaTraderMessage msg = 
				MetaTraderMessageCreator.createMTMsg(createMdCtx(LocalDateTime.now()));
		blp.fireAway(msg);
		msg = MetaTraderMessageCreator.createMTMsg(createMdCtx(LocalDateTime.now()));
		blp.fireAway(msg);
		CommonStrategyData csd = blp.getOrCreateCSD();
		SortedSet<MarketDataCtx> mds = csd.getMarketDataForSymbol("TEST");
		Assert.assertEquals(2, mds.size());
	}

	@Test
	public void testTrades() {
		MetaTraderMessage msg = 
				MetaTraderMessageCreator.createMTMsg(createTradeCtx(LocalDateTime.now()));
		blp.fireAway(msg);
	}
	
	private static class TestBlp extends BusinessLogicPipelineImpl {

		public TestBlp(MetaTraderConfig config) { 
			super(config, new RoutingPipeline(config));
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
		
		@Override
		protected void handleTrades(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
