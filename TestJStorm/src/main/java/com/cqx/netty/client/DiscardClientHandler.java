package com.cqx.netty.client;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqx.netty.bean.DiscardBean;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DiscardClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger logger = LoggerFactory.getLogger(DiscardClientHandler.class);
	private DiscardBean discardBean;
	private BlockingQueue<String> queryResult;
	
	public DiscardClientHandler(DiscardBean discardBean) {
		this.discardBean = discardBean;
	}
	
	public DiscardClientHandler(DiscardBean discardBean, BlockingQueue<String> queryResult) {
		this.discardBean = discardBean;
		this.queryResult = queryResult;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client channelActive..");
//		ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",
//				CharsetUtil.UTF_8)); // 必须有flush
		ctx.writeAndFlush(discardBean.getMsg());
		// 必须存在flush
		// ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
		// ctx.flush();
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		logger.info("client channelRead..");
		ByteBuf buf = msg.readBytes(msg.readableBytes());
//		DiscardBean discardBean = new DiscardBean(msg);
//		logger.info("Client received:" + ByteBufUtil.hexDump(msg) + "; The value is:" + discardBean.toString());
		String result = buf.toString(Charset.forName("utf-8"));
		logger.info("Client received:" + ByteBufUtil.hexDump(buf) + "; The value is:" + result);
		this.queryResult.put(result);
//		// 抛弃收到的数据
//		ReferenceCountUtil.release(msg);
		//ctx.channel().close().sync();// client关闭channel连接
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
