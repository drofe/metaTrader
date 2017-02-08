package org.bergefall.common.data;

import org.junit.Assert;
import org.junit.Test;

public class DataContextTest {

	@Test
	public final void testLongQtyInPositionCtx() {
		PositionCtx ctx = new PositionCtx("TEST");
		
		ctx.addLongQty(4000000, 229300000);
		Assert.assertEquals(4000000, ctx.getLongQty());
		Assert.assertEquals(229300000, ctx.getAvgLongPrice());
		
		ctx.addLongQty(4000000, 229300000);
		Assert.assertEquals(2 * 4000000, ctx.getLongQty());
		Assert.assertEquals(229300000, ctx.getAvgLongPrice());
		
		ctx.removeLongQty(4000000, 229300000);
		Assert.assertEquals(4000000, ctx.getLongQty());
		Assert.assertEquals(229300000, ctx.getAvgLongPrice());
		
		ctx.removeLongQty(4000000, 229300000);
		Assert.assertEquals(0, ctx.getLongQty());
		Assert.assertEquals(0, ctx.getAvgLongPrice());
		
		ctx.addLongQty(0, 20000000);
		Assert.assertEquals(0, ctx.getLongQty());
		Assert.assertEquals(0, ctx.getAvgLongPrice());
		
		ctx.addLongQty(100000000, 10000000);
		Assert.assertEquals(100000000, ctx.getLongQty());
		Assert.assertEquals(10000000, ctx.getAvgLongPrice());
		
		ctx.addLongQty(100000000, 20000000);
		Assert.assertEquals(2 * 100000000, ctx.getLongQty());
		Assert.assertEquals(15000000, ctx.getAvgLongPrice());
		
		ctx.addLongQty(0, 20000000);
		Assert.assertEquals(2 * 100000000, ctx.getLongQty());
		Assert.assertEquals(15000000, ctx.getAvgLongPrice());
		
		ctx.addLongQty(100000000, 0);
		Assert.assertEquals(3 * 100000000, ctx.getLongQty());
		Assert.assertEquals(10000000, ctx.getAvgLongPrice());
		
	}

	@Test
	public final void testShortQtyInPositionCtx() {
		PositionCtx ctx = new PositionCtx("TEST");
		
		ctx.addShortQty(4000000, 229300000);
		Assert.assertEquals(4000000, ctx.getShortQty());
		Assert.assertEquals(229300000, ctx.getAvgShortPrice());
		
		ctx.addShortQty(4000000, 229300000);
		Assert.assertEquals(2 * 4000000, ctx.getShortQty());
		Assert.assertEquals(229300000, ctx.getAvgShortPrice());
		
		ctx.removeShortQty(4000000, 229300000);
		Assert.assertEquals(4000000, ctx.getShortQty());
		Assert.assertEquals(229300000, ctx.getAvgShortPrice());
		
		ctx.removeShortQty(4000000, 229300000);
		Assert.assertEquals(0, ctx.getShortQty());
		Assert.assertEquals(0, ctx.getAvgShortPrice());
		
		ctx.addShortQty(0, 20000000);
		Assert.assertEquals(0, ctx.getShortQty());
		Assert.assertEquals(0, ctx.getAvgShortPrice());
		
		ctx.addShortQty(100000000, 10000000);
		Assert.assertEquals(100000000, ctx.getShortQty());
		Assert.assertEquals(10000000, ctx.getAvgShortPrice());
		
		ctx.addShortQty(100000000, 20000000);
		Assert.assertEquals(2 * 100000000, ctx.getShortQty());
		Assert.assertEquals(15000000, ctx.getAvgShortPrice());
		
		ctx.addShortQty(0, 20000000);
		Assert.assertEquals(2 * 100000000, ctx.getShortQty());
		Assert.assertEquals(15000000, ctx.getAvgShortPrice());
		
		ctx.addShortQty(100000000, 0);
		Assert.assertEquals(3 * 100000000, ctx.getShortQty());
		Assert.assertEquals(10000000, ctx.getAvgShortPrice());
		
	}
	
}
