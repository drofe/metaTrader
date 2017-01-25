package org.bergefall.iobase.blp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.base.strategy.basicbeans.AddDataToCommonData;
import org.bergefall.base.strategy.basicbeans.StoreTradeToDb;
import org.bergefall.common.DateUtils;
import org.bergefall.common.config.MetaTraderConfig;
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

	protected static final SystemLoggerIf log;
	
	private static final String PreMDStrategy = "preMDStrategy";
	private static final String PreAccountStrategy = "preAccStrategy";
	private static final String PreInstrumentStrategy = "preInstrStrategy";
	private static final String PreBeatStrategy = "preBeatStrategy";
	private static final String PreOrderStrategy = "preOrderStrategy";
	private static final String preTradeStrategy = "preTradeStrategy";
		
	private static final AtomicInteger cBLPNr = new AtomicInteger(0);
	private boolean active;
	
	private Integer thisBlpNumber;
	private final SequencedBlpQueue sequencedQueue;
	private int defaultSeqQueueSize = 1024 * 4;
	private CommonStrategyData csd;
	private Map<String, List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>>> strategyMap;
	private MetaTraderConfig config;
	private boolean runPreMdStrat = true;
	private boolean runPreAccStrat = true;
	private boolean runPreInstrStrat = true;
	private boolean runPreBeatStrat = true;
	private boolean runPreOrderStrat = true;
	private boolean runPreTradeStrat = true;
	private BeatsGenerator beatGenerator;
	
	
	static {
		log = SystemLoggerImpl.get();
	}
	
	public BusinessLogicPipelineImpl (MetaTraderConfig config) {
		this.config = config;
		parseConfig();
		this.thisBlpNumber = Integer.valueOf(cBLPNr.getAndIncrement());
		this.sequencedQueue = new SequencedBlpQueueImpl(defaultSeqQueueSize);
		this.sequencedQueue.doSequenceLog(true);
		this.csd = getOrCreateCSD();
		this.strategyMap = new HashMap<>();
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
	
	@Override
	public Integer getBlpNr() {
		return thisBlpNumber;
	}
	
	@Override
	public boolean enqueue(MetaTraderMessage msg) throws InterruptedException {
		if (null == msg) {
			return false;
		}
		boolean res = sequencedQueue.enqueue(msg);
		if (!res) {
			log.error("FATAL: Failed to enqueue msg: " + msg);
		}
		return res;
	}
	
	@Override
	public void shutdown() {
		if (null != beatGenerator) {
			beatGenerator.stopBeatGenerator();
		}
		active = false;
		
	}
	
	@Override
	public void attachBeatGenerator(BeatsGenerator beatGen) {
		beatGenerator = beatGen;
	}
	
	protected void parseConfig() {
		runPreMdStrat = config.getBlpBoolean("runPreMDStrategy");
		runPreAccStrat = config.getBlpBoolean("runPreAccStrategy");
		runPreInstrStrat = config.getBlpBoolean("runPreInstrStrategy");
		runPreBeatStrat = config.getBlpBoolean("runPreBeatStrategy");
		runPreTradeStrat = config.getBlpBoolean("runPreTradeStrategy");
	}
	
	protected void fireHandlers(MetaTraderMessage msg) {
		MetaTraderMessage.Type type = msg.getMsgType();
		
		switch (type) {
		case Account :
			handleAccountsInternal(msg);
			break;
		case MarketData : 
			handleMarketDataInternal(msg);
			break;
		case Instrument:
			handleInstrumentInternal(msg);
			break;
		case Beat:
			handleBeatsInternal(msg);
			break;
		case Order:
			handleOrderInternal(msg);
			break;
		case Trade:
			handleTradeInternal(msg);
			break;
		default:
			break;
		}
	}
	
	private void handleInstrumentInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreInstrStrat) {
			runStrategy(PreInstrumentStrategy, token, intraMsg);
		}
		handleInstrument(token, intraMsg);
	}
	
	private void handleTradeInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreTradeStrat) {
			runStrategy(preTradeStrategy, token, intraMsg);
		}
		handleTrades(token, intraMsg);
	}
	
	private void handleOrderInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreOrderStrat) {
			runStrategy(PreOrderStrategy, token, intraMsg);
		}
		handleOrders(token, intraMsg);
	}
	
	private void handleBeatsInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreBeatStrat) {
			runStrategy(PreBeatStrategy, token, intraMsg);
		}
		handleBeats(token, intraMsg);
	}

	private void handleAccountsInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreAccStrat) {
			runStrategy(PreAccountStrategy, token, intraMsg);
		}
		handleAccounts(token, intraMsg);
	}

	private void handleMarketDataInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreMdStrat) {
			runStrategy(PreMDStrategy, token, intraMsg);
		}
		handleMarketData(token, intraMsg);
	}
	
	protected IntraStrategyBeanMsg getNewIntraBeanMsg() {
		return new IntraStrategyBeanMsg();
	}
	
	protected StrategyToken getNewToken(MetaTraderMessage msg) {
		StrategyToken token = new StrategyToken(DateUtils.getDateTimeFromTimestamp(msg.getTimeStamps(0)), csd);
		token.setTriggeringMsg(msg);
		return token;
	}
	
	// This is temp stuff until it is controlled via config.
	private void buildStrategies() {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preMd = new LinkedList<>();
		AddDataToCommonData addData = new AddDataToCommonData();
		addData.parseConfig(config);
		preMd.add(addData);
		strategyMap.put(PreMDStrategy, preMd);
		
		
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preTrade = new LinkedList<>();
		StoreTradeToDb storeTrade = new StoreTradeToDb();
		storeTrade.parseConfig(config);
		preTrade.add(storeTrade);
		strategyMap.put(preTradeStrategy, preTrade);
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
	
	protected abstract void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleBeats(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleOrders(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleTrades(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
}
