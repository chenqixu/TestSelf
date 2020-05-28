package com.cqx.netty.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 公共客户端接口
 *
 * @author chenqixu
 */
public class IClient<T> {

    private static Logger logger = LoggerFactory.getLogger(IClient.class);
    private String host;
    private int port;
    private IClientHandler<T> iClientHandler;

    private IClient() {
    }

    public static IClient newbuilder() {
        return new IClient();
    }

    private CountDownLatch query(Map<String, String> params, boolean isBlocking) throws Exception {
        if (!check()) throw new Exception("运行参数不满足！具体参数：" + getRunParams());
        CountDownLatch latch = null;
        if (isBlocking) {//是否同步
            /**
             * 设置同步
             */
            latch = new CountDownLatch(1);
            iClientHandler.resetSync(latch);
        }
        /**
         * 设置参数
         */
        iClientHandler.setParams(params);
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
                            logger.info("connected...");
                            ch.pipeline().addLast(iClientHandler);
                        }
                    });
            logger.info(this + " created..");

            ChannelFuture cf = b.connect().sync(); // 异步连接服务器
            logger.info(this + " connected..."); // 连接完成

            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
            logger.info(this + " closed.."); // 关闭完成
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }
        if (isBlocking) {//是否同步
            return latch;
        } else {
            return null;
        }
    }

    public void queryNoBlocking(Map<String, String> params) throws Exception {
        query(params, false);
    }

    public T queryBlocking(Map<String, String> params) throws Exception {
        CountDownLatch latch = query(params, true);
        if (latch != null) {
            /**
             * 同步返回结果，最多允许10秒超时
             */
            if (latch.await(10, TimeUnit.SECONDS)) {
                return iClientHandler.getResult();
            } else {
                /**
                 * 超时
                 */
                return null;
            }
        }
        return null;
    }

    private boolean check() {
        if (port > 0 && host != null && iClientHandler != null)
            return true;
        return false;
    }

    public IClient setHost(String host) {
        this.host = host;
        return this;
    }

    public IClient setPort(int port) {
        this.port = port;
        return this;
    }

    public IClient setiClientHandler(IClientHandler iClientHandler) {
        this.iClientHandler = iClientHandler;
        return this;
    }

    private String getRunParams() {
        return "[port：" + port + " [host：" + host + " [iClientHandler：" + iClientHandler;
    }
}
