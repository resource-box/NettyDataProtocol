package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;

/**
 * 기본 핸들러 클래스입니다. 해당 클래스를 상속받아 커스텀 핸들러를 정의해야 합니다.
 * @param <T>
 */
@ChannelHandler.Sharable
public class DefaultHandler<T> extends SimpleChannelInboundHandler<T> {

    /**
     * 오버라이드용 메서드입니다. 수신 데이터를 처리하기 위해 해당 메서드를 구현해야 합니다.
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) {

    }

}
