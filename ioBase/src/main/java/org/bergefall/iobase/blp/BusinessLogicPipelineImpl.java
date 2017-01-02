package org.bergefall.iobase.blp;

import java.util.concurrent.atomic.AtomicInteger;

import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

/**
 * This class is meant to be created as the (or one of the) main thread for the single threaded 
 * business logic i MetaTrader servers. One server can hold several BLPs but they shall
 * not execute the same strategy or involve the same instruments.
 * @author ola
 *
 */
public abstract class BusinessLogicPipelineImpl implements BusinessLogicPipline {

	private static final AtomicInteger cBLPNr = new AtomicInteger(0);
	private boolean active;
	private static final SystemLoggerIf log;
	private Integer thisBlpNumber;
	private final SequencedBlpQueue sequencedQueue;
	private int defaultSeqQueueSize = 1024 * 4;
	
	static {
		log = SystemLoggerImpl.get();
	}
	
	public BusinessLogicPipelineImpl () {
		thisBlpNumber = Integer.valueOf(cBLPNr.getAndIncrement());
		sequencedQueue = new SequencedBlpQueueImpl(defaultSeqQueueSize);
		sequencedQueue.doSequenceLog(true);
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("BLP-" + getBlpNr());
		active = true;
		while(active) {
			try {
				MetaTraderMessage msg = sequencedQueue.take();
				fireHandlers(msg);
			} catch (Throwable th) {
				log.error("Something went wrong in BLP " + getBlpNr() + ": " + th);
			}
		}
	}
	
	protected Integer getBlpNr () {
		return thisBlpNumber;
	}
	
	@Override
	public boolean enqueue(MetaTraderMessage msg) throws InterruptedException {
		boolean res = sequencedQueue.enqueue(msg);
		if (!res) {
			log.error("FATAL: Failed to enqueue msg: " + msg);
		}
		return res;
	}
	
	@Override
	public void shutdown() {
		active = false;
	}
	
	private void fireHandlers(MetaTraderMessage msg) {
		MetaTraderMessage.Type type = msg.getMsgType();
		
		switch (type) {
		case Account :
			handleAccounts(msg);
			break;
		case MarketData : 
			handleMarketData(msg);
			break;
		case Instrument : 
			handleInstrument(msg);
			break;
			default:
				break;
		}
	}
	
	protected abstract void handleAccounts(MetaTraderMessage msg);
	
	protected abstract void handleInstrument(MetaTraderMessage msg);
	
	protected abstract void handleMarketData(MetaTraderMessage msg);
}
