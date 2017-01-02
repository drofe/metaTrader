package org.bergefall.protocol.metatrader;

import static org.bergefall.common.MetaTraderConstants.DIVISOR;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;

public class MetaTraderMessageCreator {
	
	public static MarketData createMD(String pDate, Double pClose) {
		 MarketData md = MarketData.newBuilder()
				.setDate(pDate)
				.setClose(Long.valueOf((long) (pClose.doubleValue() * DIVISOR)))
				.build();

		return md;
	}
	
	public static MarketData createMD(MarketDataCtx priceCtx) {
		MarketData md = MarketData.newBuilder()
				.setDate(priceCtx.getDate().toString())
				.setAsk(priceCtx.getAskPrice())
				.setBid(priceCtx.getBidPrice())
				.setClose(priceCtx.getClosePrice())
				.setOpen(priceCtx.getOpenPrice())
				.setAvg(priceCtx.getAvgPrice())
				.setTurnover(priceCtx.getTurnover())
				.setTotVol(priceCtx.getTotVol())
				.setTrades(priceCtx.getNrTrades())
				.setHigh(priceCtx.getHighPrice())
				.setLow(priceCtx.getLowPrice())
				.build();
		return md;
	}
	
	public static MetaTraderMessage createMTMsg(MarketDataCtx priceCtx) {
		MetaTraderMessage mtMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.MarketData)
				.setMarketData(createMD(priceCtx))
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
				.build();
		return mtMsg;
	}
}
