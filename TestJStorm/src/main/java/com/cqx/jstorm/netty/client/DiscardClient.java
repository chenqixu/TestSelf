package com.cqx.jstorm.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqx.jstorm.netty.bean.DiscardBean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DiscardClient {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscardClient.class);
	private String host;
	private int port;

	public DiscardClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void heart(final DiscardBean discardBean) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group) // 注册线程池
				.channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
				.remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
				.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						logger.info("connected...");
						ch.pipeline().addLast(new DiscardClientHandler(discardBean));
					}
				});
			logger.info("created..");
			
			ChannelFuture cf = b.connect().sync(); // 异步连接服务器
			logger.info("connected..."); // 连接完成
			
			cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
			logger.info("closed.."); // 关闭完成
		} finally {
			group.shutdownGracefully().sync(); // 释放线程池资源
		}
	}
	
	public void query(final DiscardBean discardBean, final BlockingQueue<String> resultQueue) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group) // 注册线程池
				.channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
				.remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
				.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						logger.info("connected...");
						ch.pipeline().addLast(new DiscardClientHandler(discardBean, resultQueue));
					}
				});
			logger.info("created..");
			
			ChannelFuture cf = b.connect().sync(); // 异步连接服务器
			logger.info("connected..."); // 连接完成
			
			cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
			logger.info("closed.."); // 关闭完成
		} finally {
			group.shutdownGracefully().sync(); // 释放线程池资源
		}
	}
}
