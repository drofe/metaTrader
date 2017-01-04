package org.bergefall.iobase.blp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.base.strategy.basicbeans.AddDataToCommonData;
import org.bergefall.common.DateUtils;
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

	protected static final String PreMDStrategy = "preMDStrategy";
	
	private static final AtomicInteger cBLPNr = new AtomicInteger(0);
	private boolean active;
	private static final SystemLoggerIf log;
	private Integer thisBlpNumber;
	private final SequencedBlpQueue sequencedQueue;
	private int defaultSeqQueueSize = 1024 * 4;
	private CommonStrategyData csd;
	private Map<String, List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>>> strategyMap;
	
	static {
		log = SystemLoggerImpl.get();
	}
	
	public BusinessLogicPipelineImpl () {
		thisBlpNumber = Integer.valueOf(cBLPNr.getAndIncrement());
		sequencedQueue = new SequencedBlpQueueImpl(defaultSeqQueueSize);
		sequencedQueue.doSequenceLog(true);
		csd = getOrCreateCSD();
		strategyMap = new HashMap<>();
		buildStrategies();
	}
	
	protected CommonStrategyData getOrCreateCSD() {
		if (null == csd) {
			return new CommonStrategyData();
		} else {
			return csd;
		}
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
	
	protected void fireHandlers(MetaTraderMessage msg) {
		MetaTraderMessage.Type type = msg.getMsgType();
		
		switch (type) {
		case Account :
			handleAccounts(msg);
			break;
		case MarketData : 
			handleMarketDataInternal(msg);
			break;
		case Instrument : 
			handleInstrument(msg);
			break;
			default:
				break;
		}
	}
	
	protected void handleMarketDataInternal(MetaTraderMessage msg) {
		StrategyToken token = new StrategyToken(DateUtils.getDateTimeFromTimestamp(msg.getTimeStamps(0)), 
				csd);
		token.setTriggeringMsg(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		runStrategy(PreMDStrategy, token, intraMsg);
		handleMarketData(token, intraMsg);
	}
	
	protected IntraStrategyBeanMsg getNewIntraBeanMsg() {
		return new IntraStrategyBeanMsg();
	}
	
	// This is temp stuff until it is controlled via config.
	private void buildStrategies() {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preMd = new LinkedList<>();
		preMd.add(new AddDataToCommonData());
		strategyMap.put(PreMDStrategy, preMd);
	}
	
	protected Status runStrategy(String strategyName, StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> strategy =
				strategyMap.get(strategyName);
		if (null == strategy) {
			log.error("Unable to find strategy: " + strategyName);
			return new Status(Status.ERROR);
		}
		Status status = null;
		for (AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status> bean : strategy) {
			status = bean.execute(token, intraMsg);
			if (null == status || status.getCode() >= Status.ERROR) {
				log.error("Error occurred in bean: " + bean.getClass().getName() + "\n\t" +
						"in strategy: " + strategyName + "\n\t" + 
						"code: " + status.getCode() + (null != status.getMessage() ? ", message: " + 
						status.getMessage() : ""));
				break;
			}			
		}
		return status;
	}
	
	protected abstract void handleAccounts(MetaTraderMessage msg);
	
	protected abstract void handleInstrument(MetaTraderMessage msg);
	
	protected abstract void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	
}
