package com.cqx.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 公共客户端处理接口
 *
 * @author chenqixu
 */
public abstract class IClientHandler<T> extends SimpleChannelInboundHandler<ByteBuf> {

    private static Logger logger = LoggerFactory.getLogger(IClientHandler.class);
    private Map<String, String> params;
    private CountDownLatch latch;
    private T t;

    private ByteBuf defaultRequest() {
        return Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8);
    }

    protected ByteBuf sendRequest() {
        return defaultRequest();
    }

    protected abstract void dealResponse(ByteBuf buf);

    protected String getParams(String key) {
        return this.params.get(key);
    }

    public void resetSync(CountDownLatch latch) {
        this.latch = latch;
    }

    public void releaseSync() {
        latch.countDown();
    }

    public T getResult() {
        return t;
    }

    public void setResult(T t) {
        this.t = t;
    }

    /**
     * 发送请求到服务器
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client channelActive..");
        ctx.writeAndFlush(sendRequest());
    }

    /**
     * 解析服务器返回的数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg)
            throws Exception {
//        logger.info("client channelRead..");
        ByteBuf buf = msg.readBytes(msg.readableBytes());
        logger.info("Client received:" + ByteBufUtil.hexDump(buf));
//        String result = buf.toString(Charset.forName("utf-8"));
//        logger.info("Client received:" + ByteBufUtil.hexDump(buf) + "; The value is:" + result);
//        this.queryResult.put(result);
        dealResponse(buf);
    }

    /**
     * 异常捕获处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
