package org.bergefall.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;

public class MetaTraderBaseConfigureeImpl implements MetaTraderConfig {

	private Properties configProperties;
	private Properties defaults;
	private SystemLoggerIf log = SystemLoggerImpl.get();
	
	
	public MetaTraderBaseConfigureeImpl(String configFile) {
		defaults = getDefaults();
		if (null == configFile) {
			//Only use defaults. Mainly for testing purposes.
			configProperties = new Properties(defaults);
			return;
		}
		File file = new File(configFile);
		configProperties = new Properties(defaults);
		if (file.exists() && file.isFile()) {
			try {

				FileReader fileReader = new FileReader(file);
				configProperties.load(fileReader);

			} catch (FileNotFoundException e) {
				log.error("Couldn't find specified log file: " + configFile + ". (Current dir: "
						+ System.getProperty("user.dir") + ")\n" + e);
			} catch (IOException e) {
				log.error("Couldn't parse specified log file: " + configFile + ". \n" + e);
			}
		} else {
			//Add leading "/" to find in classpath
			if (false == configFile.startsWith("/")) {
				configFile = "/" + configFile;
			}
			try (final InputStream stream =
			           this.getClass().getResourceAsStream(configFile)) {
				if (null != stream) {
					configProperties.load(stream);
				} else {
					log.error("Couldn't find specified log file: " + configFile + " in classpath.");
				}
			} catch (IOException | NullPointerException e) {
				log.error("Couldn't parse specified log file: " + configFile + ". \n" + 
						SystemLoggerIf.getStacktrace(e));		
				}
		}
	}
	
	public Map<String, String> getAllConfigsFor(String configSet) {
		Set<String> propertyNames = configProperties.stringPropertyNames();
		if (propertyNames.isEmpty()) {
			return null;
		}
		Map<String, String> configs = new HashMap<>();
		for(String name : propertyNames) {
			if (name.startsWith(configSet)) {
				configs.put(name, configProperties.getProperty(name));
			}
		}
		return configs;
	}
	
	@Override
	public Long getCommonLong(String propKey) {
		return getLongProperty(COMMON + propKey);
	}
	
	@Override
	public Double getCommonDoubleConfig(String propKey) {
		return getDoubleProperty(COMMON + propKey);
	}
	
	@Override
	public String getCommonStringConfig(String propKey) {
		return getStringProperty(COMMON + propKey);
	}
	
	@Override
	public Long getIoLongConfig(String propKey) {
		return getLongProperty(IO + propKey);
	}
	
	@Override
	public Double getIoDoubleConfig(String propKey) {
		return getDoubleProperty(IO + propKey);
	}
	
	@Override
	public String getIoString(String propKey) {
		return getStringProperty(IO + propKey);
	}
	
	@Override
	public Boolean getBlpBoolean(String propKey) {
		return getBooleanProperty(BLP + propKey);
	}

	@Override
	public Boolean getBlpBoolean(int blpNr, String propKey) {
		Boolean prop = getBooleanProperty(BLP + blpNr + "." + propKey);
		if (null == prop) {
			prop = getBlpBoolean(propKey);
		}
		return prop;
	}
	

	@Override
	public boolean getBooleanProperty(String prefix, String propKey) {
		String key = null;
		if (prefix.endsWith(".")) {
			key = prefix + propKey;
		} else {
			key = prefix + "." + propKey;
		}
		Boolean prop = getBooleanProperty(key);
		if (null == prop) {
			return false;
		}
		return prop.booleanValue();
	}
	
	@Override
	public long getLongProperty(String prefix, String propKey) {
		String key = null;
		if (prefix.endsWith(".")) {
			key = prefix + propKey;
		} else {
			key = prefix + "." + propKey;
		}
		Long prop = getLongProperty(key);
		if (null == prop) {
			return 0L;
		}
		return prop.longValue();
	}
	
	@Override
	public Long getBlpLong(String propKey) {
		return getLongProperty(BLP + propKey);
	}
	
	@Override
	public Long getBlpLong(int blpNr, String propKey) {
		Long prop = getLongProperty(BLP + blpNr + "." + propKey);
		if (null == prop) {
			prop = getBlpLong(propKey);
		}
		return prop;
	}
	
	@Override
	public String getBlpString(String propKey) {
		return getStringProperty(BLP + propKey);
	}
	
	@Override
	public String getBlpString(int blpNr, String propKey) {
		String prop = getStringProperty(BLP + blpNr + "." + propKey);
		if (null == prop) {
			prop = getBlpString(propKey);
		}
		return prop;
	}
	
	@Override
	public String getRoutingString(String propKey) {
		return getStringProperty(ROUTING + propKey);
	}
	
	@Override
	public String toString() {
		StringBuilder strBldr = new StringBuilder();
		strBldr.append("Current configuration:" + System.lineSeparator());
		for (String key : configProperties.stringPropertyNames()) {
			String value = configProperties.getProperty(key);
			strBldr.append(key + ":" + value + System.lineSeparator());
		}
		return strBldr.toString();
	}
	
	protected Properties getProperties() {
		return configProperties;
	}
	
	private Boolean getBooleanProperty(String key) {
		checkInitialized();
		String prop = configProperties.getProperty(key);
		if (null == prop) {
			return null;
		}
		return Boolean.valueOf(prop);
	}
	
	private Long getLongProperty(String key) {
		checkInitialized();
		String prop = configProperties.getProperty(key);
		if (null == prop) {
			return null;
		}
		try {
			return Long.valueOf(prop.replaceAll("_", ""));
		} catch (NumberFormatException e) {
			log.error("Faild to parse key " + key + " to Integer");
			return null;
		}
	}
	
	private Double getDoubleProperty(String key) {
		checkInitialized();
		String prop = configProperties.getProperty(key);
		if (null == prop) {
			return null;
		}
		try {
			return Double.valueOf(prop);
		} catch (NumberFormatException e) {
			log.error("Faild to parse key " + key + " to Integer");
			return null;
		}
	}
	
	private String getStringProperty(String key) {
		checkInitialized();
		String prop = configProperties.getProperty(key);
		if (null == prop) {
			return null;
		}
		return prop;
	}
	
	private void checkInitialized() {
		if (null == configProperties) {
			throw new ConfigurationException("Config is not propery initialized from config file!");
		}
	}
	
	private Properties getDefaults() {
		Properties def = new Properties();
		def.setProperty(COMMON + "processname", "metaTraderService");
		def.setProperty(IO + "port", "8348");
		def.setProperty(BLP + "runPreMDStrategy", "false");
		def.setProperty(BLP + "runPreAccStrategy", "true");
		def.setProperty(BLP + "runPreInstrStrategy", "true");
		def.setProperty(BLP + "runPreTradeStrategy", "false");
		def.setProperty(BLP + "runPreOrderStrategy", "false");
		def.setProperty(BLP + "runPreBeatStrategy", "true");
		def.setProperty(BLP + "beatInterval", "5000");
		def.setProperty(BLP + "runHandleMarketData", "false");
		def.setProperty(BLP + "runHandleAcc", "true");
		def.setProperty(BLP + "runHandleInstr", "false");
		def.setProperty(BLP + "runHandleBeat", "false");
		def.setProperty(BLP + "runHandleTrade", "false");
		def.setProperty(BLP + "runHandleOrder", "false");		
		def.setProperty(ROUTING + "accountAddr", "all");
		def.setProperty(ROUTING + "instrumentAddr", "all");
		def.setProperty(ROUTING + "orderAddr", "all");
		def.setProperty(ROUTING + "marketDataAddr", "all");
		def.setProperty(ROUTING + "tradeAddr", "all");
		return def;
	}
}
