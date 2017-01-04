package org.bergefall.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
	
	private static String dateTimeFormatString = "yyyy-mm-dd HH:mm:ss.SSS";
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatString);
	/**
	 * This is heavy as it instantiates a new SimpleDateFormat.
	 * Use in debug/demo code only
	 * @return
	 */
	public static String getCurrentTimeAsReadableDate() {
		long currnetTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormatString);
		Date resultdate = new Date(currnetTime);
		return sdf.format(resultdate);
	}
	
	public static LocalDateTime convertStringToLDT(String dateTimeString) {
		return LocalDateTime.parse(dateTimeString, formatter);
	}
	
	public static LocalDateTime getDateTimeFromTimestamp(long timestamp) {
	    if (timestamp == 0)
	      return null;
	    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone
	        .getDefault().toZoneId());
	}
	
	public static Timestamp getSqlTimeStampFromLDT(LocalDateTime ldt) {
		return Timestamp.valueOf(ldt);
	}
}
