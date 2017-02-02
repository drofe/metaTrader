package org.bergefall.backtest.client;

import java.util.Set;

import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.dbstorage.ReadHistoricalEqPrices;
import org.bergefall.iobase.client.MetaTraderClientChannelInitializer;
import org.bergefall.iobase.client.MetaTraderTcpClientHandlerBase;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BacktestCLIClient {

	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8348"));
	ReadHistoricalEqPrices priceReader;
	
	public static void main(String[] args) throws InterruptedException {
		
		BacktestCLIClient client = new BacktestCLIClient();
		String symb = null;
		if (args.length == 1) {
			symb = args[0];
		} else {
			symb = "ERIC";
		}
		client.runClient(symb);
	}
	
	public void runClient(String symb) {
		priceReader = new ReadHistoricalEqPrices();
		Set<MarketDataCtx> priceSet = priceReader.getAllPricesForSymb(symb);
		try {
			sendData(priceSet);
		} catch (InterruptedException e) {
			
		}
	}
	
	private void sendData(Set<MarketDataCtx> prices) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new MetaTraderClientChannelInitializer());

			// Create connection
			Channel c = bootstrap.connect(HOST, PORT).sync().channel();

			// Get handle to handler so we can send message
			MetaTraderTcpClientHandlerBase handle = c.pipeline().get(MetaTraderTcpClientHandlerBase.class);
	
			for (MarketDataCtx price :  prices) {
				MetaTraderMessage msg = MetaTraderMessageCreator.createMTMsg(price);
				handle.sendAsyncReq(msg);
			}
			Thread.sleep(5000);
			c.close();

		} finally {
			group.shutdownGracefully();
		}

	}
	
}
