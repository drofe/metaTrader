package org.bergefall.iobase.server;

import org.bergefall.base.beats.BeatsGenerator;
import org.bergefall.common.config.ConfigurationException;
import org.bergefall.common.config.MetaTraderBaseConfigureeImpl;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.blp.BusinessLogicPipline;

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
	protected BusinessLogicPipline blp;
	protected MetaTraderConfig config;
	protected BeatsGenerator beatGenerator;
	
	protected void initServer(String configFile) {
		// Create event loop groups. One for incoming connections handling and
		// second for handling actual event by workers
		this.serverGroup = getServerGroup();
		this.workerGroup = getWorkerGroup();
		this.blp = getBLP();
		this.config = getConfig(configFile);
		this.bootStrap = getBootStrap();
		this.beatGenerator = new BeatsGenerator(this.blp, this.config);
		Thread businessPileLine = new Thread(blp);
		businessPileLine.start();
		Thread beatGen = new Thread(beatGenerator);
		beatGen.start();
		
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
			blp.shutdown();
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
	
	
	
	protected abstract BusinessLogicPipline getBLP();
	
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
		return new MetaTraderServerChannelInitializer(blp);
	}

	protected Class<? extends ServerChannel> getServerChannel() {
		return NioServerSocketChannel.class;
	}
}
