package org.bergefall.iobase.sequence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.iobase.SequencingException;
import org.bergefall.iobase.blp.SequencedBlpQueue;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class SequenceReplayer implements SequencedBlpQueue {

	FileInputStream inStream;
	private static final SystemLoggerIf log = SystemLoggerImpl.get(); 
	Queue<MetaTraderMessage> msgQueue;
	
	public void init(String pathToFile) {
		try {
			inStream = new FileInputStream(pathToFile);
		} catch (FileNotFoundException e) {
			log.error("FATAL could not find file to read. " + e);
		}
	}
	
	public List<MarketData> readEntireFile() {
		List<MarketData> allEnries = new ArrayList<>();
		while(true) {
			try {
				MarketData md = MarketData.parseDelimitedFrom(inStream);
				if (null == md) {
					break;
				}
				allEnries.add(md);
			} catch (IOException e) {
				log.error("FATAL could not find file to read. " + e);
			}
		}
		return allEnries;
	}
	
	public List<MetaTraderMessage> readEntireFileMtm() {
		List<MetaTraderMessage> allEnries = new ArrayList<>();
		while(true) {
			try {
				MetaTraderMessage mtm = MetaTraderMessage.parseDelimitedFrom(inStream);
				if (null == mtm) {
					break;
				}
				allEnries.add(mtm);
			} catch (IOException e) {
				log.error("FATAL could not find file to read. " + e);
			}
		}
		return allEnries;
	}

	private Queue<MetaTraderMessage> enqueueEntireFileMtm() {
		Queue<MetaTraderMessage> allEnries = new LinkedList<>();
		while(true) {
			try {
				MetaTraderMessage mtm = MetaTraderMessage.parseDelimitedFrom(inStream);
				if (null == mtm) {
					break;
				}
				allEnries.add(mtm);
			} catch (IOException e) {
				log.error("FATAL could not find file to read. " + e);
			}
		}
		return allEnries;
	}
	
	@Override
	public boolean enqueue(MetaTraderMessage e) throws InterruptedException {
		throw new SequencingException("Not possible to enqueue in a replay queue provider. " +
				"Check config and/or implementation.");
	}

	@Override
	public boolean enqueue(MetaTraderMessage e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new SequencingException("Not possible to enqueue in a replay queue provider. " +
				"Check config and/or implementation.");
	}

	@Override
	public MetaTraderMessage take() throws InterruptedException {
		if(null == msgQueue) {
			msgQueue = enqueueEntireFileMtm();
		}
		return msgQueue.poll();
	}

	@Override
	public void doSequenceLog(boolean on) {
		return; //Currently not possible to create new logs from replay...
	}
}
