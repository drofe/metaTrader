package org.bergefall.iobase.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.DateUtils;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.iobase.blp.BusinessLogicPipelineImpl;
import org.bergefall.iobase.server.MetaTraderServerApplication;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class DemoServer extends MetaTraderServerApplication {

	private static final String configFile = "../common/src/main/resources/ConfigExample.properties";

	public static void main(String[] args) throws InterruptedException {
		DemoServer ds = new DemoServer();
		ds.initServer(configFile);
		Thread server = new Thread(ds);
		server.setName("Demo Server listener");
		server.start();
		server.join();
	}
	
	private static class SimpleBLP extends BusinessLogicPipelineImpl {

		public SimpleBLP(MetaTraderConfig config, CommonStrategyData csd) {
			super(config, csd);
		}

		@Override
		protected void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			MetaTraderMessage msg = token.getTriggeringMsg();
			if ( msg.getSeqNo() % 11 == 0) {
				System.out.println("Handled MarketData msg with seqno: " + msg.getSeqNo() + " at: " +
						DateUtils.getCurrentTimeAsReadableDate() + msg);			
			}
		}

		@Override
		protected void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			MetaTraderMessage msg = token.getTriggeringMsg();
			if ( msg.getSeqNo() % 10 == 0) {
				System.out.println("Handled Account msg with seqno: " + msg.getSeqNo() + " at: " +
						DateUtils.getCurrentTimeAsReadableDate() + msg);			
			}	
		}

		@Override
		protected void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
			//Trigger fire of Trade;
			MetaTraderMessage msg = MetaTraderMessageCreator.createMTMsg(new TradeCtx("TEST", LocalDateTime.now(),
					1, true, 123L, 12L, 120L, 130L, 10L));
			try {
				token.getRoutingBlp().enqueue(msg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			log.info("Got trade: " + token.getTriggeringMsg());
		}
		
	}


	@Override
	protected List<BusinessLogicPipeline> getBLPs(MetaTraderConfig config) {
		List<BusinessLogicPipeline> list = new ArrayList<>();
		list.add(new SimpleBLP(config, csd));
		list.add(new SimpleBLP(config, csd));
		return list;
	}
}
