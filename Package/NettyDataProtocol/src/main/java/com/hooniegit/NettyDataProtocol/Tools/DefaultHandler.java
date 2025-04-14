package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class DefaultHandler<T> extends SimpleChannelInboundHandler<List<T>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<T> msg) {

    }
}
