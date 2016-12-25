package org.bergefall.iobase.demo;

import org.bergefall.protocol.marketdata.MarketDataProtos.MetaTraderMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class DemoClientHandler extends SimpleChannelInboundHandler<Void> { 

  private Channel channel;

  public void sendRequest() {
    MetaTraderMessage tMsg = MetaTraderMessage.newBuilder().build();
    
    // Send request
    channel.writeAndFlush(tMsg);
      
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) {
      channel = ctx.channel();
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Void msg)
      throws Exception {
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
  }
}