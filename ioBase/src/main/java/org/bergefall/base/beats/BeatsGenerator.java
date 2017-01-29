package org.bergefall.base.beats;

import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;

/**
 * Generator for beats. Beats can be used to control scheduled and/or batched tasks.
 * @author ola
 *
 */
public class BeatsGenerator implements Runnable {
	
	private static final long defaultInterval = 1000L;
	private static final SystemLoggerIf log = SystemLoggerImpl.get();
	private boolean active;
	private long interval;
	private int genNr;
	private BusinessLogicPipeline blp;
	
	public BeatsGenerator(final BusinessLogicPipeline blp,
			final MetaTraderConfig config) {
		this.blp = blp;
		interval = defaultInterval;
		parseConfig(config);
		blp.attachBeatGenerator(this);
		genNr = blp.getBlpNr();
	}
	
	@Override
	public void run() {

		Thread.currentThread().setName("BeatGenerator-" + genNr);
		active = true;
		log.info("Starting beats with interval: " + interval);
		try {
			while (active) {
				blp.enqueue(MetaTraderMessageCreator.createBeat(System.currentTimeMillis()));
				Thread.sleep(interval);
			}
		} catch (InterruptedException e) {
			log.error("Beat generator interrupted. \n" + e);
		} finally {
			log.info("Beat generator now stopped.");
		}

	}
	
	private void parseConfig(MetaTraderConfig config) {
		if (null == config) {
			return;
		}
		Long confedVal = config.getBlpLong("beatInterval");
		interval = (null == confedVal ? interval : confedVal.longValue());
	}
	
	public void stopBeatGenerator() {
		log.info("Beat generator stopped by invoking stopGenerator.");
		active = false;
	}
}
