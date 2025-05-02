package com.hooniegit.NettyDataProtocol.Protobuf;

import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @param <T>
 */
public class NettyObjectClient<T> {

    private final String HOST;
    private final int PORT;
    private final int CHANNEL_COUNT;

    private final List<Channel> channels = new ArrayList<>();
    private final NioEventLoopGroup group = new NioEventLoopGroup();

    private final AtomicInteger INDEX = new AtomicInteger(0);
    private boolean IS_INITIALIZED = false;

    public NettyObjectClient(String HOST, int PORT, int CHANNEL_COUNT) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.CHANNEL_COUNT = CHANNEL_COUNT;
    }

    public void initialize() throws InterruptedException {
        if (IS_INITIALIZED) {
            return;
        }

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new Encoder<T>(),
                                new Decoder(),
                                new ChannelInboundHandlerAdapter()
                        );
                    }
                });


        for (int i = 0; i < CHANNEL_COUNT; i++) {
            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();
            channels.add(channel);
        }

        IS_INITIALIZED = true;
    }

    public void send(T data) {
        int index = this.INDEX.getAndUpdate(i -> (i + 1) % CHANNEL_COUNT);
        Channel channel = this.channels.get(index);

        channel.writeAndFlush(data);
    }

    public void shutdown() {
        for (Channel channel : channels) {
            if (channel.isOpen()) {
                channel.close();
            }
        }
        group.shutdownGracefully();
        System.out.println("Netty clients shut down.");
    }
}
