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
import org.bergefall.common.MetaTraderConstants;
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
	protected Map<String, HistoricalAverageRelations> historicalValues;
	protected boolean writeToFile;
	
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
			writeToFile(strat, marketData);
			HistoricalAverageRelations histVals = addToHistory(strat);
			if (strat.isFastAvgGreaterThanSlow() && !histVals.getIsFastG(1)) {
				Integer id = csd.getAccountId(strat.getName());
				createOrder(marketData.getInstrument(), marketData.getClose(), false, 
						null == id ? 0 : id.intValue());
			} else if (strat.isSlowAvgGreaterThanFast() && !histVals.getIsSlowG(1)){
				Integer id = csd.getAccountId(strat.getName());
				createOrder(marketData.getInstrument(), marketData.getClose(), true, 
						null == id ? 0 : id.intValue());
			}
		}
	}
	
	protected void writeToFile(MaStrategy strat, MarketData marketData) {
		if(false == writeToFile) {
			return;
		}
		try {
			fileHandler.write("Strat: " + strat.getName() +
					", Close price," + marketData.getClose() +
					", Short average, " + strat.getFastAvg() + 
					", Long average, " + strat.getSlowAvg());
		} catch (IOException e) {
			log.error(SystemLoggerIf.getStacktrace(e));
		}
	}
	
	protected void createOrder(String symb, long price, boolean isAsk, int accountId) {
		AccountCtx accCtx = csd.getAccount(accountId);
		
		long qty;
		if (isAsk) {
			qty = accCtx.getPosition(symb).getLongQty();
		} else {
			qty = getBuyQty(accCtx, price);
		}
		OrderCtx ctx = new OrderCtx(symb, qty, isAsk);
		ctx.setPrice(price);
		ctx.setAccountId(accountId);
		intraMsg.addOrder(ctx);
	}
	
	private long getBuyQty(AccountCtx accCtx, long price) {
		long cashAmount = accCtx.getPosition(MetaTraderConstants.CASH).getLongQty();
		if (cashAmount <= 0) {
			return 0L;
		}
		return cashAmount / price;
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
		historicalValues = new HashMap<>();
		writeToFile = null == config ? false : config.getBooleanProperty(this.getClass().getName(), "writeToFile");
		if (writeToFile) {
			fileHandler = new RotatingFileHandler("./", false, 60_000, "PriceMAData");
		}
	}
	
	private HistoricalAverageRelations addToHistory(MaStrategy strat) {
		HistoricalAverageRelations rel = historicalValues.get(strat.getName());
		if (null == rel) {
			rel = new HistoricalAverageRelations(strat.getName(), 10);
			historicalValues.put(strat.getName(), rel);
		}
		rel.addFastVal(strat.getFastAvg());
		rel.addSlowVal(strat.getSlowAvg());
		rel.addisFastG(strat.isFastAvgGreaterThanSlow());
		rel.addIsSlowG(strat.isSlowAvgGreaterThanFast());
		
		return rel;
	}
	
	protected class HistoricalAverageRelations {
		private final String strategyName;
		private boolean[] isFastGreater;
		private int isFastGPos;
		private boolean[] isSlowGreater;
		private int isSlowGPos;
		private long[] slows;
		private int slowPos;
		private long[] fasts;
		private int fastPos;

		public HistoricalAverageRelations(final String strategyName, int bufferSize) {
			this.strategyName = strategyName;
			isFastGreater = new boolean[bufferSize];
			isSlowGreater = new boolean[bufferSize];
			slows = new long[bufferSize];
			fasts = new long[bufferSize];
		}
		
		public String getStrategyName() {
			return strategyName;
		}
		
		public Boolean getIsSlowG(int nrTicksBack) {
			if (nrTicksBack >= isSlowGreater.length ||
					nrTicksBack < 0) {
				return null;
			}
			int pos = isSlowGPos - nrTicksBack - 1;
			if (pos < 0) {
				pos += isSlowGreater.length;
			}
			return Boolean.valueOf(isSlowGreater[pos]);
		}
		
		public Boolean getIsFastG(int nrTicksBack) {
			if (nrTicksBack >= isFastGreater.length ||
					nrTicksBack < 0) {
				return null;
			}
			int pos = isFastGPos - nrTicksBack - 1;
			if (pos < 0) {
				pos += isFastGreater.length;
			}
			return Boolean.valueOf(isFastGreater[pos]);
		}
		
		public long getSlowAvg(int nrTicksBack) {
			if (nrTicksBack >= slows.length ||
					nrTicksBack < 0) {
				return 0L;
			}
			int pos = slowPos - nrTicksBack - 1;
			if (pos < 0) {
				pos += slows.length;
			}
			return slows[pos];
		}
		
		public long getFastAvg(int nrTicksBack) {
			if (nrTicksBack >= fasts.length ||
					nrTicksBack < 0) {
				return 0L;
			}
			int pos = fastPos - nrTicksBack - 1;
			if (pos < 0) {
				pos += fasts.length;
			}
			return fasts[pos];
		}
		
		public void addSlowVal(long val) {
			slows[slowPos] = val;
			slowPos++;
			if (slows.length == slowPos) {
				slowPos = 0;
			}
		}
		
		public void addFastVal(long val) {
			fasts[fastPos] = val;
			fastPos++;
			if (fasts.length == fastPos) {
				fastPos = 0;
			}
		}
		
		public void addisFastG(boolean val) {
			isFastGreater[isFastGPos] = val;
			isFastGPos++;
			if (isFastGreater.length == isFastGPos) {
				isFastGPos = 0;
			}
		}
		
		public void addIsSlowG(boolean val) {
			isSlowGreater[isSlowGPos] = val;
			isSlowGPos++;
			if (isSlowGreater.length == isSlowGPos) {
				isSlowGPos = 0;
			}
		}

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
		
		public boolean isFastAvgGreaterThanSlow() {
			return getFastAvg() > getSlowAvg();
		}
		
		public boolean isSlowAvgGreaterThanFast() {
			return getSlowAvg() > getFastAvg();
		}
	}

}
