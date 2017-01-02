package org.bergefall.iobase.blp;

import java.util.concurrent.TimeUnit;

import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public interface SequencedBlpQueue {

	/**
	 * Enqueue stuff in this sequencer/queue.
	 * @param MetaTraderMessage
	 * @return true if message got added to queue, false otherwise.
	 * @throws InterruptedException
	 */
	public boolean enqueue(MetaTraderMessage e) throws InterruptedException;

	public boolean enqueue(MetaTraderMessage e, long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
	 * Use to take item from queue
	 * @return Message
	 */
	public MetaTraderMessage take() throws InterruptedException;
	
	/**
	 * Turns sequence logging to file on/off
	 * @param on
	 */
	public void doSequenceLog(boolean on);
	
}
