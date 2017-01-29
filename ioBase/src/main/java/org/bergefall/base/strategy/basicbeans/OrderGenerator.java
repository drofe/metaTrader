package org.bergefall.base.strategy.basicbeans;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class OrderGenerator extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4045345888404994835L;

	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		Status status = new Status();
		if (false == intraMsg.getOrders().isEmpty()) {
			for (OrderCtx orderCtx : intraMsg.getOrders()) {
				MetaTraderMessage order = convertToOrder(orderCtx);
				routeOrder(token, status, order);
				if (Status.OK != status.getCode()) {
					return status;
				}
			}
		}

		return status;
	}

	protected Status routeOrder(StrategyToken token, Status status,
			MetaTraderMessage order) {
		BusinessLogicPipeline blp = token.getRoutingBlp();
		try {
			blp.enqueue(order);
		} catch (InterruptedException e) {
			status = new Status(Status.ERROR, "Failed to route order!");
		}
		return status;
	}

	protected MetaTraderMessage convertToOrder(OrderCtx ctx) {
		return MetaTraderMessageCreator.createMTMsg(ctx);
	}
}