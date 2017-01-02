package org.bergefall.iobase.blp;

import java.time.LocalDateTime;

import org.bergefall.iobase.BlpTestBase;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.junit.Before;
import org.junit.Test;

public class SequencerTest extends BlpTestBase {

	SequencedBlpQueue mQueue;
	MetaTraderMessage[] arr;
	@Before
	public void setup() {
		mQueue = new SequencedBlpQueueImpl(60_000);
		mQueue.doSequenceLog(true);
		arr = new MetaTraderMessage[60_000];
	}
	
	@Test
	public void testQueuePerformance() throws InterruptedException {
		int warmup = 25000;
		int test = 35000;
		//Add stuff to queue.
		for (int i = 1; i <= warmup + test; i++) {
			MetaTraderMessage mtm = MetaTraderMessageCreator.createMTMsg(createPriceCtx(LocalDateTime.of(2016, 12, 
					i%30 +1, 0, 0)));
			mQueue.enqueue(mtm);
		}
		//Warmup take code
		for (int j = 0; j < warmup; j++) {
			mQueue.take();
		}
		
		//Test performance
		long start = System.currentTimeMillis();
		for (int k = 0; k < test; k++) {
			arr[k] = mQueue.take();
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("It took: " + time + "ms to take " + arr.length + "objects.");
	}

}
