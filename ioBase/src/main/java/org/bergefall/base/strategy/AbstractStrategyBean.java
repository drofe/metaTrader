package org.bergefall.base.strategy;

import java.io.Serializable;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

@SuppressWarnings("serial")
public abstract class AbstractStrategyBean<IN, OUT> implements Serializable {
	protected static final SystemLoggerIf log = SystemLoggerImpl.get();
	protected Status status;
	/**
	 * The triggering message.
	 */
	protected MetaTraderMessage msg;
	protected BusinessLogicPipeline routingPipeline;
	protected CommonStrategyData csd;

	
	public OUT executeBean(StrategyToken token, IN intraMsg) {
		status = token.getStatus();
		msg = token.getTriggeringMsg();
		routingPipeline = token.getRoutingBlp();
		csd = token.getCsd();
		return execute(token, intraMsg);
		
	}
	
	public abstract OUT execute(StrategyToken token, IN intraMsg);
	
	public void initBean(MetaTraderConfig config) {
		
	}
	public void shutdownHook() {
		
	}
}
