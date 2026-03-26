package com.hooniegit.NettyDataProtocol.Test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * TCP 데이터 수신 서버입니다.
 */
public final class TcpKryoServer implements AutoCloseable {

    // TCP Server 설정
    private final int port;
    private final int maxFrameBytes;
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup worker;
    private Channel serverChannel;
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Supplier<BatchInboundHandler> handlerSupplier;
    private final KryoPool kryoPool;

    // Status
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    /**
     * TCP 서버 생성자입니다.
     * @param port 서버 포트 설정
     * @param maxFrameBytes 최대 프레임 크기 설정 (바이트 단위)
     * @param workerThreads 워커 스레드 수 설정 (I/O 처리에 사용)
     * @param handlerSupplier 비즈니스 로직을 처리할 BatchInboundHandler 공급자 (BatchInboundHandler::new 형태로 전달)
     */
    public TcpKryoServer(int port, int maxFrameBytes, int workerThreads, Supplier<BatchInboundHandler> handlerSupplier  ) {
        this.port = port;
        this.maxFrameBytes = maxFrameBytes;
        this.worker = new NioEventLoopGroup(workerThreads);
        this.handlerSupplier = handlerSupplier;
        this.kryoPool = new KryoPool(Math.max(16, workerThreads * 4), 64 * 1024, maxFrameBytes);
    }

    /**
     * TCP 서버를 시작합니다.
     * 서버가 바인딩되면 클라이언트 수락이 가능한 상태가 됩니다.
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        this.handlerSupplier.get();
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                // 서버 소켓 옵션 설정
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 클라이언트 소켓 옵션 설정
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // Netty의 PooledByteBufAllocator를 사용하여 버퍼 할당 최적화
                .childOption(ChannelOption.ALLOCATOR, io.netty.buffer.PooledByteBufAllocator.DEFAULT)
                // 쓰기 버퍼 워터마크 설정: low=8MB, high=32MB (적절히 조정 가능)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(8 * 1024 * 1024, 32 * 1024 * 1024))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        channels.add(ch);
                        // 연결 상태 핸들러 추가: activeConnections 카운터를 관리
                        ch.pipeline().addLast("connStatus", new ConnectionStatusHandler(activeConnections));
                        // 프레임 디코더 및 인코더 추가: 메시지 길이 기반 프레임 처리
                        ch.pipeline().addLast("frameDecoder",
                                new LengthFieldBasedFrameDecoder(maxFrameBytes, 0, 4, 0, 4));
                        // 프레임 인코더: 메시지 전송 시 길이 필드 추가
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        // Flush Consolidation 핸들러 추가: 작은 쓰기 작업들을 모아서 한 번에 플러시하여 성능 향상
                        ch.pipeline().addLast("flushConsolidation", new FlushConsolidationHandler(256, true));
                        // Kryo 디코더 및 인코더 추가: 객체 직렬화/역직렬화 처리
                        ch.pipeline().addLast("kryoDecoder", new KryoDecoder(kryoPool));
                        // Kryo 인코더 추가: 객체를 바이트로 직렬화하여 전송
                        ch.pipeline().addLast("kryoEncoder", new KryoEncoder(kryoPool));
                        // 비즈니스 로직 핸들러 추가: 클라이언트로부터 수신된 메시지를 처리
                        ch.pipeline().addLast("business", handlerSupplier.get());
                    }
                });

        this.serverChannel = b.bind(port).sync().channel();
    }

    /**
     * 현재 활성화된 클라이언트 연결 수를 반환합니다.
     * @return 활성 연결 수
     */
    public int getActiveConnections() { return activeConnections.get(); }

    /**
     * 현재 서버에 연결된 모든 채널의 수를 반환합니다.
     * (활성 연결과는 별개로, 연결된 채널의 총 수를 나타냅니다)
     * @return 연결된 총 채널 수
     */
    public int getTotalChannels() { return channels.size(); }

    /**
     * TCP 서버를 종료합니다.
     * 모든 채널을 닫고, 이벤트 루프 그룹을 종료하여 리소스를 해제합니다.
     */
    @Override
    public void close() {
        if (serverChannel != null) serverChannel.close();
        channels.close();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}