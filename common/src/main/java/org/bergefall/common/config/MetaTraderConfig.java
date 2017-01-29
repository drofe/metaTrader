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

	public Long getBlpLong(String propKey);
	
	public String getRoutingString(String propKey);
}
