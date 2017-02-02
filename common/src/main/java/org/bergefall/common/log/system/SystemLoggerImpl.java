package org.bergefall.common.log.system;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class SystemLoggerImpl implements SystemLoggerIf {

	private static SystemLoggerImpl cInstance;
	private static final Logger logger = LoggerFactory.getLogger(SystemLoggerImpl.class);
	private BlockingQueue<LogEntry> logQueue;
	private static boolean logInOwnTread = false;
	
	private SystemLoggerImpl() {
		//Private by design.
		logQueue = new LinkedBlockingQueue<>();
	}
	
	public static SystemLoggerImpl get() {
		if (null == cInstance) {
			cInstance = new SystemLoggerImpl();
		}
		return cInstance;
	}
	
	@Override
	public void trace(String msg) {
		if (!logger.isTraceEnabled()) {
			return;
		}
		LogEntry entry = new LogEntry(new Exception(), msg);
		if (logInOwnTread) {
			logQueue.offer(entry);
			return;
		}
		logger.trace(formatLogEntry(entry));
	}
	
	@Override
	public void info(String msg) {
		if (!logger.isInfoEnabled()) {
			return;
		}
		LogEntry entry = new LogEntry(new Exception(), msg, Level.INFO);
		if (logInOwnTread) {
			logQueue.offer(entry);
			return;
		}
		logger.info(formatLogEntry(entry));
	}
	
	@Override
	public void error(String msg) {
		if (!logger.isErrorEnabled()) {
			return;
		}
		LogEntry entry = new LogEntry(new Exception(), msg, Level.ERROR);
		if (logInOwnTread) {
			logQueue.offer(entry);
			return;
		}
		logger.error(formatLogEntry(entry));
	}
	
	
	private void doLog(LogEntry entry) {
		if (null == entry || null == entry.getLogLevel()) {
			return;
		}
		switch (entry.getLogLevel().toInt()) {
		case Level.TRACE_INT:
			logger.trace(formatLogEntry(entry));
			break;
		case Level.INFO_INT:
			logger.info(formatLogEntry(entry));
			break;
		case Level.DEBUG_INT:
			logger.debug(formatLogEntry(entry));
			break;
		case Level.WARN_INT:
			logger.warn(formatLogEntry(entry));
			break;
		case Level.ERROR_INT:
			logger.error(formatLogEntry(entry));
			break;
		case Level.OFF_INT:
		default:
			break;
		}
	}
	
	public void setLogInOwnThread(boolean logInThread) {
		if (logInOwnTread && logInThread) {
			return; //Already running.
		}
		logInOwnTread = logInThread;
		Thread logThread = new Thread(new loggingThread());
		logThread.setDaemon(true);
		logThread.setName("SystemLoggerThread");
		logThread.start();
	}
	
	private String formatLogEntry(LogEntry entry) {
		return entry.getCallingClass() + "." + entry.getCallingMethod() + "() L:" + entry.getCallingLine() +
				"\n    " + entry.getMessage();
	}
	
	private class loggingThread implements Runnable {

		@Override
		public void run() {
			while(logInOwnTread || !logQueue.isEmpty()) { //Empty queue if turned off.
				try {
					doLog(logQueue.poll(1000, TimeUnit.MILLISECONDS));
				} catch (InterruptedException e) {
					logger.error("Exception: " + e.getMessage());
				}
			}
		}
	}
	
	private class LogEntry {
		
		final Exception stack;
		final String message;
		final Level logLevel;
		
		LogEntry(Exception stack, String msg) {
			this.stack = stack;
			this.message = msg;
			this.logLevel = Level.TRACE;
		}
		
		LogEntry(Exception stack, String msg, Level level) {
			this.stack = stack;
			this.message = msg;
			this.logLevel = level;
			
		}
		
		String getMessage() {
			return message;
		}
		Level getLogLevel () {
			return logLevel;
		}
		String getCallingMethod() {
			StackTraceElement[] stacktraceArr = stack.getStackTrace();
			String methodName = stacktraceArr[1].getMethodName();
			return null != methodName ? methodName : "";

		}
		
		String getCallingClass() {
			StackTraceElement[] stacktraceArr = stack.getStackTrace();
			String className = stacktraceArr[1].getClassName();
			return null != className ? className : "";
	    }
		
		int getCallingLine() {
	        StackTraceElement[] tStackTraceArray = stack.getStackTrace();
	        int number = tStackTraceArray[1].getLineNumber();
	        return number;
	    }
	}
}
