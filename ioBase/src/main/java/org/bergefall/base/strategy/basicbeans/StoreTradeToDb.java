package org.bergefall.base.strategy.basicbeans;

import java.time.LocalDateTime;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.dbstorage.trade.TradeWriter;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

public class StoreTradeToDb extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3502502275690184501L;
	private TradeWriter tradeWriter = new TradeWriter();
	protected boolean storeToDB = true;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		if (false == storeToDB) {
			return status;
		}
		if (null != msg) {
			switch (msg.getMsgType()) {
			case Trade:
				storeNewTrade(msg.getTrade());
				break;
			default:
				break;
			}
		}
		if (null != intraMsg && false == intraMsg.getTrades().isEmpty()) {
			for (TradeCtx tradeCtx : intraMsg.getTrades()) {
				storeNewTradeCtx(tradeCtx);
			}
		}
		return status;
	}

	protected void storeNewTrade(Trade trade) {
		TradeCtx ctx = convertToCtx(trade);
		storeNewTradeCtx(ctx);
	}
	
	protected void storeNewTradeCtx(TradeCtx trade) {
		tradeWriter.storeTradeCtx(trade);	
	}

	protected TradeCtx convertToCtx(Trade trade) {
		TradeCtx ctx = new TradeCtx(trade.getInstrument().getName(), 
				LocalDateTime.parse(trade.getDate()), 
				trade.getAccount().getId(), 
				trade.getIsEntry(), 
				trade.getPrice(), 
				trade.getQty(), 
				trade.getNetProfit(), 
				trade.getGrossProfit(),
				trade.getCommission());
		return ctx;
	}
}
