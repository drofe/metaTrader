package org.bergefall.iobase.blp;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public interface BusinessLogicPipeline extends Runnable {

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

	/**
	 * Attaches a beat generator for this BLP. Not mandatory.
	 * @param beatGen
	 */
	public void attachBeatGenerator(BeatsGenerator beatGen);

	/**
	 * Gets the BLP number for this BLP. Unique  within server.
	 * 0 is reserved for routing pipeline.
	 * @return
	 */
	public Integer getBlpNr();

	/**
	 * Get the common data store.
	 * @return The single instance of the CSD.
	 */
	public CommonStrategyData getCSD();
	
	/**
	 * Set the router BLP for this BLP.
	 * @param router
	 */
	public void setRoutingBlp(BusinessLogicPipeline router);

}
