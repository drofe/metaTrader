package org.bergefall.se.server.strategy.beans;

import java.util.HashMap;
import java.util.Map;

import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.PositionCtx;

/**
 * Bean that implements trailing stop loss.
 * 
 * @author ola
 *
 */
public class TrailingStoppLossBean extends StopLossBean {

	protected Map<Integer, LimitTracker> currentLimits;
	private static final long serialVersionUID = 3756765059747923318L;

	@Override
	protected void checkLossLimit(AccountCtx acc, MarketDataCtx md) {
		PositionCtx pos = acc.getPosition(md.getSymbol());
		LimitTracker currLimt = getCurrentLimit(acc.getId());
		currLimt.addCurrentPrice(md.getSymbol(), md.getClosePrice());
		long limitBasePrice = Math.max(currLimt.getCurrentHighPrice(md.getSymbol()), pos.getAvgLongPrice());
		if (isStopLossHit(limitBasePrice, md)) {
			intraMsg.addOrder(createExitOrder(acc.getId(), acc.getPosition(md.getSymbol())));
		}
	}

	protected LimitTracker getCurrentLimit(int accountId) {
		LimitTracker limit = currentLimits.get(Integer.valueOf(accountId));
		if (null == limit) {
			limit = new LimitTracker();
			currentLimits.put(Integer.valueOf(accountId), limit);
		}
		return limit;
	}
	
	@Override
	public void initBean(MetaTraderConfig config) {
		super.initBean(config);
		currentLimits = new HashMap<>();
	}
	
	
	private class LimitTracker {
		Map<String, Long> currentHighPrices;
		
		public LimitTracker() {
			currentHighPrices = new HashMap<>();
		}
		
		public void addCurrentPrice(String symbol, Long price) {
			Long oldPrice = currentHighPrices.get(symbol);
			if (null == oldPrice) {
				currentHighPrices.put(symbol, price);
				return;
			}
			if (oldPrice.longValue() < price.longValue()) {
				currentHighPrices.put(symbol, price);
			}
		}
		
		public long getCurrentHighPrice(String symbol) {
			Long oldPrice = currentHighPrices.get(symbol);
			if (null == oldPrice) {
				oldPrice = Long.valueOf(0L);
				currentHighPrices.put(symbol, oldPrice);
			}
			return oldPrice.longValue();
		}
	}
}
