package org.bergefall.base.strategy.basicbeans;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Instrument;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;

public class AddDataToCommonData extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7670724435222821659L;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraBeanMsg) {

		csd = token.getCsd();
		if (null != intraBeanMsg && null != msg) {

			switch (msg.getMsgType()) {
			case MarketData :
				handleMarketData(msg.getMarketData());
				break;
			case Account :
				handleAccount(msg.getAccount());
				break;
			case Instrument :
				handleInstrument(msg.getInstrument());
			default :
				break;
			}
		}
		return status;
	}

	private void handleInstrument(Instrument instrument) {
		csd.addOrUpdateInstrument(instrument);
	}

	protected void handleMarketData(MarketData marketData) {
		csd.addMarketData(marketData);
	}
	
	protected void handleAccount(Account acc) {
		csd.addOrUpdateAccount(acc);
	}

}
