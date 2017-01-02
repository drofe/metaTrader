package org.bergefall.iobase.test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.bergefall.iobase.BlpTestBase;
import org.bergefall.iobase.sequence.RotatingFileSequencer;
import org.bergefall.iobase.sequence.SequenceReplayer;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class WriteProtobufDataTest extends BlpTestBase {

	RotatingFileSequencer mLogger;
	SequenceReplayer mReader;
	protected static final String cTestFile = "/tmp/TestFile.seq";
	@Before
	public void setup() {
		mLogger = new RotatingFileSequencer();
		mLogger.init(null);
		mReader = new SequenceReplayer();
	}
	@After
	public void cleanup() {
		File file = new File(cTestFile);
		if (file.exists()) {
			file.delete();
		}
	}
	
	@Test
	public void testSimpleWriteRead() {
		int nr = 10;
		for (int i = 1; i <= nr; i++) {
			MarketData md = MetaTraderMessageCreator.createMD(createPriceCtx(LocalDateTime.of(2016, 12, i, 0, 0)));
			mLogger.writeMarketData(md);
		}
		mReader.init(cTestFile);
		List<MarketData> writtenRecords = mReader.readEntireFile();
		Assert.assertEquals(nr, writtenRecords.size());
		for (MarketData md : writtenRecords) {
			System.out.println(md.toString());
		}
		
	}
	
	@Test
	public void testWriteReadMetaTraderMsg() {
		int nr = 10;
		MetaTraderMessage mtm;
		for (int i = 1; i <= nr; i++) {
			if (0 == i % 2) {
				mtm = MetaTraderMessageCreator.createMTMsg(createPriceCtx(LocalDateTime.of(2016, 12, i, 0, 0)));
			} else {
				mtm = MetaTraderMessageCreator.createMTMsg(createAccountCtx(i));
			}
			mLogger.writeMetaTraderMessage(mtm);
		}
		mReader.init(cTestFile);
		List<MetaTraderMessage> writtenRecords = mReader.readEntireFileMtm();
		Assert.assertEquals(nr, writtenRecords.size());
		for (MetaTraderMessage mtmsg : writtenRecords) {
			System.out.println(mtmsg.toString());
		}
		
	}

}
