package org.bergefall.common.log.system;

public interface SystemLoggerIf {

	/**
	 * Turn on/off logging in a separate thread.
	 * @param 
	 */
	void setLogInOwnThread(boolean b);

	/**
	 * Log trace level logging.
	 * @param string
	 */
	void trace(String string);

	/**
	 * Log info level logging.
	 * @param msg
	 */
	public void info(String msg);
}
