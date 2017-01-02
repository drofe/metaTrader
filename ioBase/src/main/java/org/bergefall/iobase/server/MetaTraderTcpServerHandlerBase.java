package org.bergefall.iobase.server;

import org.bergefall.iobase.blp.BusinessLogicPipline;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MetaTraderTcpServerHandlerBase extends SimpleChannelInboundHandler<MetaTraderMessage> {

	private BusinessLogicPipline businessPipeline;
	
	public MetaTraderTcpServerHandlerBase(BusinessLogicPipline blp) {
		super();
		businessPipeline = blp;
	}
	
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MetaTraderMessage msg)
      throws Exception {
	  businessPipeline.enqueue(msg);
//    MetaTraderMessage.Builder builder = MetaTraderMessage.newBuilder();
//    builder.setMarketData(MarketData.newBuilder().setDate("Accepted from Server, returning response"));
//    ctx.write(builder.build());
    
  }
  
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
  }

}