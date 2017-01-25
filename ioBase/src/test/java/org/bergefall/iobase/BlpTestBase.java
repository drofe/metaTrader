package org.bergefall.iobase;

import java.time.LocalDateTime;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.TradeCtx;

public class BlpTestBase {

	protected MarketDataCtx createMdCtx(LocalDateTime ltd) {
		return new MarketDataCtx("TEST", ltd, 1, 2, 2, 4, 5, 6, 7, 8, 9, 10);
	}
	
	protected AccountCtx createAccountCtx(int id) {
		return new AccountCtx("TEST", (long) id, "Broker", "User");
	}
	
	protected TradeCtx createTradeCtx(LocalDateTime ltd) {
		return new TradeCtx("TEST", ltd, 1, true, 123456L, 123567L, 123123L, 123567L, Long.valueOf(567-123));
	}
}
