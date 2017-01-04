package org.bergefall.base.strategy;

import java.io.Serializable;

import org.bergefall.common.config.MetaTraderConfig;

@SuppressWarnings("serial")
public abstract class AbstractStrategyBean<IN, OUT> implements Serializable {
	
	public abstract OUT execute(StrategyToken token, IN intraMsg);
	
	public void parseConfig(MetaTraderConfig config) {
		
	}
}
