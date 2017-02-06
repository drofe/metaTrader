package org.bergefall.se.test;

import java.time.LocalDateTime;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class StrategyEngineTestBase {

	protected static final String testAcc = "TestAcc";
	protected static final int testAccId = 0;
	protected static final String ERIC = "ERIC";
	protected static final String CASH = MetaTraderConstants.CASH;
	protected CommonStrategyData csd;
	protected MetaTraderConfig config = null;
	
	
	public StrategyEngineTestBase() {

	}

	protected void initCsd() {
		csd = new CommonStrategyData();
		AccountCtx accCtx = new AccountCtx(testAcc, testAccId, "testBroker", "testUser");
		csd.addOrUpdateAccount(accCtx);
	}
	
	protected StrategyToken getNewToken() {
		return new StrategyToken(LocalDateTime.now(), csd, null);
	}
	
	protected StrategyToken getNewWithMdMsgToken() {
		StrategyToken token = new StrategyToken(LocalDateTime.now(), csd, null);
		token.setTriggeringMsg(MetaTraderMessageCreator.createTestMsg());
		return token;
	}
	
	
	protected TradeCtx getNewBuyTrade(final String symb, long price, long qty) {
		return new TradeCtx(symb, LocalDateTime.now(), testAccId, true, price, qty, 0L, 0L, 0L);
	}
	
	protected TradeCtx getNewSellTrade(final String symb, long price, long qty) {
		return new TradeCtx(symb, LocalDateTime.now(), testAccId, false, price, qty, 0L, 0L, 0L);
	}
	
	protected MetaTraderMessage getNewMarketData(int i) {
		return getNewMarketData(i, 10L);
	}
	protected MetaTraderMessage getNewMarketData(int i, long closePrice) {
		MarketDataCtx ctx = new MarketDataCtx(ERIC, LocalDateTime.now(), 0L, closePrice, 0, 0, 0, 0, 0, 0, 0, 0);
		return MetaTraderMessageCreator.createMTMsg(ctx);
	}
	
}
