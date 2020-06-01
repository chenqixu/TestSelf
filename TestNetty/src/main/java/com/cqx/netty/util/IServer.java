package com.cqx.netty.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 公共服务接口
 *
 * @author chenqixu
 */
public class IServer {

    private static final Logger logger = LoggerFactory.getLogger(IServer.class);
    private int port = 0;
    private Map<String, String> params;
    private IServerHandler iServerHandler;
    private Class cls;
    private EventLoopGroup group;

    private IServer() {
    }

    public static IServer newbuilder() {
        return new IServer();
    }

    public void start() throws Exception {
        if (!check()) throw new Exception("运行参数不满足！具体参数：" + getRunParams());
        /**
         * 如果使用空构造，这里就会启动cpu核数两倍的线程
         * Create a new instance that uses twice as many {@link EventLoop}s as there processors/cores available
         */
        group = new NioEventLoopGroup(1);
//        try {
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(group) // 绑定线程池
                .channel(NioServerSocketChannel.class) // 指定使用的channel
                .localAddress(this.port)// 绑定监听端口
                .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        logger.info("connected...; Client:" + ch.remoteAddress());
//                            ch.pipeline().addLast(Utils.genrate(cls, params)); // 客户端触发操作
                        ch.pipeline().addLast(iServerHandler); // 客户端触发操作
                    }
                });
        ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
        logger.info(this + " started and listen on " + cf.channel().localAddress());
        cf.channel().closeFuture(); // 关闭服务器通道
//            cf.channel().closeFuture().sync(); // 关闭服务器通道
//        } finally {
//            group.shutdownGracefully().sync(); // 释放线程池资源
//        }
    }

    public void close() {
        if (group != null) {
            try {
                group.shutdownGracefully().sync(); // 释放线程池资源，加上sync会阻塞
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private boolean check() {
//        if (port > 0 && cls != null && params != null)
        if (port > 0)
            return true;
        return false;
    }

    public IServer setPort(int port) {
        this.port = port;
        return this;
    }

    public IServer setCls(Class cls) {
        this.cls = cls;
        return this;
    }

    public IServer setiServerHandler(IServerHandler iServerHandler) {
        this.iServerHandler = iServerHandler;
        return this;
    }

    public IServer setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    private String getRunParams() {
        return "[port：" + port + " [cls：" + cls + " [params：" + params;
    }
}
