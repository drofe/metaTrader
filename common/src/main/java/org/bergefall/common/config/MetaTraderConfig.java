package org.bergefall.common.config;

public interface MetaTraderConfig {
	public static final String COMMON = "common.";
	public static final String MESSAGING = "messaging.";
	public static final String IO = "io.";
	public static final String STRATEGY = "strategy.";
	public static final String BLP = "blp.";
	public static final String ROUTING = "routing.";
	
	
	public Long getCommonLong(String propKey);
	
	public Double getCommonDoubleConfig(String propKey);
	
	public String getCommonStringConfig(String propKey);
	
	public Long getIoLongConfig(String propKey);
	
	public Double getIoDoubleConfig(String propKey);
	
	public String getIoString(String propKey);
	
	public Boolean getBlpBoolean(String propKey);
	
	public Boolean getBlpBoolean(int blpNr, String propKey);

	public Long getBlpLong(String propKey);

	public Long getBlpLong(int blpNr, String propKey);
	
	public String getBlpString(int blpNr, String propKey);
	
	public String getBlpString(String propKey);
	
	public String getRoutingString(String propKey);
	
	/**
	 * Gets a generic boolean config value.
	 * Default is false
	 * @param prefix Prefix. Should normally be calling class (Bean)
	 * @param propKey Key.
	 * @return value, default false.
	 */
	public boolean getBooleanProperty(String prefix, String propKey);
	
	/**
	 * Gets a generic long config value.
	 * Default is 0
	 * @param prefix Prefix. Should normally be calling class (Bean)
	 * @param propKey Key.
	 * @return value, default 0L.
	 */
	public long getLongProperty(String prefix, String propKey);
}
