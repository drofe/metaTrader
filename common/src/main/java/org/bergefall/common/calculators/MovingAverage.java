package org.bergefall.common.calculators;

public interface MovingAverage {

	/**
	 * Add value to moving average calculator
	 * @param val
	 */
	public void addValue(long val);
	
	/**
	 * Get average of values.
	 * @return average
	 */
	public long getAverage();
	
	/**
	 * Check if there are enough data points to return appropriate average.
	 * @return true if filled
	 */
	public boolean isFilled();
	
	/**
	 * Returns the window size for average calculation for this instance.
	 * @return
	 */
	public int getAverageWindowSize();
}
