package com.cqx.distributed.net;

import com.cqx.netty.bean.NettyBaseBean;
import com.cqx.netty.util.IServer;
import com.cqx.netty.util.IServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * 内部服务
 *
 * @author chenqixu
 */
public class InternalServer {
    private IServer iServer;

    public InternalServer() {
        iServer = IServer.newbuilder();
    }

    public void start(int port) throws Exception {
        Map<String, String> params = new HashMap<>();
        iServer.setPort(port)
                .setParams(params)
                .buildBootstrap()
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("RegisterServerHandler", new RegisterServerHandler());
                    }
                });
        iServer.start();
    }

    public void close() {
    }

    class RegisterServerHandler extends IServerHandler {

        @Override
        protected ByteBuf dealHandler(ByteBuf buf) {
            NettyBaseBean responseBean = new NettyBaseBean();
            NettyBaseBean requestBean = new NettyBaseBean(buf);
            int head = requestBean.getHead();
            String body = requestBean.getBodyString();
            switch (ServerCodeEnum.valueOf(head)) {
                case Register://资源服务注册
                    //注册成功
                    responseBean.setHead(ServerCodeEnum.Success.getServerCode());
                    responseBean.setBody("资源服务注册成功");
                    break;
                default:
                    break;
            }
            return responseBean.serialize();
        }
    }
}
