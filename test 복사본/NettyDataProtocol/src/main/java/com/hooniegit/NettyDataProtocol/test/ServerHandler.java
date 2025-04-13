package com.hooniegit.NettyDataProtocol.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.LocalDateTime;
import java.util.List;

public class ServerHandler<T> extends SimpleChannelInboundHandler<List<T>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<T> msg) {
        System.out.println( "[" + LocalDateTime.now() + "] Received Size: " + msg.size());
//        for (T sample : msg) {
//            System.out.println("  ↪ " + sample);
//        }
    }
}
