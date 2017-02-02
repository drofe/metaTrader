package org.bergefall.common.log.system;

import java.io.PrintWriter;
import java.io.StringWriter;

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
	
	/**
	 * Get stacktrace from throwable.
	 * @param th
	 * @return Stacktrace as string.
	 */
	public static String getStacktrace(final Throwable th) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		th.printStackTrace(pw);
		return sw.getBuffer().toString();
		}
	}
