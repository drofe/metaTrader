package org.bergefall.iobase.blp;

import java.util.LinkedList;
import java.util.List;

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

	private static final String PreMDStrategy = "preMDStrategy";
	private static final String PreAccountStrategy = "preAccStrategy";
	private static final String PreInstrumentStrategy = "preInstrStrategy";
	private static final String PreBeatStrategy = "preBeatStrategy";
	private static final String PreOrderStrategy = "preOrderStrategy";
	private static final String preTradeStrategy = "preTradeStrategy";
	
	protected boolean runPreMdStrat = true;
	protected boolean runPreAccStrat = true;
	protected boolean runPreInstrStrat = true;
	protected boolean runPreBeatStrat = true;
	protected boolean runPreOrderStrat = true;
	protected boolean runPreTradeStrat = true;
	protected BeatsGenerator beatGenerator;
	
	
	public BusinessLogicPipelineImpl (MetaTraderConfig config,
			BusinessLogicPipeline routingPipeline) {
		super(config);
		this.routingPipeline = routingPipeline;
		parseConfig();
	}
	
	@Override
	public void shutdown() {		
		if (null != beatGenerator) {
			beatGenerator.stopBeatGenerator();
		}
		super.shutdown();
	}
		
	protected void parseConfig() {
		runPreMdStrat = config.getBlpBoolean("runPreMDStrategy");
		runPreAccStrat = config.getBlpBoolean("runPreAccStrategy");
		runPreInstrStrat = config.getBlpBoolean("runPreInstrStrategy");
		runPreBeatStrat = config.getBlpBoolean("runPreBeatStrategy");
		runPreTradeStrat = config.getBlpBoolean("runPreTradeStrategy");
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
	
	private void handleInstrumentInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreInstrStrat) {
			runStrategy(PreInstrumentStrategy, token, intraMsg);
		}
		handleInstrument(token, intraMsg);
	}
	
	private void handleTradeInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreTradeStrat) {
			runStrategy(preTradeStrategy, token, intraMsg);
		}
		handleTrades(token, intraMsg);
	}
	
	private void handleOrderInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreOrderStrat) {
			runStrategy(PreOrderStrategy, token, intraMsg);
		}
		handleOrders(token, intraMsg);
	}
	
	private void handleBeatsInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreBeatStrat) {
			runStrategy(PreBeatStrategy, token, intraMsg);
		}
		handleBeats(token, intraMsg);
	}

	private void handleAccountsInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreAccStrat) {
			runStrategy(PreAccountStrategy, token, intraMsg);
		}
		handleAccounts(token, intraMsg);
	}

	private void handleMarketDataInternal(MetaTraderMessage msg) {
		StrategyToken token = getNewToken(msg);
		IntraStrategyBeanMsg intraMsg = getNewIntraBeanMsg();
		if (runPreMdStrat) {
			runStrategy(PreMDStrategy, token, intraMsg);
		}
		handleMarketData(token, intraMsg);
	}
	
	
	// This is temp stuff until it is controlled via config.
	protected void buildStrategies() {
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preMd = new LinkedList<>();
		AddDataToCommonData addData = new AddDataToCommonData();
		addData.parseConfig(config);
		preMd.add(addData);
		strategyMap.put(PreMDStrategy, preMd);
		
		
		List<AbstractStrategyBean<IntraStrategyBeanMsg, ? extends Status>> preTrade = new LinkedList<>();
		StoreTradeToDb storeTrade = new StoreTradeToDb();
		storeTrade.parseConfig(config);
		preTrade.add(storeTrade);
		strategyMap.put(preTradeStrategy, preTrade);
	}
		
	protected abstract void handleAccounts(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleInstrument(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleMarketData(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleBeats(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleOrders(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
	protected abstract void handleTrades(StrategyToken token, IntraStrategyBeanMsg intraMsg);
	
}
