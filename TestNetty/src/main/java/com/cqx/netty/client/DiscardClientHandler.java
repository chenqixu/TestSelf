package com.cqx.netty.client;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

import com.cqx.netty.bean.ClientQueryBean;
import com.cqx.netty.bean.DiscardBean;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DiscardClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private DiscardBean discardBean;
	private ClientQueryBean queryResult;
	
	public DiscardClientHandler(DiscardBean discardBean) {
		this.discardBean = discardBean;
	}
	
	public DiscardClientHandler(DiscardBean discardBean, ClientQueryBean queryResult) {
		this.discardBean = discardBean;
		this.queryResult = queryResult;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("client channelActive..");
//		ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",
//				CharsetUtil.UTF_8));
		ctx.writeAndFlush(discardBean.getMsg());
		// ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
		// ctx.flush();
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		System.out.println("client channelRead..");
		ByteBuf buf = msg.readBytes(msg.readableBytes());
//		DiscardBean discardBean = new DiscardBean(msg);
//		System.out.println("Client received:" + ByteBufUtil.hexDump(msg) + "; The value is:" + discardBean.toString());
		String result = buf.toString(Charset.forName("utf-8"));
		System.out.println("Client received:" + ByteBufUtil.hexDump(buf) + "; The value is:" + result);
		this.queryResult.put(result);
//		ReferenceCountUtil.release(msg);
		//ctx.channel().close().sync();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
