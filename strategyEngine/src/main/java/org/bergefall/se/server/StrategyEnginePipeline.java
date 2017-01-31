package org.bergefall.se.server;

import java.util.LinkedList;
import java.util.List;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.base.strategy.basicbeans.BackTestBean;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipelineImpl;
import org.bergefall.se.server.strategy.beans.MovingAverageCalculatingBean;

public class StrategyEnginePipeline extends BusinessLogicPipelineImpl {

	private static final String mas = "movingAverageStrategy";
	private static final String backtestStrategy = "backtestStrat";
	
	public StrategyEnginePipeline(MetaTraderConfig config) {
		super(config);

	}

	@Override 
	protected void buildStrategies() {
		super.buildStrategies();
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> movStrat = new LinkedList<>();
		MovingAverageCalculatingBean movAvgBean = new MovingAverageCalculatingBean();
		movAvgBean.initBean(config);
		movStrat.add(movAvgBean);
		strategyMap.put(mas, movStrat);
		
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> bTestStrat = new LinkedList<>();
		BackTestBean backTestBean = new BackTestBean();
		backTestBean.initBean(config);
		bTestStrat.add(backTestBean);
		strategyMap.put(backtestStrategy, bTestStrat);
	}
	@Override
	protected void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		runStrategy(mas, token, intraMsg);
	}

	@Override
	protected void handleBeats(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleOrders(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		runStrategy(backtestStrategy, token, intraMsg);
	}

	@Override
	protected void handleTrades(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		runStrategy(backtestStrategy, token, intraMsg);
	}

}
