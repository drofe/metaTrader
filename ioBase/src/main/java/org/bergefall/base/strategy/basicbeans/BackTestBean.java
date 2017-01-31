package org.bergefall.base.strategy.basicbeans;

import java.time.LocalDateTime;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Order;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

@SuppressWarnings("serial")
public class BackTestBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {

		if (Type.Order == msg.getMsgType()) {
			handleOrder(msg.getOrder());
		} else if (Type.Trade == msg.getMsgType()) {
			handleTrade(msg.getTrade());
		}
		return status;
	}
	
	protected void handleOrder(Order order) {
		//For back testing each order automatically gets traded.
		TradeCtx ctx = new TradeCtx(order.getInstrument().getName(), 
				LocalDateTime.now(), 
				order.getAccount().getId(), 
				getIsEntry(order), 
				order.getPrice(), 
				order.getQty(), 
				0L, 
				0L, 
				0L);
		
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
		return true;
	}
}
