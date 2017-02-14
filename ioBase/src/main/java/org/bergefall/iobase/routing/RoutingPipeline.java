package org.bergefall.iobase.routing;

import java.util.LinkedList;
import java.util.List;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.DateUtils;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.iobase.blp.BusinessLogicPipelineBase;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class RoutingPipeline extends BusinessLogicPipelineBase {

	protected static String routingStrategy = "routeStrategy";

	public RoutingPipeline(MetaTraderConfig config, CommonStrategyData csd) {
		super(config, 0, csd);//Zero Blp nr reserved for routing.
	}

	@Override
	protected void fireHandlers(MetaTraderMessage msg) {
		runStrategy(routingStrategy, getNewToken(msg), getNewIntraBeanMsg());
	}

	@Override
	protected void setBlpIdentifiers(MetaTraderConfig config) {
		blpName = "RoutingPipeline-";
		sequenceLogFileName = "RoutingSequencedMsgs-" + thisBlpNr;
	}
	
	@Override
	protected void buildStrategies() {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> routingStrat = new LinkedList<>();
		RoutingClientBean routingBean = new RoutingClientBean();
		routingBean.initBean(config);
		routingStrat.add(routingBean);
		strategyMap.put(routingStrategy, routingStrat);
	}

	@Override
	protected StrategyToken getNewToken(MetaTraderMessage msg) {
		StrategyToken token = new StrategyToken(DateUtils.getDateTimeFromTimestamp(msg.getTimeStamps(0)), csd, this);
		token.setTriggeringMsg(msg);
		return token;
	}

	@Override
	public Integer getBlpNr() {
		return Integer.valueOf(0);
	}

	@Override
	public void setRoutingBlp(BusinessLogicPipeline router) {
		return; //Not applicable for the router itself.
	}
}
