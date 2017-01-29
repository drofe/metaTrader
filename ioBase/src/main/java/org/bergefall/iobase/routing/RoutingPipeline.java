package org.bergefall.iobase.routing;

import java.util.LinkedList;
import java.util.List;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.DateUtils;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipelineBase;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class RoutingPipeline extends BusinessLogicPipelineBase {

	protected static String routingStrategy = "routeStrategy";
	
	public RoutingPipeline(MetaTraderConfig config) {
		super(config);
		routingPipeline = this;
	}
		
	@Override
	protected void fireHandlers(MetaTraderMessage msg) {
		runStrategy(routingStrategy, getNewToken(msg), getNewIntraBeanMsg());
	}
	
	protected void buildStrategies() {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> routingStrat = new LinkedList<>();
		RoutingClientBean routingBean = new RoutingClientBean();
		routingBean.parseConfig(config);
		routingStrat.add(routingBean);
		strategyMap.put(routingStrategy, routingStrat);
	}
	
	@Override
	protected StrategyToken getNewToken(MetaTraderMessage msg) {
		StrategyToken token = new StrategyToken(DateUtils.getDateTimeFromTimestamp(msg.getTimeStamps(0)), csd, this);
		token.setTriggeringMsg(msg);
		return token;
	}
}
