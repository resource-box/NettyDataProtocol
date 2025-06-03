package com.hooniegit.NettyDataProtocol.Server;

import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.DefaultHandler;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.function.Supplier;

/**
 * Netty Server Class :: Receive Java Object Data
 * @param <T>
 */
public class ObjectServer<T extends Object> {

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel serverChannel;

    private final int port;
    private final Supplier<DefaultHandler<T>> handlerSupplier;

    public ObjectServer(int port, int bossGroupThreads, int workerGroupThreads, Supplier<DefaultHandler<T>> handlerSupplier) {
        this.bossGroup = new NioEventLoopGroup(bossGroupThreads);
        this.workerGroup = new NioEventLoopGroup(workerGroupThreads);

        this.port = port;
        this.handlerSupplier = handlerSupplier;
    }

    /**
     * PostConstruct Method :: Start Server
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
     * PreDestroy Method :: Stop Server
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