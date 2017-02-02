package org.bergefall.iobase.blp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.DateUtils;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

/**
 * This class is the base for all types of Business Logic Pipelines.
 * @author ola
 *
 */
public abstract class BusinessLogicPipelineBase implements BusinessLogicPipeline {

	protected static final SystemLoggerIf log;
	protected static final AtomicInteger cBLPNr = new AtomicInteger(0);
	protected CommonStrategyData csd;
	protected String blpName = "";
	protected MetaTraderConfig config;
	protected BeatsGenerator beatGenerator;
	protected BusinessLogicPipeline routingPipeline;
	protected Map<String, List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>>> strategyMap;
	protected Integer thisBlpNr;
	protected String sequenceLogFileName;
	
	
	private boolean active;
	private final SequencedBlpQueue sequencedQueue;
	private int defaultSeqQueueSize = 1024 * 4;


	static {
		log = SystemLoggerImpl.get();
	}
	
	public BusinessLogicPipelineBase(MetaTraderConfig config, int blpNr) {
		this.config = config;
		this.sequencedQueue = new SequencedBlpQueueImpl(defaultSeqQueueSize);		
		this.csd = getOrCreateCSD();
		this.strategyMap = new HashMap<>();
		buildStrategies();
		if (blpNr > 0 && blpNr < cBLPNr.get()) {
			log.error("Trying to set BLP nr that might already been taken. Using generated instead.");
			thisBlpNr = cBLPNr.getAndIncrement();
		} else if (blpNr > 0) {
			thisBlpNr = Integer.valueOf(blpNr);
			cBLPNr.set(blpNr);
		} else {
			thisBlpNr = cBLPNr.getAndIncrement();
		}
		setBlpIdentifiers(config);
		this.sequencedQueue.setSequenceLogFileName(sequenceLogFileName);
		this.sequencedQueue.doSequenceLog(true);
	}
	
	public BusinessLogicPipelineBase(MetaTraderConfig config) {
		this(config, -1);
	}

	public BusinessLogicPipelineBase(MetaTraderConfig config, 
			BusinessLogicPipeline routingBlp) {
		this(config);
		this.routingPipeline = routingBlp;

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
		Thread.currentThread().setName(blpName + getBlpNr());
		active = true;
		MetaTraderMessage msg = null;
		while(active) {
			try {
				msg = sequencedQueue.take();
				fireHandlers(msg);
			} 
			catch (Exception e) {
				log.error("Caught an exception while handling message: " + msg + SystemLoggerIf.getStacktrace(e));
			}
			catch (Throwable th) {
				log.error("Caught an exception while handling message: " + msg + SystemLoggerIf.getStacktrace(th));
			}
		}
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
		active = false;
		
		//Call all shutdown hooks.
		for (List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> strategy : strategyMap.values()) {
			for( AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status> bean : strategy) {
				bean.shutdownHook();
			}
		}
	}

	@Override
	public void attachBeatGenerator(BeatsGenerator beatGen) {
		beatGenerator = beatGen;
	}

	protected void fireHandlers(MetaTraderMessage msg) {
		return; //Meant to be overridden.
	}

	protected IntraStrategyBeanMsg getNewIntraBeanMsg() {
		return new IntraStrategyBeanMsg();
	}

	protected StrategyToken getNewToken(MetaTraderMessage msg) {
		StrategyToken token = new StrategyToken(DateUtils.getDateTimeFromTimestamp(msg.getTimeStamps(0)), csd, routingPipeline);
		token.setTriggeringMsg(msg);
		return token;
	}

	// This is temp stuff until it is controlled via config.
	protected void buildStrategies() {
	}

	protected void addBeanToStrategy(AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status> bean,
			List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> strat) {
		bean.initBean(config);
		strat.add(bean);
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
			status = bean.executeBean(token, intraMsg);
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

	protected abstract void setBlpIdentifiers(MetaTraderConfig config);
	
}
