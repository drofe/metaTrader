package org.bergefall.iobase;

import java.time.LocalDateTime;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;

public class BlpTestBase {

	protected MarketDataCtx createMdCtx(LocalDateTime ltd) {
		return new MarketDataCtx("TEST", ltd, 1, 2, 2, 4, 5, 6, 7, 8, 9, 10);
	}
	
	protected AccountCtx createAccountCtx(int id) {
		return new AccountCtx("TEST", (long) id, "Broker", "User");
	}
}
