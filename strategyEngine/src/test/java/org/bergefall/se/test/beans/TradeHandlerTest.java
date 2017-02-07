package org.bergefall.se.test.beans;

import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.se.server.strategy.beans.TradeHandlingBean;
import org.bergefall.se.test.StrategyEngineTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TradeHandlerTest extends StrategyEngineTestBase {

	TradeHandlingBean tradeHandlerBean;
	
	@Before
	public void setup() {
		initCsd();
		tradeHandlerBean = new TradeHandlingBean();
		tradeHandlerBean.initBean(null);
	}
	
	@Test
	public void testSimpleBuy() {
		long price = 10L;
		long qty = 100L;
		
		IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
		msg.addTrade(getNewBuyTrade(ERIC, price, qty));
		tradeHandlerBean.executeBean(getNewToken(), msg);
		
		Assert.assertEquals(qty, csd.getAccount(testAccId).getPosition(ERIC).getLongQty());
		Assert.assertEquals(0L, csd.getAccount(testAccId).getPosition(ERIC).getShortQty());
		Assert.assertEquals(-1 * price * qty, csd.getAccount(testAccId).getPosition(CASH).getLongQty());
	}
	
	@Test
	public void testSimpleSell() {
		long price = price(11L);
		long qty = qty(100L);
		
		//Add position to sell.
		csd.getAccount(testAccId).getPosition(ERIC).setLongQty(qty);
		
		IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
		msg.addTrade(getNewSellTrade(ERIC, price, qty));
		tradeHandlerBean.executeBean(getNewToken(), msg);
		
		Assert.assertEquals(0L, csd.getAccount(testAccId).getPosition(ERIC).getLongQty());
		Assert.assertEquals(0L, csd.getAccount(testAccId).getPosition(ERIC).getShortQty());
		Assert.assertEquals((price * qty) / MetaTraderConstants.DIVISOR, 
				csd.getAccount(testAccId).getPosition(CASH).getLongQty());
	}
	
	@Test
	public void testBuyThenSellWithProfit() {
		long price = price(10L);
		long qty = qty(100L);
		long profit = price(4);
		
		//Add start cash.
		csd.getAccount(testAccId).getPosition(CASH).setLongQty((price * qty) / MetaTraderConstants.DIVISOR);
		
		IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
		msg.addTrade(getNewBuyTrade(ERIC, price, qty));
		tradeHandlerBean.executeBean(getNewToken(), msg);
		
		msg = new IntraStrategyBeanMsg();
		msg.addTrade(getNewSellTrade(ERIC, price + profit, qty));
		tradeHandlerBean.executeBean(getNewToken(), msg);
		
		Assert.assertEquals((price+profit) * qty / MetaTraderConstants.DIVISOR, 
				csd.getAccount(testAccId).getPosition(CASH).getLongQty());
	}
	
	@Test
	public void testBuyThenSellWithLoss() {
		long price = price(10L);
		long qty = qty(100L);
		long profit = price(-6);
		
		//Add start cash.
		csd.getAccount(testAccId).getPosition(CASH).setLongQty(price * qty / MetaTraderConstants.DIVISOR);
		
		IntraStrategyBeanMsg msg = new IntraStrategyBeanMsg();
		msg.addTrade(getNewBuyTrade(ERIC, price, qty));
		tradeHandlerBean.executeBean(getNewToken(), msg);
		
		msg = new IntraStrategyBeanMsg();
		msg.addTrade(getNewSellTrade(ERIC, price + profit, qty));
		tradeHandlerBean.executeBean(getNewToken(), msg);
		
		Assert.assertEquals((price+profit) * qty / MetaTraderConstants.DIVISOR, csd.getAccount(testAccId).getPosition(CASH).getLongQty());
	}
}
