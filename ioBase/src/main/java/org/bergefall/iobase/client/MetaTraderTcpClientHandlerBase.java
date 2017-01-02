package org.bergefall.iobase.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class MetaTraderTcpClientHandlerBase extends SimpleChannelInboundHandler<MetaTraderMessage> { 

  private Channel channel;
  private MetaTraderMessage resp;
  BlockingQueue<MetaTraderMessage> resps = new LinkedBlockingQueue<>();
  
  public void sendAsyncReq(MetaTraderMessage msg) {
	  channel.writeAndFlush(msg);
  }
  
  public MetaTraderMessage sendRequestAwaitRsp(MetaTraderMessage req) {

    // Send request
    channel.writeAndFlush(req);
    // Now wait for response from server
    boolean interrupted = false;
    for (;;) {
        try {
            resp = resps.take();
            break;
        } catch (InterruptedException ignore) {
            interrupted = true;
        }
    }

    if (interrupted) {
        Thread.currentThread().interrupt();
    }
    
    return resp;
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) {
      channel = ctx.channel();
  }
  
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MetaTraderMessage msg)
      throws Exception {
	  resps.add(msg);
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
  }
}