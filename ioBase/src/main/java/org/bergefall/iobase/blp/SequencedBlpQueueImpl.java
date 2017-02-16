package org.bergefall.iobase.blp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.bergefall.iobase.SequencingException;
import org.bergefall.iobase.sequence.RotatingFileSequencer;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class SequencedBlpQueueImpl implements SequencedBlpQueue {

	protected static TimeUnit defaultUnit = TimeUnit.MILLISECONDS;
	protected static long defaultTimeout = 10_000;
	protected boolean doSeqLogging = false;
	protected RotatingFileSequencer seqLogger;
	protected BlockingQueue<MetaTraderMessage> queue;
	protected String logfileName;

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1630685580164107223L;
	private AtomicLong sequenceNr = new AtomicLong(1);
	
	
	public SequencedBlpQueueImpl(int capacity) {
		this(capacity, true);
	}
	
	public SequencedBlpQueueImpl(int capacity, boolean fair) {
		queue = new ArrayBlockingQueue<MetaTraderMessage>(capacity, fair);
	}
	
	@Override
	public void setSequenceLogFileName(String name) {
		if (null != logfileName) {
			throw new RuntimeException("Filename is already set! Initialization is done in wrong order!");
		}
		logfileName = name;
	}
	
	@Override
	public void doSequenceLog(boolean on) {
		doSeqLogging = on;
		if (doSeqLogging && null == seqLogger) {
			seqLogger = new RotatingFileSequencer();
			seqLogger.init(logfileName);
		}
	}
	
	public boolean enqueue(MetaTraderMessage e, long timeout, TimeUnit unit) throws InterruptedException {
		if (e.getSeqNo() > 0) {
			throw new SequencingException("MTMessage already sequenced when added to sequenced queue!");
		}
		return queue.offer(e, timeout, unit);
	}
	
	
	public boolean enqueue(MetaTraderMessage e) throws InterruptedException {
		return enqueue(e, defaultTimeout, defaultUnit);
	}

	@Override
	public MetaTraderMessage take() throws InterruptedException {
		MetaTraderMessage tmp = queue.take();
		//Performance tests shows that it is extremely cheap to create a new builder.
		//If future tests shows otherwise lets build a pool.
		MetaTraderMessage msg = MetaTraderMessage.newBuilder(tmp)
				.setSeqNo(sequenceNr.getAndIncrement())
				.addTimeStamps(System.currentTimeMillis())
				.build();
		if (doSeqLogging) {
			seqLogger.writeMetaTraderMessage(msg);
		}

		return msg;
	}
}
