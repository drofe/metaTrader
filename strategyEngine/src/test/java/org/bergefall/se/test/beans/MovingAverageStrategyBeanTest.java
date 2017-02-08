package org.bergefall.se.test.beans;

import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.se.server.strategy.beans.MovingAverageCalculatingBean;
import org.bergefall.se.test.StrategyEngineTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MovingAverageStrategyBeanTest extends StrategyEngineTestBase {

	MovingAverageCalculatingBean masBean;
	
	@Before
	public void setup() {
		initCsd();
		masBean  = new MovingAverageCalculatingBean();
		masBean.initBean(config);
	}

	@Test
	public void testConstantPriceNoOrder() {
		for (int i = 0; i < 100; i++) {
			StrategyToken token = getNewToken();
			IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
			token.setTriggeringMsg(getNewMarketData(i));
			masBean.executeBean(token, msg);
			Assert.assertEquals(0, msg.getOrders().size());
		}
		
	}
	
	@Test
	public void testGenerateBuyOrder() {
		for (int i = 0; i < 30; i++) {
			StrategyToken token = getNewToken();
			IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
			token.setTriggeringMsg(getNewMarketData(i));
			masBean.executeBean(token, msg);
			Assert.assertEquals(0, msg.getOrders().size());
		}

		//Insert one higher price -> Fast shall raise above slow and raise buy order
		StrategyToken token = getNewToken();
		IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
		token.setTriggeringMsg(getNewMarketData(0, price(30)));
		masBean.executeBean(token, msg);
		Assert.assertEquals(1, msg.getOrders().size());
		Assert.assertEquals(price(30), msg.getOrders().get(0).getPrice());
		Assert.assertEquals(false, msg.getOrders().get(0).isAsk());
		Assert.assertEquals(qty(1000/30), msg.getOrders().get(0).getQty());
		Assert.assertEquals(ERIC, msg.getOrders().get(0).getSymbol());
	}
	
	@Test
	public void testGenerateBuyOrderWhenCashIsAvailable() {
		long initialCash = qty(1000L);
		long newPrice = price(30);
		csd.getAccount(testAccId).getPosition(CASH).addLongQty(initialCash, MetaTraderConstants.CashPrice);
		for (int i = 0; i < 30; i++) {
			StrategyToken token = getNewToken();
			IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
			token.setTriggeringMsg(getNewMarketData(i));
			masBean.executeBean(token, msg);
			Assert.assertEquals(0, msg.getOrders().size());
		}

		//Insert one higher price -> Short fast shall raise above slow and raise buy order
		StrategyToken token = getNewToken();
		IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
		token.setTriggeringMsg(getNewMarketData(0, newPrice));
		masBean.executeBean(token, msg);
		Assert.assertEquals(1, msg.getOrders().size());
		Assert.assertEquals(newPrice, msg.getOrders().get(0).getPrice());
		Assert.assertEquals(false, msg.getOrders().get(0).isAsk());
		Assert.assertEquals(qty(initialCash / newPrice), msg.getOrders().get(0).getQty());
		Assert.assertEquals(ERIC, msg.getOrders().get(0).getSymbol());
	}
}
