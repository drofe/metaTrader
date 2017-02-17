package org.bergefall.se.server;

import static org.bergefall.common.MetaTraderConstants.qty;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.iobase.server.MetaTraderServerApplication;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.se.server.webhandlers.PublishProfitAndLoss;
import org.bergefall.se.server.webhandlers.PublishTradeStatistics;

public class StrategyEngine extends MetaTraderServerApplication {

	private static final String configFile = "StrategyEngine.properties";
	
	@Override
	protected List<BusinessLogicPipeline> getBLPs(MetaTraderConfig config) {
		ArrayList<BusinessLogicPipeline> blpList = new ArrayList<>();
		blpList.add(new StrategyEnginePipeline(config, csd));
		return blpList;
	}

	@Override 
	protected void initWebService(MetaTraderConfig config) {
		super.initWebService(config);
		webService.get("/pal", new PublishProfitAndLoss(getCSD()));
		webService.get("/trades", new PublishTradeStatistics(getCSD()));
	}
	
	public void runBootloaders() {
		//For back testing, add a accounts to be used.
		int id = 0;
		for(int i = 23; i < 24 ; i++) {
			for (int j = 9; j < 10; j++) {
				AccountCtx ctx = new AccountCtx(j + "_" + i, id++, "test", "test");
				PositionCtx posCtx = ctx.getPosition(MetaTraderConstants.CASH);
				posCtx.addLongQty(qty(1000L), MetaTraderConstants.CashPrice);
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
