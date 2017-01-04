package org.bergefall.base.strategy.basicbeans;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class AddDataToCommonData extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7670724435222821659L;
	private CommonStrategyData csd;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraBeanMsg) {
		Status status = new Status();
		csd = token.getCsd();
		if (null != intraBeanMsg && null != token.getTriggeringMsg()) {
			MetaTraderMessage msg = token.getTriggeringMsg();
			switch (msg.getMsgType()) {
			case MarketData :
				handleMarketData(msg.getMarketData());
				break;
			case Account :
				handleAccount(msg.getAccount());
				break;
				default :
					break;
			}
		}
		return status;
	}

	protected void handleMarketData(MarketData marketData) {
		csd.addMarketData(marketData);
	}
	
	protected void handleAccount(Account acc) {
		csd.addOrUpdateAccount(acc);
	}

}
