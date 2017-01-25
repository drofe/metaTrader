package org.bergefall.protocol.metatrader;

import static org.bergefall.common.MetaTraderConstants.DIVISOR;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Beat;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;

public class MetaTraderMessageCreator {
	
	public static MetaTraderMessage createTestMsg() {
		AccountCtx ctx = new AccountCtx("TEST", 0, "TEST_BROKER", "TEST_USER");
		return createMTMsg(ctx);
	}
	
	public static MetaTraderMessage createBeat(long time) {
		Beat beat = Beat.newBuilder().setTime(time).build();
		MetaTraderMessage message = MetaTraderMessage.newBuilder().setBeat(beat).build();
		return message;
	}
	
	public static MarketData createMD(String pDate, Double pClose) {
		 MarketData md = MarketData.newBuilder()
				.setDate(pDate)
				.setClose(Long.valueOf((long) (pClose.doubleValue() * DIVISOR)))
				.build();

		return md;
	}
	
	public static MarketData createMD(MarketDataCtx mdCtx) {
		MarketData md = MarketData.newBuilder()
				.setInstrument(mdCtx.getSymbol())
				.setDate(mdCtx.getDate().toString())
				.setAsk(mdCtx.getAskPrice())
				.setBid(mdCtx.getBidPrice())
				.setClose(mdCtx.getClosePrice())
				.setOpen(mdCtx.getOpenPrice())
				.setAvg(mdCtx.getAvgPrice())
				.setTurnover(mdCtx.getTurnover())
				.setTotVol(mdCtx.getTotVol())
				.setTrades(mdCtx.getNrTrades())
				.setHigh(mdCtx.getHighPrice())
				.setLow(mdCtx.getLowPrice())
				.build();
		return md;
	}
	
	public static MetaTraderMessage createMTMsg(MarketDataCtx priceCtx) {
		MetaTraderMessage mtMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.MarketData)
				.setMarketData(createMD(priceCtx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return mtMsg;
	}
	
	public static Account createAccount(AccountCtx accounCtx) {
		Account account = Account.newBuilder()
				.setBroker(accounCtx.getBroker())
				.setName(accounCtx.getName())
				.setId((int) accounCtx.getId())
				.build();
		return account;
	}
	
	public static MetaTraderMessage createMTMsg(AccountCtx accountCtx) {
		MetaTraderMessage mtMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.Account)
				.setAccount(createAccount(accountCtx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return mtMsg;
	}
}
