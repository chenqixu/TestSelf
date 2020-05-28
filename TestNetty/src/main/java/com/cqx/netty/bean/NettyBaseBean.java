package com.cqx.netty.bean;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * NettyBaseBean
 *
 * @author chenqixu
 */
public class NettyBaseBean {
    private int head;
    private byte[] body;

    public NettyBaseBean() {
    }

    /**
     * 从ByteBuf反序列化为Bean
     *
     * @param buf
     */
    public NettyBaseBean(ByteBuf buf) {
        if (buf != null) {
            head = buf.readInt();
            body = new byte[buf.readableBytes()];
            buf.readBytes(body);
        }
    }

    /**
     * Bean序列化成ByteBuf
     *
     * @return
     */
    public ByteBuf serialize() {
        ByteBuf buf = Unpooled.buffer(2);
        buf.writeInt(head);
        buf.writeBytes(body);
        return buf;
    }

    public int getHead() {
        return head;
    }

    public NettyBaseBean setHead(int head) {
        this.head = head;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyString() {
        return new String(body);
    }

    public <T> T getBodyObject(Class<T> cls) {
        return JSON.parseObject(getBodyString(), cls);
    }

    public NettyBaseBean setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public NettyBaseBean setBody(String msg) {
        setBody(msg.getBytes());
        return this;
    }

    public NettyBaseBean setBody(Object object) {
        setBody(JSON.toJSONString(object).getBytes());
        return this;
    }
}
