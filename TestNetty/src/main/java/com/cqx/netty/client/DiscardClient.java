package com.cqx.netty.client;

import java.net.InetSocketAddress;

import com.cqx.netty.bean.ClientQueryBean;
import com.cqx.netty.bean.DiscardBean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DiscardClient {
	private String host;
	private int port;

	public DiscardClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void heart(final DiscardBean discardBean) throws Exception {
		/**
		 * 如果使用空构造，这里就会启动cpu核数两倍的线程
		 * Create a new instance that uses twice as many {@link EventLoop}s as there processors/cores available
		 */
		EventLoopGroup group = new NioEventLoopGroup(1);
		try {
			Bootstrap b = new Bootstrap();
			b.group(group) // 注册线程池
				.channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
				.remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
				.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						System.out.println("connected...");
						ch.pipeline().addLast(new DiscardClientHandler(discardBean));
					}
				});
			System.out.println("created..");
			
			ChannelFuture cf = b.connect().sync(); // 异步连接服务器
			System.out.println("connected..."); // 连接完成
			
			cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
			System.out.println("closed.."); // 关闭完成
		} finally {
			group.shutdownGracefully().sync(); // 释放线程池资源
		}
	}
	
	public void query(final DiscardBean discardBean, final ClientQueryBean resultQueue) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group) // 注册线程池
				.channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
				.remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
				.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						System.out.println("connected...");
						ch.pipeline().addLast(new DiscardClientHandler(discardBean, resultQueue));
					}
				});
			System.out.println("created..");
			
			ChannelFuture cf = b.connect().sync(); // 异步连接服务器
			System.out.println("connected..."); // 连接完成
			
			cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
			System.out.println("closed.."); // 关闭完成
		} finally {
			group.shutdownGracefully().sync(); // 释放线程池资源
		}
	}
	
	public static void main(String[] args) throws Exception {
//		DiscardBean discardBean = new DiscardBean(1, 1234567l, DiscardBean.buildDataBean("testThread1", 1, Utils.getNow()));
//		new DiscardClient("192.168.230.128", 18888).heart(discardBean);
	}
}
