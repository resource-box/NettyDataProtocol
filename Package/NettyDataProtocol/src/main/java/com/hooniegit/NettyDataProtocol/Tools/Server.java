package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.function.Supplier;

/**
 * Server Class for Netty Data Protocol
 * @param <T>
 * @author hooniegit
 */
public class Server<T extends Object> {

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel serverChannel;

    private final int port;
    private final Supplier<DefaultHandler<T>> handlerSupplier;

    /**
     * Constructor for Server
     * @param port
     * @param bossGroupThreads
     * @param workerGroupThreads
     * @param handlerSupplier
     */
    public Server(int port, int bossGroupThreads, int workerGroupThreads, Supplier<DefaultHandler<T>> handlerSupplier) {
        this.bossGroup = new NioEventLoopGroup(bossGroupThreads);
        this.workerGroup = new NioEventLoopGroup(workerGroupThreads);

        this.port = port;
        this.handlerSupplier = handlerSupplier;
    }

    /**
     * PostConstruct Method to Start The Server
     * @throws Exception
     */
    public void start() throws Exception {
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

        ChannelFuture future = bootstrap.bind(port).sync();
        this.serverChannel = future.channel();
    }

    /**
     * PreDestroy Method to Stop The Server (Graceful Shutdown)
     * @throws Exception
     */
    public void stop() throws Exception {
        if (serverChannel != null) {
            serverChannel.close().sync();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}