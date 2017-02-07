package org.bergefall.se.test.beans;

import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.se.server.strategy.beans.TrailingStoppLossBean;
import org.bergefall.se.test.StrategyEngineTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TrailingStopLossTest extends StrategyEngineTestBase {

	TrailingStoppLossBean stopLossBean;
	
	@Before
	public void setup() {
		initCsd();
	}
	
	@Test
	public void testSimpleStopOrder() {
		stopLossBean = new TrailingStoppLossBean();
		stopLossBean.initBean(config);
		long buyPrice = price(100L);
		//Add position
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(100);
		pos.setAvgLongPrice(buyPrice);

		MetaTraderMessage md = getNewMarketData(0, price(99L));
		StrategyToken token = getNewToken();
		token.setTriggeringMsg(md);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(1, intraMsg.getOrders().size());
	}
	
	@Test
	public void testSimpleOrderNoStop() {
		stopLossBean = new TrailingStoppLossBean();
		stopLossBean.initBean(config);
		long buyPrice = price(100L);
		//Add position
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(100);
		pos.setAvgLongPrice(buyPrice);

		MetaTraderMessage md = getNewMarketData(0, price(101L));
		StrategyToken token = getNewToken();
		token.setTriggeringMsg(md);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(0, intraMsg.getOrders().size());
	}
	
	@Test
	public void testSimpleOrderStopWithConfig() {		
		stopLossBean = new TrailingStoppLossBean();
		getTestConfig().setConfig(stopLossBean.getClass().getName() + ".stopLoss", "20_000");  //2%
		stopLossBean.initBean(config);
		long buyPrice = price(100L);
		//Add position
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(100);
		pos.setAvgLongPrice(buyPrice);

		//Down 1%, no trigger.
		MetaTraderMessage md = getNewMarketData(0, price(99L));
		StrategyToken token = getNewToken();
		token.setTriggeringMsg(md);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(0, intraMsg.getOrders().size());
		
		//Down 2% trigger
		md = getNewMarketData(0, price(98L));
		token = getNewToken();
		token.setTriggeringMsg(md);
		intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(1, intraMsg.getOrders().size());
	}
	
	@Test
	public void testSimpleTrailingStopWithConfig() {		
		stopLossBean = new TrailingStoppLossBean();
		getTestConfig().setConfig(stopLossBean.getClass().getName() + ".stopLoss", "20_000");  //2%
		stopLossBean.initBean(config);
		long buyPrice = price(100L);
		//Add position
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(100);
		pos.setAvgLongPrice(buyPrice);

		//Down 1%, no trigger.
		MetaTraderMessage md = getNewMarketData(0, price(99L));
		StrategyToken token = getNewToken();
		token.setTriggeringMsg(md);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(0, intraMsg.getOrders().size());
		
		//Up to 103
		md = getNewMarketData(0, price(103L));
		token = getNewToken();
		token.setTriggeringMsg(md);
		intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(0, intraMsg.getOrders().size());
		
		//Up to 110
		md = getNewMarketData(0, price(110L));
		token = getNewToken();
		token.setTriggeringMsg(md);
		intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(0, intraMsg.getOrders().size());
		
		//Down to 108 which is not really 2%
		md = getNewMarketData(0, price(108L));
		token = getNewToken();
		token.setTriggeringMsg(md);
		intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(0, intraMsg.getOrders().size());
		
		//Down to 107 which is more than 2%, trigger!
		md = getNewMarketData(0, price(107L));
		token = getNewToken();
		token.setTriggeringMsg(md);
		intraMsg = new IntraStrategyBeanMsg();
		stopLossBean.executeBean(token, intraMsg);
		Assert.assertEquals(1, intraMsg.getOrders().size());
	}

}
