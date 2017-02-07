package org.bergefall.se.server;

import java.util.LinkedList;
import java.util.List;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.base.strategy.basicbeans.BackTestBean;
import org.bergefall.base.strategy.basicbeans.OrderGenerator;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipelineImpl;
import org.bergefall.se.server.strategy.beans.EquityLineGeneratorBean;
import org.bergefall.se.server.strategy.beans.MovingAverageCalculatingBean;
import org.bergefall.se.server.strategy.beans.TradeHandlingBean;

public class StrategyEnginePipeline extends BusinessLogicPipelineImpl {

	private static final String mas = "movingAverageStrategy";
	private static final String backtestStrategy = "backtestStrat";
	private static final String tradeStrategy = "tradeStrat";
	
	public StrategyEnginePipeline(MetaTraderConfig config) {
		super(config);
	}

	@Override 
	protected void buildStrategies() {
		super.buildStrategies();
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> strat = new LinkedList<>();
		addBeanToStrategy(new MovingAverageCalculatingBean(), strat);
		addBeanToStrategy(new OrderGenerator(), strat);
		addBeanToStrategy(new BackTestBean(), strat);
		addBeanToStrategy(new TradeHandlingBean(), strat);
		addBeanToStrategy(new EquityLineGeneratorBean(), strat);
		strategyMap.put(mas, strat);
		
		strat = new LinkedList<>();		
		strategyMap.put(backtestStrategy, strat);
		
		strat = new LinkedList<>();
		strategyMap.put(tradeStrategy, strat);

	}
		
	@Override
	protected void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
	}

	@Override
	protected void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
	}

	@Override
	protected void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		runStrategy(mas, token, intraMsg);
	}

	@Override
	protected void handleBeats(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
	}

	@Override
	protected void handleOrders(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		runStrategy(backtestStrategy, token, intraMsg);
	}

	@Override
	protected void handleTrades(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		runStrategy(tradeStrategy, token, intraMsg);
	}

}
