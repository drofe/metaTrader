package org.bergefall.se.test.beans;

import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.basicbeans.BackTestBean;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.se.test.StrategyEngineTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BackTestingBeanTest extends StrategyEngineTestBase {
	
	private BackTestBean backTestBean;
	
	@Before
	public void setup() {
		initCsd();
		backTestBean = new BackTestBean();
		backTestBean.initBean(config);
	}
	
	@Test
	public final void testSimpleSellAllAmountWithProfit() {
		long qty = qty(1000L);
		long buyPrice = price(9L);
		long sellPrice = price(10L);
		
		//Add position to sell.
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(qty, buyPrice);
		
		//Create order
		OrderCtx order = new OrderCtx(ERIC, qty, true);
		order.setPrice(sellPrice);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		intraMsg.addOrder(order);
		backTestBean.executeBean(getNewWithMdMsgToken(), intraMsg);
		Assert.assertEquals(1, intraMsg.getTrades().size());
		Assert.assertEquals(0L, intraMsg.getTrades().get(0).getCommission().longValue());
		Assert.assertEquals(sellPrice, intraMsg.getTrades().get(0).getPrice().longValue());
		Assert.assertEquals((sellPrice - buyPrice) * qty / MetaTraderConstants.DIVISOR, 
				intraMsg.getTrades().get(0).getGrossProfit().longValue());
		Assert.assertEquals((sellPrice - buyPrice) * qty / MetaTraderConstants.DIVISOR, 
				intraMsg.getTrades().get(0).getNetProfit().longValue());
	}
	
	@Test
	public final void testSimpleSellAllAmountWithLoss() {
		long qty = qty(1000L);
		long buyPrice = price(9L);
		long sellPrice = price(8L);
		
		//Add position to sell.
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(qty, buyPrice);
		
		//Create order
		OrderCtx order = new OrderCtx(ERIC, qty, true);
		order.setPrice(sellPrice);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		intraMsg.addOrder(order);
		backTestBean.executeBean(getNewWithMdMsgToken(), intraMsg);
		Assert.assertEquals(1, intraMsg.getTrades().size());
		Assert.assertEquals(0L, intraMsg.getTrades().get(0).getCommission().longValue());
		Assert.assertEquals(sellPrice, intraMsg.getTrades().get(0).getPrice().longValue());
		Assert.assertEquals((sellPrice - buyPrice) * qty / MetaTraderConstants.DIVISOR, 
				intraMsg.getTrades().get(0).getGrossProfit().longValue());
		Assert.assertEquals((sellPrice - buyPrice) * qty / MetaTraderConstants.DIVISOR, 
				intraMsg.getTrades().get(0).getNetProfit().longValue());
	}
	
	@Test
	public final void testSimpleSellPartAmountWithLoss() {
		long buyqty = qty(1000L);
		long sellqty = qty(200L);
		long buyPrice = price(9L);
		long sellPrice = price(8L);
		
		//Add position to sell.
		PositionCtx pos = csd.getAccount(testAccId).getPosition(ERIC);
		pos.addLongQty(buyqty, buyPrice);
		
		//Create order
		OrderCtx order = new OrderCtx(ERIC, sellqty, true);
		order.setPrice(sellPrice);
		IntraStrategyBeanMsg intraMsg = new IntraStrategyBeanMsg();
		intraMsg.addOrder(order);
		backTestBean.executeBean(getNewWithMdMsgToken(), intraMsg);
		Assert.assertEquals(1, intraMsg.getTrades().size());
		Assert.assertEquals(0L, intraMsg.getTrades().get(0).getCommission().longValue());
		Assert.assertEquals(sellPrice, intraMsg.getTrades().get(0).getPrice().longValue());
		Assert.assertEquals((sellPrice - buyPrice) * sellqty / MetaTraderConstants.DIVISOR, 
				intraMsg.getTrades().get(0).getGrossProfit().longValue());
		Assert.assertEquals((sellPrice - buyPrice) * sellqty / MetaTraderConstants.DIVISOR, 
				intraMsg.getTrades().get(0).getNetProfit().longValue());
		
	}

}
