package org.bergefall.iobase.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	protected static String acc = "account";
	protected static String instr = "instrument";
	protected static String order = "order";
	protected static String trade = "trade";
	protected static String marketData = "marketData";
	protected static String all = "all";
	
	protected Set<MsgClient> accountServers = new HashSet<>();
	protected Set<MsgClient> instrumentServers = new HashSet<>();
	protected Set<MsgClient> orderServers = new HashSet<>();
	protected Set<MsgClient> tradeServers = new HashSet<>();
	protected Set<MsgClient> marketDataServers = new HashSet<>();
	protected Set<MsgClient> allServers = new HashSet<>();
	protected Map<String, Set<MsgClient>> serverMap = new HashMap<>();
	
	private static final long serialVersionUID = -6007578345427827284L;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		
		switch (msg.getMsgType()) {
		case Account :
			routeMessage(status, msg, serverMap.get(acc));
			break;
		case MarketData :
			routeMessage(status, msg, serverMap.get(marketData));
			break;
		case Instrument:
			routeMessage(status, msg, serverMap.get(instr));
			break;
		case Order:
			routeMessage(status, msg, serverMap.get(order));
			break;
		case Trade:
			routeMessage(status, msg, serverMap.get(trade));
			break;
		default:
			break;
		}
		return status;
	}
	
	protected void routeMessage(Status status, MetaTraderMessage msg, 
			Set<MsgClient> to) {
		if (to.isEmpty()) {
			return;
		}
		//Create a copy and remove router BLPs seq.no.
		MetaTraderMessage msgCpy = MetaTraderMessage.newBuilder(msg).setSeqNo(0).build();
		for (MsgClient client : to) {
			try {
				client.sendMsg(msgCpy);
			} catch (InterruptedException e) {
				status.setCode(Status.ERROR);
				status.setMsg("Error while sending msg: " + msg.toString());
				return;
			}
		}
	}
	
	@Override
	public void initBean(MetaTraderConfig config) {
		serverMap.put(all, allServers);
		serverMap.put(acc, accountServers);
		serverMap.put(marketData, marketDataServers);
		serverMap.put(instr, instrumentServers);
		serverMap.put(order, orderServers);
		serverMap.put(trade, tradeServers);
		
		allServers.add(new MsgClient("127.0.0.1", config.getIoLongConfig("port")));
		String accAddr = config.getRoutingString("accountAddr");
		populateClientList(acc, accAddr);
		String instrAddr = config.getRoutingString("instrumentAddr");
		populateClientList(instr, instrAddr);
		String orderAddr = config.getRoutingString("orderAddr");
		populateClientList(order, orderAddr);
		String mdAddr = config.getRoutingString("marketDataAddr");
		populateClientList(marketData, mdAddr);
		String tradeAddr = config.getRoutingString("tradeAddr");
		populateClientList(trade, tradeAddr);
	}
	
	private void populateClientList(String serverGroup, String config) {
		if (null == config ||
			config.isEmpty() ||
			config.equalsIgnoreCase("all")) {
			serverMap.put(serverGroup, allServers);
			return;
		}
		String[] servers = config.split(",");		 
		for (String server : servers) {
			MsgClient client = new MsgClient(server); 
			serverMap.get(serverGroup).add(client);
			serverMap.get(all).add(client);
		}
	}
	
	private class MsgClient {
		
		final String host;
		final Long port;
		private boolean initialized;
		private Channel channel;
		private EventLoopGroup group;
		private MetaTraderTcpClientHandlerBase handle;
		
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
				
		public void shutdown() {
			channel.close();
			group.shutdownGracefully();
			initialized = false;
		}
		
		public void sendMsg(MetaTraderMessage msg) throws InterruptedException {
			
			if(false == initialized) {
				init();
			}
			if (initialized) {
				handle.sendAsyncReq(msg);
			}
				
		}
		
		private void init() throws InterruptedException {
			group = new NioEventLoopGroup();
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new MetaTraderClientChannelInitializer());
			// Create connection
			channel = bootstrap.connect(host, port.intValue()).sync().channel();
			handle = channel.pipeline().get(MetaTraderTcpClientHandlerBase.class);
			initialized = true;
		}

	}
}
