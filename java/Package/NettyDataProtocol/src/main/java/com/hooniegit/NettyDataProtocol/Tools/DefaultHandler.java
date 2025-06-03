package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;

/**
 * Default Handler Class :: Handle Incoming Messages
 * @param <T>
 * @author hooniegit
 */
public class DefaultHandler<T> extends SimpleChannelInboundHandler<List<T>> {

    /**
     * Override Method :: Handle Incoming Messages
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<T> msg) {

    }

}
