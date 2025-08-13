package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Tools.DefaultHandler;
import com.hooniegit.SourceData.Interface.TagData;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class TestHandler extends DefaultHandler<TagData<String>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<TagData<String>> msg) {
        System.out.println("data received");
        for (TagData<String> data : msg) {
            System.out.println("Received data: " + data.getTimestamp());
        }
    }
}
