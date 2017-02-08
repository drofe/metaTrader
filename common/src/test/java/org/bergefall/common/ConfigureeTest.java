package org.bergefall.common;

import org.bergefall.common.config.MetaTraderBaseConfigureeImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigureeTest {

	MetaTraderConfig config;
	private static final String testConfigFile = "./src/main/resources/ConfigExample.properties";
	@Before
	public void setup() {
		config = new MetaTraderBaseConfigureeImpl(testConfigFile);
	}
	
	@Test
	public void test() {
		Long port = config.getIoLongConfig("port");
		Assert.assertNotNull(port);
		Assert.assertEquals(8348, port.longValue());
	}

}
