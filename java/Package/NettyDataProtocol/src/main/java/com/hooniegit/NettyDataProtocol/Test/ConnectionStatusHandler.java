package com.hooniegit.NettyDataProtocol.Test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TCP 서버 : 클라이언트와의 연결 상태를 감지하는 핸들러입니다.
 */
public final class ConnectionStatusHandler extends ChannelInboundHandlerAdapter {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(ConnectionStatusHandler.class);

    // Status
    private final AtomicInteger active;

    public ConnectionStatusHandler(AtomicInteger active) { this.active = active; }

    /**
     * 채널이 활성화될 때마다 activeConnections 카운터를 증가시킵니다.
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        int now = active.incrementAndGet();
        log.info("[SERVER] channelActive remote={} activeConnections={}",
                ctx.channel().remoteAddress(), now);
        ctx.fireChannelActive();
    }

    /**
     * 채널이 비활성화될 때마다 activeConnections 카운터를 감소시킵니다.
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        int now = active.decrementAndGet();
        log.info("[SERVER] channelInactive remote={} activeConnections={}",
                ctx.channel().remoteAddress(), now);
        ctx.fireChannelInactive();
    }

    /**
     * 예외 발생 시에 로그를 남기고 연결을 종료합니다.
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("[SERVER] exception remote={}", ctx.channel().remoteAddress(), cause);
        ctx.close(); // (선택) 예외 발생 시 안전을 위해 연결을 종료합니다.
    }
}
