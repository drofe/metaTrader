package org.bergefall.common.log.system;

public interface SystemLoggerIf {

	/**
	 * Turn on/off logging in a separate thread.
	 * @param 
	 */
	public void setLogInOwnThread(boolean b);

	/**
	 * Log trace level logging.
	 * @param string
	 */
	public void trace(String msg);
	
	/**
	 * Log error level logging.
	 * @param string
	 */
	public void error(String msg);

	/**
	 * Log info level logging.
	 * @param msg
	 */
	public void info(String msg);
}
