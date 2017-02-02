package org.bergefall.se.server;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.iobase.server.MetaTraderServerApplication;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class StrategyEngine extends MetaTraderServerApplication {

	private static final String configFile = "./src/main/resources/StrategyEngine.properties";
	
	@Override
	protected List<BusinessLogicPipeline> getBLPs(MetaTraderConfig config) {
		ArrayList<BusinessLogicPipeline> blpList = new ArrayList<>();
		blpList.add(new StrategyEnginePipeline(config));
		return blpList;
	}

	public void runBootloaders() {
		//For back testing, add a accounts to be used.
		int id = 0;
		for(int i = 20; i < 25 ; i++) {
			for (int j = 5; j < 10; j++) {
				AccountCtx ctx = new AccountCtx(j + "_" + i, id++, "test", "test");
				MetaTraderMessage msg = MetaTraderMessageCreator.createMTMsg(ctx);
				for(BusinessLogicPipeline blp : blps) {
					try {
						blp.enqueue(msg);
					} catch (InterruptedException e) {
						//By design
					}
				}
			}			
		}
	}
	
	public static void main(String[] args) throws InterruptedException {		
		StrategyEngine ds = new StrategyEngine();
		ds.initServer(configFile);
		ds.runBootloaders();
		Thread server = new Thread(ds);
		server.setName("Strategy Engine");
		server.start();
		server.join();
	}

}
