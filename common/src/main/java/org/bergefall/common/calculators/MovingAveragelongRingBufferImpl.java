package org.bergefall.common.calculators;

/**
 * Implementation of Moving Average as a ringbuffer of primitive long
 * @author ola
 *
 */
public class MovingAveragelongRingBufferImpl implements MovingAverage {

	private long[] buffer;
	private int windowSize;
	private int fillLevel;
	private long sum;
	private int bufPos;
	
	public MovingAveragelongRingBufferImpl(int windowSize) {
		this.windowSize = windowSize;
		buffer = new long[windowSize];
	}
	
	public void addValue(long val) {
		//If pos already occupied remove current val from sum.
		if (isFilled()) {
			sum -= buffer[bufPos];
		} else {
			fillLevel++;
		}
		
		buffer[bufPos] = val;
		sum += val;
		
		bufPos++;
		if (bufPos == windowSize) {
			bufPos = 0;
		}
	}
	
	public long getAverage() {
		return sum / fillLevel;
	}
	
	public int getAverageWindowSize() {
		return windowSize;
	}
	
	public boolean isFilled() {
		return fillLevel == windowSize;
	}
	
	@Override
	public String toString() {
		return "Avg: " + getAverage() + ", is filled: " + isFilled();
	}
	
}
