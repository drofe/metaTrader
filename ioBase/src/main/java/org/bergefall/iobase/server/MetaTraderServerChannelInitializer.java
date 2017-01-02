package org.bergefall.iobase.server;

import org.bergefall.iobase.blp.BusinessLogicPipline;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class MetaTraderServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	private BusinessLogicPipline blp; 
	
	public MetaTraderServerChannelInitializer(BusinessLogicPipline blp) {
		this.blp = blp;
	}
	  @Override
	  protected void initChannel(SocketChannel ch) throws Exception {
	    ChannelPipeline p = ch.pipeline();
	    p.addLast(new ProtobufVarint32FrameDecoder());
	    p.addLast(new ProtobufDecoder(MetaTraderMessage.getDefaultInstance()));

	    p.addLast(new ProtobufVarint32LengthFieldPrepender());
	    p.addLast(new ProtobufEncoder());

	    p.addLast(new MetaTraderTcpServerHandlerBase(blp));
	  }


}
