package org.bergefall.iobase.blp;

import org.bergefall.common.config.MetaTraderBaseConfigureeImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.BlpTestBase;
import org.bergefall.iobase.routing.RoutingPipeline;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.junit.Before;
import org.junit.Test;

public class RoutingBlpTest extends BlpTestBase {

	TestRouterBlp routingBlp;
	
	@Before
	public void setup() {
		MetaTraderConfig config = new MetaTraderBaseConfigureeImpl(null);
		routingBlp = new TestRouterBlp(config);
	}

	@Test
	public void testInstruments() {
		MetaTraderMessage msg = 
				MetaTraderMessageCreator.createMTMsg(createInstrCtx("RotingTestInstr"));
		routingBlp.fireAway(msg);
	}
	
	@Test
	public void testRunningRouter()  {
		try {
			Thread routeThread = new Thread(routingBlp);
			routeThread.start();
			MetaTraderMessage msg = 
					MetaTraderMessageCreator.createMTMsg(createInstrCtx("RotingTestInstr"));
			routingBlp.enqueue(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			routingBlp.shutdown();
		}
	}
	
	private static class TestRouterBlp extends RoutingPipeline {

		public TestRouterBlp(MetaTraderConfig config) { 
			super(config);
		}
		
		public void fireAway(MetaTraderMessage msg) {
			fireHandlers(msg);
		}
		
	}
}
