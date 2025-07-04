package com.hooniegit.NettyDataProtocol.Server;

import com.hooniegit.NettyDataProtocol.Exception.NettyServerStartFailedException;
import com.hooniegit.NettyDataProtocol.Exception.NettyServerStopFailedException;
import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.DefaultHandler;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * 통신 서버 클래스입니다. 클라이언트 연결을 관리하고 데이터를 수신합니다.
 * @param <T>
 */
public class ObjectServer<T extends Object> {

    // 생성자 속성
    private final int PORT;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel serverChannel;

    // 상태 속성
    private final Supplier<DefaultHandler<T>> handlerSupplier;

    public ObjectServer(int PORT, int bossGroupThreads, int workerGroupThreads, Supplier<DefaultHandler<T>> handlerSupplier) {
        this.PORT = PORT;
        this.bossGroup = new NioEventLoopGroup(bossGroupThreads);
        this.workerGroup = new NioEventLoopGroup(workerGroupThreads);
        this.handlerSupplier = handlerSupplier;
    }

    /**
     * 서버를 시작합니다.
     * @throws NettyServerStartFailedException 서버 시작 실패 예외
     */
    public void start() throws NettyServerStartFailedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new Decoder(),
                                    new Encoder<T>(),
                                    handlerSupplier.get()
                            );
                        }
                    });

            ChannelFuture future = bootstrap.bind(PORT).sync();
            this.serverChannel = future.channel();
        } catch (Exception e) {
            throw new NettyServerStartFailedException(e.toString());
        }

    }

    /**
     * 채널과 그룹을 종료합니다.
     * @throws NettyServerStopFailedException 서버 중지 실패 예외
     */
    public void stop() throws NettyServerStopFailedException {
        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception e) {
            throw new NettyServerStopFailedException(e.toString());
        }
    }

}