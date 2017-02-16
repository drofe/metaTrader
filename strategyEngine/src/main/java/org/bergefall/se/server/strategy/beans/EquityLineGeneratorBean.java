package org.bergefall.se.server.strategy.beans;

import java.io.IOException;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.log.RotatingFileHandler;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;
import org.bergefall.se.server.common.PortfolioValueDataMangler;

/**
 * Prints account summaries to file.
 * Triggered by new market data.
 * Relies on that preMdStrategy is run with AddDataToCommonData bean.
 * @author ola
 *
 */
public class EquityLineGeneratorBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	private static final long serialVersionUID = -4734313004680657145L;
	private RotatingFileHandler fileHander;
	private boolean writeToFile;

	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		if (null != msg && Type.MarketData == msg.getMsgType()) {
			handleMarketData(msg.getMarketData());
		}
		return status;
	}
	
	private void handleMarketData(MarketData marketData) {
		if (writeToFile) {
			try {
				fileHander.write(PortfolioValueDataMangler.formatRecord(csd, marketData.getDate()));
			} catch (IOException e) {
				log.error("Error writing to EQLine file. \n" + SystemLoggerIf.getStacktrace(e));
			}
		}
		
	}

	

	@Override
	public void initBean(MetaTraderConfig config) {
		super.initBean(config);
		writeToFile = null == config ? false : getBooleanBeanProperty("writeToFile");
		if (writeToFile) {
			fileHander = new RotatingFileHandler("./", true, 60_000, "PortfolioValue-");
		}
	}
	
	@Override
	public void shutdownHook() {
		if (null != fileHander) {
			fileHander.close();
		}
	}

}
