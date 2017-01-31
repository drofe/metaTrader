package org.bergefall.se.server;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.iobase.server.MetaTraderServerApplication;

public class StrategyEngine extends MetaTraderServerApplication {

	private static final String configFile = "./src/main/resources/StrategyEngine.properties";
	
	@Override
	protected List<BusinessLogicPipeline> getBLPs(MetaTraderConfig config) {
		ArrayList<BusinessLogicPipeline> blpList = new ArrayList<>();
		blpList.add(new StrategyEnginePipeline(config));
		return blpList;
	}

	public static void main(String[] args) throws InterruptedException {		
		StrategyEngine ds = new StrategyEngine();
		ds.initServer(configFile);
		Thread server = new Thread(ds);
		server.setName("Strategy Engine");
		server.start();
		server.join();
	}

}
