package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Service;

@Service
public class Server {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel serverChannel;

    @PostConstruct
    public void start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new Decoder(),
                                new Encoder<Sample>(),
                                new ServerHandler<Sample>()
                        );
                    }
                });

        ChannelFuture future = bootstrap.bind(9999).sync();
        this.serverChannel = future.channel();
        System.out.println("Netty TCP Server Started on Port 9999");
    }

    @PreDestroy
    public void stop() throws Exception {
        if (serverChannel != null) {
            serverChannel.close().sync();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println("Netty TCP Server Stopped.");
    }

}

