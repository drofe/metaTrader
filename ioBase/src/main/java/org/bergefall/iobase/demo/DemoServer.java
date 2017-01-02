package org.bergefall.iobase.demo;

import org.bergefall.common.DateUtils;
import org.bergefall.iobase.blp.BusinessLogicPipelineImpl;
import org.bergefall.iobase.blp.BusinessLogicPipline;
import org.bergefall.iobase.server.MetaTraderServerApplication;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class DemoServer extends MetaTraderServerApplication {

	private static final String configFile = "../common/src/main/resources/ConfigExample.properties";

	public static void main(String[] args) throws InterruptedException {
		DemoServer ds = new DemoServer();
		ds.initServer(configFile);
		ds.startListening();
	}
	
	private static class SimpleBLP extends BusinessLogicPipelineImpl {

		@Override
		protected void handleAccounts(MetaTraderMessage msg) {
			if ( msg.getSeqNo() % 10 == 0) {
				System.out.println("Handled Account msg with seqno: " + msg.getSeqNo() + " at: " +
						DateUtils.getCurrentTimeAsReadableDate() + msg);			
			}			
		}

		@Override
		protected void handleInstrument(MetaTraderMessage msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleMarketData(MetaTraderMessage msg) {
			if ( msg.getSeqNo() % 10 == 0) {
				System.out.println("Handled MarketData msg with seqno: " + msg.getSeqNo() + " at: " +
						DateUtils.getCurrentTimeAsReadableDate() + msg);			
			}
		}
		
	}


	@Override
	protected BusinessLogicPipline getBLP() {
		return new SimpleBLP();
	}
}
