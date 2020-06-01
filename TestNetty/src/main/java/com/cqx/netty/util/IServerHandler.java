package com.cqx.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 公共服务处理接口
 *
 * @author chenqixu
 */
public abstract class IServerHandler extends ChannelHandlerAdapter {

    protected static Logger logger = LoggerFactory.getLogger(IServerHandler.class);
//    private Map<String, String> params;

    public IServerHandler() {
        init();
    }

//    protected String getParams(String key) {
//        return this.params.get(key);
//    }

    protected abstract void init();

    protected abstract ByteBuf dealHandler(ByteBuf buf);

    /**
     * 字符串转ByteBuf
     *
     * @param msg
     * @return
     */
    public ByteBuf strToByteBuf(String msg) {
        return Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
    }

    /**
     * 这里我们覆盖了chanelRead()事件处理方法。 每当从客户端收到新的数据时，这个方法会在收到消息时被调用，
     * 这个例子中，收到的消息的类型是ByteBuf
     *
     * @param ctx 通道处理的上下文信息
     * @param msg 接收的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        logger.info("Client received:" + buf);
        /**
         * 解析包类型，分别处理
         */
        ByteBuf result = dealHandler(buf);
        if (result != null) {
            logger.info("返回结果给客户端:" + result);
            // 返回结果给客户端
            ctx.write(result);
        } else {
            logger.info("抛弃收到的数据");
            // 抛弃收到的数据
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 客户端消息读取完成之后的操作
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channelReadComplete..");
        // 第一种方法：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        //ctx.flush(); // 第二种方法：在client端关闭channel连接，这样的话，会触发两次channelReadComplete方法。
        //ctx.flush().close().sync(); // 第三种：改成这种写法也可以，但是这种写法，没有第一种方法的好。
    }

    /***
     * 这个方法会在发生异常时触发
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，即当 Netty 由于 IO
         * 错误或者处理器在处理事件时抛出的异常时。在大部分情况下，捕获的异常应该被记录下来 并且把关联的 channel
         * 给关闭掉。然而这个方法的处理方式会在遇到不同异常的情况下有不 同的实现，比如你可能想在关闭连接之前发送一个错误码的响应消息。
         */
        logger.info("server occur exception:" + cause.getMessage());
        // 出现异常就关闭
        cause.printStackTrace();
        ctx.close();
    }

//    public void setParams(Map<String, String> params) {
//        this.params = params;
//    }
}
