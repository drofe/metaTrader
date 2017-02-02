package org.bergefall.se.server.strategy.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.calculators.MovingAverage;
import org.bergefall.common.calculators.MovingAveragelongRingBufferImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.common.log.FileHandler;
import org.bergefall.common.log.RotatingFileHandler;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;

/** 
 * A moving average strategy.
 * @author ola
 *
 */
@SuppressWarnings("serial")
public class MovingAverageCalculatingBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	protected static final SystemLoggerIf log = SystemLoggerImpl.get();
	protected IntraStrategyBeanMsg intraMsg;
	protected FileHandler fileHandler;
	protected Map<String, List<MaStrategy>> strategies;	
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		if (Type.MarketData == msg.getMsgType()) {
			this.intraMsg = intraMsg;
			handleNewMarketData(msg.getMarketData());
		}
		return status;
	}

	protected void handleNewMarketData(MarketData marketData) {
		List<MaStrategy> strats = getStrategies(marketData.getInstrument());
		
		for (MaStrategy strat : strats) {
			strat.addNewVal(marketData.getClose());
			try {
				fileHandler.write("Strat: " + strat.getName() +
						", Close price," + marketData.getClose() +
						", Short average, " + strat.getFastAvg() + 
						", Long average, " + strat.getSlowAvg());
			} catch (IOException e) {
				log.error(SystemLoggerIf.getStacktrace(e));
			}
			if (strat.isFastAvgMoreThanSlow()) {
				Long id = csd.getAccountId(strat.getName());
				createOrder(marketData.getInstrument(), 100, marketData.getClose(), true, null == id ? 0 : id.intValue());
			} else if (strat.isSlowAvgMoreThanFast()){
				Long id = csd.getAccountId(strat.getName());
				createOrder(marketData.getInstrument(), 100, marketData.getClose(), false, null == id ? 0 : id.intValue());
			}
		}
	}
	
	protected void createOrder(String symb, 
			long qty, long price, boolean isBid, int accountId) {
		OrderCtx ctx = new OrderCtx(symb, qty, isBid);
		ctx.setPrice(price);
		ctx.setAccountId(accountId);
		intraMsg.addOrder(ctx);
	}
	
	private List<MaStrategy> getStrategies(String symb) {
		List<MaStrategy> strats = strategies.get(symb);
		if (null == strats) {
			strats = generateStrats();
			strategies.put(symb, strats);
		}
		return strats;
	}
	
	private List<MaStrategy> generateStrats() {
		List<MaStrategy> strats = new ArrayList<>();
//		for (AccountCtx acc : csd.getAllAccounts()) {
//			String[] ratios = acc.getName().split("_");
//			try {
//				int shortW = Integer.valueOf(ratios[0]).intValue();
//				int longW = Integer.valueOf(ratios[1]).intValue();
//				strats.add(new MaStrategy(acc.getName(), shortW, longW));
//			} catch (NumberFormatException e) {
//				log.error(SystemLoggerIf.getStacktrace(e));
//			}
//		}
		strats.add(new MaStrategy("10_30", 10, 30));
		return strats;
	}
	
	@Override
	public void initBean(MetaTraderConfig config) {
		strategies = new HashMap<>();
		fileHandler = new RotatingFileHandler("./", false, 60_000, "PriceMAData");
	}
	
	protected class HistoricalAverageRelations {
		private String strategyName;
		//TODO: Add 2 boolean ring buffers of given length.
		
	}
	
	protected class MaStrategy {
		private MovingAverage fastMA;
		private MovingAverage slowMA;
		private String name;
		
		public MaStrategy(String name, int shortWindow, int longWindow) {
			fastMA = new MovingAveragelongRingBufferImpl(shortWindow);
			slowMA = new MovingAveragelongRingBufferImpl(longWindow);
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public long getFastAvg() {
			return fastMA.getAverage();
		}
		
		public long getSlowAvg() {
			return slowMA.getAverage();
		}
		
		public void addNewVal(long val) {
			fastMA.addValue(val);
			slowMA.addValue(val);
		}
		
		public boolean isFastAvgMoreThanSlow() {
			return getFastAvg() > getSlowAvg();
		}
		
		public boolean isSlowAvgMoreThanFast() {
			return getSlowAvg() > getFastAvg();
		}
	}

}
