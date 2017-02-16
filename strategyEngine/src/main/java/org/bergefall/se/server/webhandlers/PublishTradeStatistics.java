package org.bergefall.se.server.webhandlers;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.iobase.web.MetaTraderWebRequest;
import org.bergefall.iobase.web.MetaTraderWebResponse;
import org.bergefall.iobase.web.WebReqHandler;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;
import org.bergefall.se.server.common.TradeStatisticsMangler;

public class PublishTradeStatistics implements WebReqHandler {

	private final CommonStrategyData csd;
	public PublishTradeStatistics(CommonStrategyData csd) {
		this.csd = csd;
	}

	@Override
	public Object handle(MetaTraderWebRequest request, MetaTraderWebResponse response) throws Exception {
		StringBuilder str = new StringBuilder();
		for (AccountCtx accCtx : csd.getAllAccounts()) {
			for (Trade trade : csd.getTradeList(accCtx.getId())) {
				str.append(TradeStatisticsMangler.formatLogEntry(trade));
				str.append(System.lineSeparator());
			}
		}
		
		return str.toString();
	}

}
