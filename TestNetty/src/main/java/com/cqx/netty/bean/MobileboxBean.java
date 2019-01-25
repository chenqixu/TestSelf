package com.cqx.netty.bean;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 移动魔百盒
 * <pre>
 *     客户端查询：
 *     头部：int：1
 *     消息：bytes：查询条件
 *
 *     服务端返回查询结果
 *     头部：int：2
 *     消息：bytes：查询结果
 * </pre>
 *
 * @author chenqixu
 */
public class MobileboxBean {
    private int head;
    private byte[] body;

    private MobileboxBean() {
    }

    /**
     * 从ByteBuf反序列化为Bean
     *
     * @param buf
     */
    public MobileboxBean(ByteBuf buf) {
        if (buf != null) {
            head = buf.readInt();
            body = new byte[buf.readableBytes()];
            buf.readBytes(body);
        }
    }

    public static MobileboxBean newbuilder() {
        return new MobileboxBean();
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

    public MobileboxBean setHead(int head) {
        this.head = head;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public MobileboxBean setBody(byte[] body) {
        this.body = body;
        return this;
    }
}
