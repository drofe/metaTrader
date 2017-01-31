package org.bergefall.iobase.blp;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.base.strategy.basicbeans.AddDataToCommonData;
import org.bergefall.base.strategy.basicbeans.StoreTradeToDb;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

/**
 * This class is meant to be created as the (or one of the) main thread for the single threaded
 * business logic i MetaTrader servers. One server can hold several BLPs but they shall
 * not execute the same strategy or involve the same instruments.
 * @author ola
 *
 */
public abstract class BusinessLogicPipelineImpl extends BusinessLogicPipelineBase {

	private static final String preMDStrategy = "preMDStrategy";
	private static final String preAccountStrategy = "preAccStrategy";
	private static final String preInstrumentStrategy = "preInstrStrategy";
	private static final String preBeatStrategy = "preBeatStrategy";
	private static final String preOrderStrategy = "preOrderStrategy";
	private static final String preTradeStrategy = "preTradeStrategy";
	private static final AtomicInteger cBLPNr = new AtomicInteger(1);
	private int thisBlpNr;

	protected boolean runPreMdStrat;
	protected boolean runPreAccStrat;
	protected boolean runPreInstrStrat;
	protected boolean runPreBeatStrat;
	protected boolean runPreOrderStrat;
	protected boolean runPreTradeStrat;
	protected boolean runHandleMarketData;
	protected boolean runHandleAcc;
	protected boolean runHandleInstr;
	protected boolean runHandleBeat;
	protected boolean runHandleOrder;
	protected boolean runHandleTrade;
	protected BeatsGenerator beatGenerator;

	public BusinessLogicPipelineImpl(MetaTraderConfig config) {
		super(config);
		this.thisBlpNr = cBLPNr.getAndIncrement();
		parseConfig();
		blpName = "BLP-";

	}

	@Override
	public void setRoutingBlp(BusinessLogicPipeline router) {
		this.routingPipeline = router;
	}
	
	@Override
	public void shutdown() {
		if (null != beatGenerator) {
			beatGenerator.stopBeatGenerator();
		}
		super.shutdown();
	}

	protected void parseConfig() {
		runPreMdStrat = config.getBlpBoolean(thisBlpNr,"runPreMDStrategy");
		runPreAccStrat = config.getBlpBoolean(thisBlpNr,"runPreAccStrategy");
		runPreInstrStrat = config.getBlpBoolean(thisBlpNr,"runPreInstrStrategy");
		runPreBeatStrat = config.getBlpBoolean(thisBlpNr,"runPreBeatStrategy");
		runPreTradeStrat = config.getBlpBoolean(thisBlpNr,"runPreTradeStrategy");
		runPreOrderStrat = config.getBlpBoolean(thisBlpNr, "runPreOrderStrategy");
		runHandleMarketData = config.getBlpBoolean(thisBlpNr,"runHandleMarketData");
		runHandleAcc = config.getBlpBoolean(thisBlpNr,"runHandleAcc");
		runHandleInstr = config.getBlpBoolean(thisBlpNr,"runHandleInstr");
		runHandleBeat = config.getBlpBoolean(thisBlpNr,"runHandleBeat");
		runHandleTrade = config.getBlpBoolean(thisBlpNr,"runHandleTrade");
		runHandleOrder = config.getBlpBoolean(thisBlpNr, "runHandleOrder");
	}

	protected void fireHandlers(MetaTraderMessage msg) {
		MetaTraderMessage.Type type = msg.getMsgType();

		switch (type) {
		case Account :
			handleAccountsInternal(msg);
			break;
		case MarketData :
			handleMarketDataInternal(msg);
			break;
		case Instrument:
			handleInstrumentInternal(msg);
			break;
		case Beat:
			handleBeatsInternal(msg);
			break;
		case Order:
			handleOrderInternal(msg);
			break;
		case Trade:
			handleTradeInternal(msg);
			break;
		default:
			break;
		}
	}

	@Override
	public Integer getBlpNr() {
		return Integer.valueOf(thisBlpNr);
	}

	private void handleInstrumentInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreInstrStrat) {
			runStrategy(preInstrumentStrategy, token, intraMsg);
		}
		if (runHandleInstr) {
			handleInstrument(token, intraMsg);
		}
	}

	private void handleTradeInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreTradeStrat) {
			runStrategy(preTradeStrategy, token, intraMsg);
		}
		if (runHandleTrade) {
			handleTrades(token, intraMsg);
		}
	}

	private void handleOrderInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreOrderStrat) {
			runStrategy(preOrderStrategy, token, intraMsg);
		}
		if (runHandleOrder) {
			handleOrders(token, intraMsg);
		}
	}

	private void handleBeatsInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreBeatStrat) {
			runStrategy(preBeatStrategy, token, intraMsg);
		}
		if (runHandleBeat) {
			handleBeats(token, intraMsg);
		}
	}

	private void handleAccountsInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreAccStrat) {
			runStrategy(preAccountStrategy, token, intraMsg);
		}
		if (runHandleAcc) {
			handleAccounts(token, intraMsg);
		}
	}

	private void handleMarketDataInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreMdStrat) {
			runStrategy(preMDStrategy, token, intraMsg);
		}
		if (runHandleMarketData) {
			handleMarketData(token, intraMsg);
		}
	}


	// This is temp stuff until it is controlled via config.
	protected void buildStrategies() {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preMd = new LinkedList<>();
		AddDataToCommonData addData = new AddDataToCommonData();
		addData.initBean(config);
		preMd.add(addData);
		strategyMap.put(preMDStrategy, preMd);

		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preTrade = new LinkedList<>();
		StoreTradeToDb storeTrade = new StoreTradeToDb();
		storeTrade.initBean(config);
		preTrade.add(storeTrade);
		strategyMap.put(preTradeStrategy, preTrade);

		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preBeat = new LinkedList<>();
		strategyMap.put(preBeatStrategy, preBeat);
		
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preAcc = new LinkedList<>();
		strategyMap.put(preAccountStrategy, preAcc);
		
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preInstr = new LinkedList<>();
		strategyMap.put(preInstrumentStrategy, preInstr);
		
	}

	protected abstract void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg);

	protected abstract void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg);

	protected abstract void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg);

	protected abstract void handleBeats(StrategyToken token, IntraStrategyBeanMsg intraMsg);

	protected abstract void handleOrders(StrategyToken token, IntraStrategyBeanMsg intraMsg);

	protected abstract void handleTrades(StrategyToken token, IntraStrategyBeanMsg intraMsg);

}
