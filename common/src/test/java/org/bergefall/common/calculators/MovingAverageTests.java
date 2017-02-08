package org.bergefall.common.calculators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bergefall.common.calculators.MovingAverage;
import org.bergefall.common.calculators.MovingAveragelongRingBufferImpl;
import org.junit.Before;
import org.junit.Test;

public class MovingAverageTests {

	private static final int CAP = 20;
	@Before
	public void setup() {
		
	}
	@Test
	public void longRingbufferMATest() {
		MovingAverage ma = new MovingAveragelongRingBufferImpl(CAP);
		longMAsTest(ma);
	}
	
	private void longMAsTest(MovingAverage ma) {
		assertEquals(CAP, ma.getAverageWindowSize());
		ma.addValue(20);
		assertFalse(ma.isFilled());
		for (int i = 0; i < CAP; i++) {
			ma.addValue(20L);
		}
		assertTrue(ma.isFilled());
		assertEquals(20L, ma.getAverage());
		for (int i = 0; i < CAP; i++) {
			ma.addValue(100L);
		}
		assertTrue(ma.isFilled());
		assertEquals(100L, ma.getAverage());
		for (int i = 0; i < CAP  / 2; i++) {
			ma.addValue(20L);
		}
		assertEquals(100L/2L + 20L/2L, ma.getAverage());
	}

}
