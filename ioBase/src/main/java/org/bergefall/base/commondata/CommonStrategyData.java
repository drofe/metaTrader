package org.bergefall.base.commondata;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.InstrumentCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account.Position;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Instrument;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Trade;

public class CommonStrategyData {
	
	private final Map<String, SortedSet<MarketDataCtx>> marketDataPerSymbol;
	private final Map<Integer, AccountCtx> accounts;
	private final Map<String, Integer> accountNameToId;
	private final Map<String, InstrumentCtx> instruments;
	private final Map<Integer, List<String>> paLLines;
	private final Map<Integer, List<Trade>> trades;
	
	public CommonStrategyData() {
		marketDataPerSymbol = new HashMap<>();
		accounts = new HashMap<>();
		instruments = new HashMap<>();
		accountNameToId = new HashMap<>();
		paLLines = new HashMap<>();
		trades = new HashMap<>();
		addMarketData(new MarketDataCtx(MetaTraderConstants.CASH, LocalDateTime.now(), 
				1L * MetaTraderConstants.DIVISOR, 1L * MetaTraderConstants.DIVISOR, 
				0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L));
	}
	
	public SortedSet<MarketDataCtx> getMarketDataForSymbol(String symbol) {
		SortedSet<MarketDataCtx> marketData = marketDataPerSymbol.get(symbol);
		return null == marketData ? new TreeSet<MarketDataCtx>() : marketData;
	}
	
	public MarketDataCtx getLatestMarketDataForSymbol(String symbol) {
		SortedSet<MarketDataCtx> marketData = marketDataPerSymbol.get(symbol);
		if (null == marketData) {
			return null;
		}
		return marketData.last();
	}

	public void addMarketData(MarketDataCtx md) {
		if (null == md) {
			return;
		}
		String symbol = md.getSymbol();
		synchronized (marketDataPerSymbol) {			
			SortedSet<MarketDataCtx> marketData = marketDataPerSymbol.get(symbol);
			if (null == marketData) {
				marketData = new TreeSet<>();
				marketDataPerSymbol.put(symbol, marketData);
			}
			marketData.add(md);
		}
	}
	
	public void addOrUpdateAccount(Account acc) {
		if (null == acc) {
			return;
		}
		
		AccountCtx accCtx = new AccountCtx(acc.getName(), acc.getId(), acc.getBroker(), acc.getUser());
		for (Position pos : acc.getPositionsList()) {
			PositionCtx posCtx = accCtx.getPosition(pos.getInstrument().getName());
			posCtx.setAvgLongPrice(pos.getAvgLongPrice());
			posCtx.setAvgShortPrice(pos.getAvgShortPrice());
			posCtx.setLongQty(pos.getLongQty());
			posCtx.setShortQty(pos.getShortQty());
			
		}
		addOrUpdateAccount(accCtx);
	}
	public Collection<AccountCtx> getAllAccounts() {
		return accounts.values();
	}
	public void addOrUpdateInstrument(Instrument instrument) {
		InstrumentCtx ctx = new InstrumentCtx(instrument.getName(), instrument.getId());
		addOrUpdateInstrument(ctx);
	}
	
	public void addOrUpdateInstrument(InstrumentCtx ctx) {
		synchronized (instruments) {
			instruments.put(ctx.getSymbol(), ctx);
		}		
	}
	
	public synchronized void addOrUpdateAccount(AccountCtx acc)  {
		accounts.put(Integer.valueOf(acc.getId()), acc);
		accountNameToId.put(acc.getName(), acc.getId());
	}
	
	public AccountCtx getAccount(int id) {
		return accounts.get(Integer.valueOf(id));
	}
	
	public Integer getAccountId(String name) {
		return accountNameToId.get(name);
	}
	
	public void addMarketData(MarketData md) {
		if (null == md) {
			return;
		}
		MarketDataCtx mdCtx = MetaTraderMessageCreator.convertToContext(md);
		addMarketData(mdCtx);
	}
	
	public void addMarketData(Collection<? extends MarketDataCtx> mds) {
		if (null == mds || mds.isEmpty()) {
			return;
		}
		for (MarketDataCtx mdItem : mds ) {
			addMarketData(mdItem);
		}
	}
	
	public void addNewPaL(Integer accId, String pal) {
		if (null == accId || null == pal) {
			return;
		}
		List<String> palList = paLLines.get(accId);
		if (null == palList) {
			palList = new LinkedList<>();
			paLLines.put(accId, palList);
		}
		palList.add(pal);
	}
	
	public List<String> getPaL(Integer accId) {
		List<String> palList = paLLines.get(accId);
		if (null == palList) {
			return new LinkedList<>();
		}
		return palList;
	}
	
	public void addNewTrade(Integer accId, Trade trade) {
		if (null == accId || null == trade) {
			return;
		}
		List<Trade> tradeList = trades.get(accId);
		if (null == tradeList) {
			tradeList = new LinkedList<>();
			trades.put(accId, tradeList);
		}
		tradeList.add(trade);
	}
	
	public List<Trade> getTradeList(Integer accId) {
		List<Trade> tradeList = trades.get(accId);
		if (null == tradeList) {
			return new LinkedList<>();
		}
		return tradeList;
	}
}
