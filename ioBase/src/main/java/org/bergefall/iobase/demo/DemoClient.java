package org.bergefall.iobase.demo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.InstrumentCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.iobase.client.MetaTraderClientChannelInitializer;
import org.bergefall.iobase.client.MetaTraderTcpClientHandlerBase;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DemoClient {

	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8348"));

	private SecureRandom random = new SecureRandom();

	public static void main(String[] args) throws InterruptedException {

		DemoClient client = new DemoClient();
		client.runClient();
	}

	public void runClient() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new MetaTraderClientChannelInitializer());

			// Create connection
			Channel c = bootstrap.connect(HOST, PORT).sync().channel();

			// Get handle to handler so we can send message
			MetaTraderTcpClientHandlerBase handle = c.pipeline().get(MetaTraderTcpClientHandlerBase.class);
			String accountNamePrefix = getRandomString();
			for (int i = 1; i < 110; i++) {
				MetaTraderMessage msg = null;
				if (0 == i % 2) {
					MarketDataCtx mdCtx = new MarketDataCtx("CINN",
							LocalDateTime.now(),
							Long.valueOf(i), Long.valueOf(2 * i), 2L, 3L, 4L, 5L, 7L, 6L, 8L, 9L);
					msg = MetaTraderMessageCreator.createMTMsg(mdCtx);
				} else {
					AccountCtx acc = new AccountCtx(accountNamePrefix + " -- "+ i, 0, "broker", "user");
					msg = MetaTraderMessageCreator.createMTMsg(acc);
				}
				handle.sendAsyncReq(msg);
				//Thread.sleep(50);
			}
			MetaTraderMessage instrMsg = MetaTraderMessageCreator
					.createMTMsg(new InstrumentCtx("TEST", 1));
			handle.sendAsyncReq(instrMsg);
			Thread.sleep(5000);
			c.close();

		} finally {
			group.shutdownGracefully();
		}

	}

	private String getRandomString() {
		return new BigInteger(80, random).toString(32);
	}
}
