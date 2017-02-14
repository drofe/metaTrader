package org.bergefall.se.server.webhandlers;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.iobase.web.MetaTraderWebRequest;
import org.bergefall.iobase.web.MetaTraderWebResponse;
import org.bergefall.iobase.web.WebReqHandler;

public class PublishProfitAndLoss implements WebReqHandler {

	private final CommonStrategyData csd;
	
	public PublishProfitAndLoss(CommonStrategyData csd) {
		this.csd = csd;
	}

	@Override
	public Object handle(MetaTraderWebRequest request, MetaTraderWebResponse response) throws Exception {
		StringBuilder str = new StringBuilder();
		for (AccountCtx acc : csd.getAllAccounts()) {
			str.append("PaL for acc: " + acc.getName());
			for (String pal : csd.getPaL(acc.getId())) {
				str.append(pal);
			}
		}
		return str.toString();
	}

}
