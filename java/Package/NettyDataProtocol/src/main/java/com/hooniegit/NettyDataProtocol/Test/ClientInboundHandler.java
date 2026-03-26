package com.hooniegit.NettyDataProtocol.Test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP 클라이언트 : 서버의 채널이 비활성화되거나 예외가 발생했을 때의 처리를 담당하는 핸들러입니다.
 */
public final class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(ClientInboundHandler.class);

    // TCP Client
    private final TcpKryoClient client;

    public ClientInboundHandler(TcpKryoClient client) { this.client = client; }

    /**
     * 채널 비활성화 시에 onDisconnected() 메서드를 호출하여 연결이 끊어진 채널을 종료하고 재연결을 시도합니다.
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("[CLIENT] channelInactive");
        client.onDisconnected(ctx.channel());
    }

    /**
     * 예외 발생 시에 onDisconnected() 메서드를 호출하여 연결이 끊어진 채널을 종료하고 재연결을 시도합니다.
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("[CLIENT] exception", cause);
        client.onDisconnected(ctx.channel());
    }

}