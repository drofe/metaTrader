package org.bergefall.se.server.strategy.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.calculators.MovingAverage;
import org.bergefall.common.calculators.MovingAveragelongRingBufferImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;

/** 
 * A moving average strategy.
 * @author ola
 *
 */
@SuppressWarnings("serial")
public class MovingAverageCalculatingBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	protected static final SystemLoggerIf log = SystemLoggerImpl.get();;
	
	protected Map<String, List<MovingAverage>> calculators;
	protected int shortAvg = 10;
	protected int longAvg = 100;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		if (Type.MarketData == msg.getMsgType()) {
			handleNewMarketData(msg.getMarketData());
		}
		return status;
	}

	protected void handleNewMarketData(MarketData marketData) {
		List<MovingAverage> calcs = getCalculators(marketData.getInstrument());
		calcs.get(0).addValue(marketData.getClose());
		calcs.get(1).addValue(marketData.getClose());
		
		log.info("Short average: " + calcs.get(0).getAverage());
		log.info("Long average: " + calcs.get(1).getAverage());
		
		if (calcs.get(0).getAverage() > calcs.get(1).getAverage()) {
			OrderCtx ctx = new OrderCtx(marketData.getInstrument(), 100);
			ctx.setPrice(marketData.getClose());
			try {
				routingPipeline.enqueue(MetaTraderMessageCreator.createMTMsg(ctx));
			} catch (InterruptedException e) {
				status.setCode(Status.ERROR);
				status.setMsg("Failed to route order");
			}
		}
		
	}
	
	private List<MovingAverage> getCalculators(String symb) {
		List<MovingAverage> calcList = calculators.get(symb);
		if (null == calcList) {
			calcList = new ArrayList<>();
			calcList.add(new MovingAveragelongRingBufferImpl(shortAvg));
			calcList.add(new MovingAveragelongRingBufferImpl(longAvg));
			calculators.put(symb, calcList);
		}
		return calcList;
	}
	
	@Override
	public void initBean(MetaTraderConfig config) {
		calculators = new HashMap<>();
	}

}
