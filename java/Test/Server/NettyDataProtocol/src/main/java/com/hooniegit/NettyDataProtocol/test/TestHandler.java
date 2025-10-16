package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Tools.DefaultHandler;
import com.hooniegit.SourceData.Interface.TagData;
import com.hooniegit.Xtream.Tools.StreamManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class TestHandler extends DefaultHandler<List<TagData<Double>>> {

    private StreamManager<List<TagData<Double>>> MANAGER;

    public TestHandler(StreamManager<List<TagData<Double>>> MANAGER) {
        this.MANAGER = MANAGER;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<TagData<Double>> msg) {
//        this.MANAGER.getNextStream().publishInitialEvent(msg);
    }

}
