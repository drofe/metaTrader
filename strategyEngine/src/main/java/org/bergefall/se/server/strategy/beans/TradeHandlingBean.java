package org.bergefall.se.server.strategy.beans;

import java.io.IOException;

import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.base.strategy.basicbeans.StoreTradeToDb;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.common.log.RotatingFileHandler;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;
import org.bergefall.se.server.common.TradeStatisticsMangler;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

public class TradeHandlingBean extends StoreTradeToDb {

	/**
	 * 
	 */
	private static final long serialVersionUID = 736085641456361407L;
	
	private RotatingFileHandler fileHander;
	private boolean writeToFile;

	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		super.execute(token, intraMsg);
		if (null != msg && Type.Trade == msg.getMsgType()) {
			handleTrade(msg.getTrade());
		}
		if (null != intraMsg && false == intraMsg.getTrades().isEmpty()) {
			for (TradeCtx tradeCtx : intraMsg.getTrades()) {
				handleTrade(tradeCtx);
			}
		}
		return status;
	}
	
	protected void handleTrade(TradeCtx tradeCtx) {
		Trade trade = MetaTraderMessageCreator.createTrade(tradeCtx);
		handleTrade(trade);		
	}
	
	protected void handleTrade(Trade trade) {
		AccountCtx accCtx = csd.getAccount(trade.getAccount().getId());		
		updatesPositions(accCtx, trade);
		csd.addNewTrade(accCtx.getId(), trade);
		if (writeToFile) {
			try {
				fileHander.write(TradeStatisticsMangler.formatLogEntry(trade));
			} catch (IOException e) {
				status.setCode(Status.ERROR);
				status.setMsg(SystemLoggerIf.getStacktrace(e));
			}
		}
	}
	
	protected void updatesPositions(AccountCtx accCtx, Trade trade) {		
		if (trade.getIsEntry()) {
			PositionCtx tradeInstrPos = accCtx.getPosition(trade.getInstrument().getName());
			tradeInstrPos.addLongQty(trade.getQty(), trade.getPrice());
			PositionCtx cashPos = accCtx.getPosition(MetaTraderConstants.CASH);
			cashPos.removeLongQty((trade.getPrice() * trade.getQty()) / MetaTraderConstants.DIVISOR,
					MetaTraderConstants.CashPrice);
		} else {
			PositionCtx tradeInstrPos = accCtx.getPosition(trade.getInstrument().getName());
			tradeInstrPos.removeLongQty(trade.getQty(), trade.getPrice());
			PositionCtx cashPos = accCtx.getPosition(MetaTraderConstants.CASH);
			cashPos.addLongQty(trade.getPrice() * trade.getQty() / MetaTraderConstants.DIVISOR, 
					MetaTraderConstants.CashPrice);
		}
	}

	@Override
	public void initBean(MetaTraderConfig config) {
		super.initBean(config);
		writeToFile = null == config ? false : getBooleanBeanProperty("writeToFile");
		if (writeToFile) {
			fileHander = new RotatingFileHandler("./", true, 60_000, "Trades-");
			try {
				fileHander.write(TradeStatisticsMangler.getTitle());
			} catch (IOException e) {
				log.error("Error writing head to Trades file." + System.lineSeparator() + 
						SystemLoggerIf.getStacktrace(e) );
			}
		}
		storeToDB = false;
	}
	
	
	@Override
	public void shutdownHook() {
		if (null != fileHander) {
			fileHander.close();
		}
	}
}
