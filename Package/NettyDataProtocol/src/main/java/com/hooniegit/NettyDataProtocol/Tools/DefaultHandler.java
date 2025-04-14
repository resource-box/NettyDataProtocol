package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * DefaultHandler Class for Netty Data Protocol (Need to Extend This And Define Custom Extended Handler)
 * @param <T>
 * @author hooniegit
 */
public class DefaultHandler<T> extends SimpleChannelInboundHandler<List<T>> {

    /**
     * Channel Read Method to Handle The Incoming Messages (Need to Override This Method)
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<T> msg) {

    }

}
