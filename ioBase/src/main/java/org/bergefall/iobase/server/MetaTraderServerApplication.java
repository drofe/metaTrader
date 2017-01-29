package org.bergefall.iobase.server;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.common.config.ConfigurationException;
import org.bergefall.common.config.MetaTraderBaseConfigureeImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public abstract class MetaTraderServerApplication {

	protected ServerBootstrap bootStrap;
	protected EventLoopGroup serverGroup;
	protected EventLoopGroup workerGroup;
	protected List<BusinessLogicPipeline> blps;
	protected List<Thread> blpThreads;
	protected MetaTraderConfig config;
	
	protected void initServer(String configFile) {
		// Create event loop groups. One for incoming connections handling and
		// second for handling actual event by workers
		this.serverGroup = getServerGroup();
		this.workerGroup = getWorkerGroup();
		this.config = getConfig(configFile);
		this.blps = getBLPs(config);
		this.bootStrap = getBootStrap();
		
		blpThreads = new ArrayList<>(blps.size());
		for (BusinessLogicPipeline blp : blps) {
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
	
	protected void startListening() throws InterruptedException {
		try {
			// Bind to port
			bootStrap.bind(getPort()).sync().channel().closeFuture().sync();
		} finally {
			serverGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			for (BusinessLogicPipeline blp1 : blps) {
				blp1.shutdown();
			}
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
