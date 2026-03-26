package com.hooniegit.NettyDataProtocol.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.flush.FlushConsolidationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TCP 데이터 전송 클라이언트입니다.
 */
public final class TcpKryoClient implements AutoCloseable {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(TcpKryoClient.class);

    // TCP Client 설정
    private final String host;
    private final int port;
    private final int maxFrameBytes;
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private volatile Channel channel;
    private final KryoPool kryoPool;

    // Status
    private final AtomicLong reconnectCount = new AtomicLong(0);
    private final AtomicLong lastConnectedAtMs = new AtomicLong(0);

    public TcpKryoClient(String host, int port, int maxFrameBytes, int ioThreads) {
        this.host = host;
        this.port = port;
        this.maxFrameBytes = maxFrameBytes;

        this.group = new NioEventLoopGroup(ioThreads);
        this.kryoPool = new KryoPool(Math.max(16, ioThreads * 4), 64 * 1024, maxFrameBytes);

        this.bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, io.netty.buffer.PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(8 * 1024 * 1024, 32 * 1024 * 1024))
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast("frameDecoder",
                                new LengthFieldBasedFrameDecoder(maxFrameBytes, 0, 4, 0, 4));
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ch.pipeline().addLast("flushConsolidation", new FlushConsolidationHandler(256, true));
                        ch.pipeline().addLast("kryoDecoder", new KryoDecoder(kryoPool));
                        ch.pipeline().addLast("kryoEncoder", new KryoEncoder(kryoPool));
                        ch.pipeline().addLast("inbound", new ClientInboundHandler(TcpKryoClient.this));
                    }
                });
    }

    /**
     * 서버와의 연결을 시도합니다. 연결이 실패할 경우 자동으로 재시도하며, 재시도 간격은 단순 백오프 방식으로 증가합니다.
     */
    public void connect() {
        doConnect(0);
    }

    private void doConnect(long delayMs) {
        group.schedule(() -> {
            log.info("[CLIENT] connecting to {}:{}", host, port);

            bootstrap.connect(host, port).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    channel = f.channel();
                    lastConnectedAtMs.set(System.currentTimeMillis());
                    log.info("[CLIENT] connected channel={}", channel.id());
                } else {
                    long rc = reconnectCount.incrementAndGet();
                    log.warn("[CLIENT] connect failed rc={}, cause={}", rc, f.cause().toString());
                    doConnect(Math.min(5000, 200 + rc * 50)); // 단순 백오프
                }
            });
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 서버와의 연결이 끊긴 채널을 닫고, 재연결을 시도합니다. 이 메서드는 ClientInboundHandler 객체에서 채널이 비활성화되거나 예외가 발생할 때 호출됩니다.
     * @param ch
     */
    void onDisconnected(Channel ch) {
        // 요구사항: 끊기면 완전 종료 -> close() 보장
        try { ch.close(); } catch (Exception ignored) {}
        channel = null;

        long rc = reconnectCount.incrementAndGet();
        log.info("[CLIENT] disconnected -> reconnect rc={}", rc);
        doConnect(Math.min(5000, 200 + rc * 50));
    }

    public boolean isConnected() {
        Channel ch = channel;
        return ch != null && ch.isActive();
    }

    public long getReconnectCount() { return reconnectCount.get(); }
    public long getLastConnectedAtMs() { return lastConnectedAtMs.get(); }

    // 핵심 전송 API
    public ChannelFuture send(Object msg) {
        Channel ch = channel;
        if (ch == null || !ch.isActive()) {
            return null; // 또는 실패 Future 반환
        }
        return ch.write(msg); // flush는 별도 정책으로 합치기
    }

    public void flush() {
        Channel ch = channel;
        if (ch != null && ch.isActive()) ch.flush();
    }

    @Override
    public void close() {
        Channel ch = channel;
        if (ch != null) ch.close();
        group.shutdownGracefully();
    }
}