package org.bergefall.base.strategy.basicbeans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Order;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

@SuppressWarnings("serial")
public class BackTestBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	/**
	 * Commission and slippage
	 */
	private long commission = 0L;
	private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {

		if (Type.Order == msg.getMsgType()) {
			convertToTradeAndRoute(msg.getOrder());
		} else if (Type.Trade == msg.getMsgType()) {
			handleTrade(msg.getTrade());
		} else if (false == intraMsg.getOrders().isEmpty()) {
			handleInChainOrders(intraMsg);			
		}
		return status;
	}
	
	protected void handleInChainOrders(IntraStrategyBeanMsg intraMsg) {
		for (OrderCtx order : intraMsg.getOrders()) {
			TradeCtx tradeCtx = new TradeCtx(order.getSymbol(), 
					Type.MarketData == msg.getMsgType() ? 
							LocalDateTime.parse(msg.getMarketData().getDate(), DTF)
							: LocalDateTime.now(), 
					order.getAccountId(), 
					getIsEntry(order), 
					order.getPrice(), 
					order.getQty(), 
					calclulateNetProfit(order), 
					calclulateGrossProfit(order),
					commission);
			intraMsg.addTrade(tradeCtx);
		}
	}
	
	protected Long calclulateNetProfit(OrderCtx order) {
		if (!order.isAsk()) {
			return Long.valueOf(0L);
		}
		return Long.valueOf(calclulateGrossProfit(order).longValue() - commission);
	}
	
	protected Long calclulateGrossProfit(OrderCtx order) {
		if (!order.isAsk()) {
			return Long.valueOf(0L);
		}
		PositionCtx posCtx = csd.getAccount(order.getAccountId()).getPosition(order.getSymbol());
		long payed = (posCtx.getAvgLongPrice() * order.getQty()) / MetaTraderConstants.DIVISOR;
		return (order.getPrice() * order.getQty())/MetaTraderConstants.DIVISOR - payed;
	}

	protected void convertToTradeAndRoute(Order order) {
		//For back testing each order automatically gets traded.
		TradeCtx ctx = new TradeCtx(order.getInstrument().getName(), 
				LocalDateTime.now(), 
				order.getAccount().getId(), 
				getIsEntry(order), 
				order.getPrice(), 
				order.getQty(), 
				0L, 
				0L, 
				commission);
		try {
			routingPipeline.enqueue(MetaTraderMessageCreator.createMTMsg(ctx));
		} catch (InterruptedException e) {
			status.setCode(Status.ERROR);
			status.setMsg("Failed to route trade.");
		}
	}
	
	protected void handleTrade(Trade trade) {
		log.info("Trade," + trade.getInstrument().getName() + ", "  + trade.getPrice());
	}

	protected boolean getIsEntry(Order order) {
		return !order.getIsAsk();
	}
	
	protected boolean getIsEntry(OrderCtx order) {
		return !order.isAsk();
	}
	
	@Override
	public void initBean(MetaTraderConfig config) {
		commission = null == config ? 0L : config.getLongProperty(this.getClass().getName(), "commission");
	}
}
