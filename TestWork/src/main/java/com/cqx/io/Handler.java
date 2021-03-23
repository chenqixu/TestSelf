package com.cqx.io;

/**
 * Handler
 *
 * @author chenqixu
 */
public interface Handler {
    void channelRead(HandlerContext ctx, Object msg);
}
