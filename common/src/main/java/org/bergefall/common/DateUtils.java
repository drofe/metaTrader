package org.bergefall.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**
	 * This is heavy as it instantiates a new SimpleDateFormat.
	 * Use in debug/demo code only
	 * @return
	 */
	public static String getCurrentTimeAsReadableDate() {
		long currnetTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd, HH:mm:ss.SSS");    
		Date resultdate = new Date(currnetTime);
		return sdf.format(resultdate);
	}
}
