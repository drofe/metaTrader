package org.bergefall.iobase.routing;

import java.util.ArrayList;
import java.util.List;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.config.ConfigurationException;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.iobase.client.MetaTraderClientChannelInitializer;
import org.bergefall.iobase.client.MetaTraderTcpClientHandlerBase;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RoutingClientBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {


	protected List<MsgClient> accountServers = new ArrayList<>();
	protected List<MsgClient> instrumentServers = new ArrayList<>();
	protected List<MsgClient> orderServers = new ArrayList<>();
	protected List<MsgClient> tradeServers = new ArrayList<>();
	protected List<MsgClient> marketDataServers = new ArrayList<>();
	protected List<MsgClient> all = new ArrayList<>();
 
	private static final long serialVersionUID = -6007578345427827284L;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		Status status = new Status();
		MetaTraderMessage msg = token.getTriggeringMsg();
		
		switch (msg.getMsgType()) {
		case Account :
			routeMessage(status, msg, accountServers);
		case MarketData :
			routeMessage(status, msg, marketDataServers);
		case Instrument:
			routeMessage(status, msg, instrumentServers);
		case Order:
			routeMessage(status, msg, orderServers);
		case Trade:
			routeMessage(status, msg, tradeServers);
			break;
		default:
			break;
		}
		return status;
	}
	
	protected void routeMessage(Status status, MetaTraderMessage msg, 
			List<MsgClient> to) {
		for (MsgClient client : to) {
			try {
				client.sendMsg(msg);
			} catch (InterruptedException e) {
				status.setCode(Status.ERROR);
				status.setMsg("Error while sending msg: " + msg.toString());
				return;
			}
		}
	}
	
	@Override
	public void parseConfig(MetaTraderConfig config) {
		all.add(new MsgClient("127.0.0.1", config.getIoLongConfig("port")));
		String accAddr = config.getRoutingString("accountAddr");
		populateClientList(accountServers, accAddr);
		String instrAddr = config.getRoutingString("instrumentAddr");
		populateClientList(instrumentServers, instrAddr);
		String orderAddr = config.getRoutingString("orderAddr");
		populateClientList(orderServers, orderAddr);
		String mdAddr = config.getRoutingString("marketDataAddr");
		populateClientList(marketDataServers, mdAddr);
		String tradeAddr = config.getRoutingString("tradeAddr");
		populateClientList(tradeServers, tradeAddr);
	}
	
	private void populateClientList(List<MsgClient> addressList, String config) {
		if (null == config ||
			config.isEmpty() ||
			config.equalsIgnoreCase("all")) {
			addressList = all;
			return;
		}
		String[] servers = config.split(",");
		for (String server : servers) {
			addressList.add(new MsgClient(server));		
		}
	}
	
	private class MsgClient {
		
		final String host;
		final Long port;
		
		public MsgClient(String host, Long port) {
			this.host = host;
			this.port = port;
		}
		
		/**
		 * 
		 * @param host ip:port
		 */
		public MsgClient(String host) {
			String[] hostPort = host.split(":");
			if (2 != hostPort.length) {
				throw new ConfigurationException("Error in config");
			}
			this.host = hostPort[0];
			this.port = Long.parseLong(hostPort[1]); 
		}
		
		public void sendMsg(MetaTraderMessage msg) throws InterruptedException {
			EventLoopGroup group = new NioEventLoopGroup();

			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group).channel(NioSocketChannel.class).handler(new MetaTraderClientChannelInitializer());

				// Create connection
				Channel c = bootstrap.connect(host, port.intValue()).sync().channel();

				// Get handle to handler so we can send message
				MetaTraderTcpClientHandlerBase handle = c.pipeline().get(MetaTraderTcpClientHandlerBase.class);
					handle.sendAsyncReq(msg);
				
				c.close();

			} finally {
				group.shutdownGracefully();
			}
		}
	}
}
