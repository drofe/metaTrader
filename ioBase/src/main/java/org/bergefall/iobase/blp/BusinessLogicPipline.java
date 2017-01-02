package org.bergefall.iobase.blp;

import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public interface BusinessLogicPipline extends Runnable {

	/**
	 * Enqueues and sequences a message to be handled by this Business Logic Pipeline.
	 * @param msg
	 * @throws InterruptedException 
	 */
	public boolean enqueue(MetaTraderMessage msg) throws InterruptedException;

	/**
	 * Shuts down this business pipeline.
	 */
	public void shutdown();
	
//	abstract void handleAccounts(MetaTraderMessage msg);
//	
//	abstract void handleInstrument(MetaTraderMessage msg);
//	
//	abstract void handleMarketData(MetaTraderMessage msg);
}
