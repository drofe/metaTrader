package org.bergefall.iobase.blp;

import org.bergefall.base.beats.BeatsGenerator;
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

	/**
	 * Attaches a beat generator for this BLP. Not mandatory.
	 * @param beatGen
	 */
	public void attachBeatGenerator(BeatsGenerator beatGen);

	/**
	 * Gets the BLP number for this BLP. Unique  within server.
	 * @return
	 */
	public Integer getBlpNr();
	
	
}
