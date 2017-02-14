package org.bergefall.iobase.server;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.common.config.ConfigurationException;
import org.bergefall.common.config.MetaTraderBaseConfigureeImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;
import org.bergefall.iobase.blp.BusinessLogicPipeline;
import org.bergefall.iobase.routing.RoutingPipeline;
import org.bergefall.iobase.web.WebService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public abstract class MetaTraderServerApplication implements Runnable {
	
	protected static final SystemLoggerIf log = SystemLoggerImpl.get();
	protected ServerBootstrap bootStrap;
	protected EventLoopGroup serverGroup;
	protected EventLoopGroup workerGroup;
	protected BusinessLogicPipeline routingBlp;
	protected CommonStrategyData csd;
	protected Thread routingThread;
	protected WebService webService;
	protected Thread webServiceThread;
	protected List<BusinessLogicPipeline> blps;
	protected List<Thread> blpThreads;
	protected MetaTraderConfig config;
	
	protected void initServer(String configFile) {
		// Create event loop groups. One for incoming connections handling and
		// second for handling actual event by workers
		this.serverGroup = getServerGroup();
		this.workerGroup = getWorkerGroup();
		this.config = getConfig(configFile);
		csd = getCSD();
		routingBlp = new RoutingPipeline(config, csd);
		routingThread = new Thread(routingBlp);
		this.blps = getBLPs(config);		
		this.bootStrap = getBootStrap();
		routingThread.start();
		initWebService(config);
		if (null != webService) {
			webServiceThread = new Thread(webService);
			webServiceThread.start();
		}
		blpThreads = new ArrayList<>(blps.size());
		for (BusinessLogicPipeline blp : blps) {
			blp.setRoutingBlp(routingBlp);
			Thread businessPipeLine = new Thread(blp);
			blpThreads.add(businessPipeLine);
			businessPipeLine.start();
			Thread beatGen = new Thread(new BeatsGenerator(blp, this.config));
			beatGen.start();
		}
		
		
		bootStrap.group(serverGroup, workerGroup)
			.channel(getServerChannel())
			.handler(getPrimaryHandler())
			.childHandler(getChildHandler());

	}
	
	protected void initWebService(MetaTraderConfig config) {
		webService = getWebServiceImpl(config);
		webService.get("/hello", (request, response) -> "Hello world") ;
	}
	
	protected WebService getWebServiceImpl(MetaTraderConfig config) {
		return new WebService(config, csd);
	}

	private void startListening() throws InterruptedException {
		try {
			// Bind to port
			bootStrap.bind(getPort()).sync().channel().closeFuture().sync();
		} finally {
			shutdown();
		}
	}
	protected int getPort() {
		Long port = config.getIoLongConfig("port");
		if (null == port) {
			throw new ConfigurationException("Port number is not specified correctly in config file.");
		}
		if (port < Integer.MIN_VALUE || port > Integer.MAX_VALUE ) {
			throw new ConfigurationException("port number is not within valid range (+/- INT_MAX), " 
					+ port.longValue());
		}
		return port.intValue();
	}
	
	@Override
	public void run() {
		try {
			startListening();
		} catch (Exception e) {
			log.error("Exception cought in netty listening layer. Shutting down");
			shutdown();
		}
	}
	
	protected CommonStrategyData getCSD() {
		return new CommonStrategyData();
	}
	
	protected void shutdown() {
		serverGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		for (BusinessLogicPipeline blp1 : blps) {
			blp1.shutdown();
		}
		routingBlp.shutdown();
		int safeCtr = 0;
		while ((workerGroup.isShuttingDown() ||
				workerGroup.isShuttingDown()) && safeCtr < 10) {
			try {
				safeCtr++;
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error(e.toString());
				// by design
			}
		}
				
	}

	protected abstract List<BusinessLogicPipeline> getBLPs(MetaTraderConfig config);
	
	protected ServerBootstrap getBootStrap() {
		return new ServerBootstrap();
	}
	
	protected EventLoopGroup getServerGroup() {
		return new NioEventLoopGroup(1);
	}
	
	protected EventLoopGroup getWorkerGroup() {
		return new NioEventLoopGroup();
	}
	
	protected MetaTraderConfig getConfig(String configFile) {
		return new MetaTraderBaseConfigureeImpl(configFile);
	}
	
	protected ChannelHandler getPrimaryHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
	protected ChannelHandler getChildHandler() {
		return new MetaTraderServerChannelInitializer(blps);
	}

	protected Class<? extends ServerChannel> getServerChannel() {
		return NioServerSocketChannel.class;
	}
}
