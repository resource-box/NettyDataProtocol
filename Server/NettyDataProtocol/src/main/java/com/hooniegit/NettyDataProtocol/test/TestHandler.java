package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Tools.DefaultHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class TestHandler<T> extends DefaultHandler<T> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<T> msg) {

    }
}
