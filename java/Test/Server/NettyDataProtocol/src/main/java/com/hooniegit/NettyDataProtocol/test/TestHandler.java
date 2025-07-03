package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Tools.DefaultHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class TestHandler extends DefaultHandler<Sample> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<Sample> msg) {
        System.out.println("data received");
        for (Sample data : msg) {
            System.out.println("Received data: " + data.toString());
        }
    }
}
