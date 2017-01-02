package org.bergefall.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		File file = new File(configFile);
		defaults = getDefaults();
		try {
			FileReader fileReader = new FileReader(file);
			configProperties = new Properties(defaults);
			configProperties.load(fileReader);
		} catch (FileNotFoundException e) {
			log.error("Couldn't find specified log file: " + configFile + ". (Current dir: " + 
		System.getProperty("user.dir") + ")\n" + e);
		} catch (IOException e) {
			log.error("Couldn't parse specified log file: " + configFile + ". \n" + e);
		}
	}
	
	private Properties getDefaults() {
		Properties def = new Properties();
		def.setProperty("common.processname", "metaTraderService");
		return def;
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
	public Long getCommonLongConfig(String propKey) {
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
	public String getIoStringConfig(String propKey) {
		return getStringProperty(IO + propKey);
	}
	
	private Long getLongProperty(String key) {
		checkInitialized();
		String prop = configProperties.getProperty(key);
		if (null == prop) {
			return null;
		}
		try {
			return Long.valueOf(prop);
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
}
