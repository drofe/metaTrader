package org.bergefall.iobase.sequence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.bergefall.common.log.sequence.SequenceLogger;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class RotatingFileSequencer implements SequenceLogger {

	private FileOutputStream fop;
	private File file;
	private static final SystemLoggerIf log = SystemLoggerImpl.get();
	private String fileName;
	
	public void init(String logFileNamePrefix) {
		if (null == logFileNamePrefix)  {
			fileName = "MetaTraderLog-" + LocalDateTime.now().toString() + ".log";
		} else {
			fileName = logFileNamePrefix + LocalDateTime.now().toString() + ".log";
		}
		file = new File(fileName);
		try {
			file.createNewFile();
			fop = new FileOutputStream(file);
		} catch (IOException e) {
			log.error("FATAL: failed to create sequence file. " + e.getMessage());
		}
	}
	
	@Override
	public void setSynchLogging(boolean on) {
		return; //TODO: implement.
	}
	
	public void writeMarketData(MarketData md) {
		try {
			md.writeDelimitedTo(fop);
		} catch (IOException e) {
			log.error("FATAL: failed to write to sequence file. " + e);
		}
	}
	
	public void writeMetaTraderMessage(MetaTraderMessage mtMsg) {
		try {
			mtMsg.writeDelimitedTo(fop);
		} catch (IOException e) {
			log.error("FATAL: failed to write to sequence file. " + e);
		}
	}

}
