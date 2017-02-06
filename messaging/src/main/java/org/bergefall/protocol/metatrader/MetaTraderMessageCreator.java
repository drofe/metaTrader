package org.bergefall.protocol.metatrader;

import static org.bergefall.common.MetaTraderConstants.DIVISOR;

import java.time.LocalDateTime;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.InstrumentCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.common.data.TradeCtx;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account.Builder;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account.Position;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Beat;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Instrument;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Order;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

public class MetaTraderMessageCreator {

	public static MetaTraderMessage createTestMsg() {
		AccountCtx ctx = new AccountCtx("TEST", 0, "TEST_BROKER", "TEST_USER");
		return createMTMsg(ctx);
	}
	
	public static MetaTraderMessage createMdTestMsg() {
		MarketDataCtx ctx = new MarketDataCtx("TEST", 
				LocalDateTime.now(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
		return createMTMsg(ctx);
	}

	public static MetaTraderMessage createBeat(long time) {
		Beat beat = Beat.newBuilder().setTime(time).build();
		MetaTraderMessage message = MetaTraderMessage.newBuilder()
				.setBeat(beat)
				.setMsgType(Type.Beat)
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return message;
	}

	public static MarketData createMD(String pDate, Double pClose) {
		 MarketData md = MarketData.newBuilder()
				.setDate(pDate)
				.setClose(Long.valueOf((long) (pClose.doubleValue() * DIVISOR)))
				.build();

		return md;
	}

	public static Order createOrder(OrderCtx ctx, AccountCtx accCtx) {
		Order order = Order.newBuilder()
				.setPrice(ctx.getPrice())
				.setQty(ctx.getQty())
				.setAccount(createAccount(accCtx))
				.setIsAsk(ctx.isAsk())
				.setInstrument(Instrument.newBuilder().setName(ctx.getSymbol()).build())
				.build();
		return order;
	}
	
	public static Instrument createInstrument(InstrumentCtx ctx) {
		Instrument instr = Instrument.newBuilder()
				.setId(ctx.getId())
				.setName(ctx.getSymbol())
				.build();
		return instr;
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

	public static Trade createTrade(TradeCtx tradeCtx) {
		Trade trade = Trade.newBuilder()
				.setAccount(Account.newBuilder().setId(tradeCtx.getAccountId()).build())
				.setInstrument(Instrument.newBuilder().setName(tradeCtx.getSymbol()).build())
				.setDate(tradeCtx.getDate().toString())
				.setPrice(tradeCtx.getPrice())
				.setQty(tradeCtx.getQty())
				.setIsEntry(tradeCtx.getIsEntry())
				.setNetProfit(tradeCtx.getNetProfit())
				.setGrossProfit(tradeCtx.getGrossProfit())
				.setCommission(tradeCtx.getCommission())
				.build();
		return trade;
	}

	public static MetaTraderMessage createMTMsg(MarketDataCtx mdCtx) {
		MetaTraderMessage mtMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.MarketData)
				.setMarketData(createMD(mdCtx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return mtMsg;
	}

	public static MetaTraderMessage createMTMsg(OrderCtx ctx, AccountCtx accCtx) {
		MetaTraderMessage mtMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.Order)
				.setOrder(createOrder(ctx, accCtx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return mtMsg;
	}

	public static MetaTraderMessage createMTMsg(TradeCtx tradeCtx) {
		MetaTraderMessage tradeMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.Trade)
				.setTrade(createTrade(tradeCtx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return tradeMsg;
	}

	public static Account createAccount(AccountCtx accounCtx) {
		Builder accBuilder = Account.newBuilder()
				.setBroker(accounCtx.getBroker())
				.setName(accounCtx.getName())
				.setId((int) accounCtx.getId());
		for (PositionCtx pos : accounCtx.getAllPositions()) {
			accBuilder.addPositions(createPosition(pos));
		}
		Account account = accBuilder 
				.build();
		return account;
	}
	
	public static Position createPosition(PositionCtx ctx) {
		Position pos = Position.newBuilder()
				.setInstrument(Instrument.newBuilder().setName(ctx.getSymbol()).build())
				.setLongQty(ctx.getLongQty())
				.setShortQty(ctx.getShortQty())
				.setAvgLongPrice(ctx.getAvgLongPrice())
				.setAvgShortPrice(ctx.getAvgShortPrice())
				.build();
		return pos;
	}

	public static MetaTraderMessage createMTMsg(AccountCtx accountCtx) {
		MetaTraderMessage mtMsg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.Account)
				.setAccount(createAccount(accountCtx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return mtMsg;
	}

	public static MetaTraderMessage createMTMsg(InstrumentCtx ctx) {
		MetaTraderMessage msg = MetaTraderMessage.newBuilder()
				.setMsgType(Type.Instrument)
				.setInstrument(createInstrument(ctx))
				.addTimeStamps(System.currentTimeMillis())
				.build();
		return msg;
	}
}
