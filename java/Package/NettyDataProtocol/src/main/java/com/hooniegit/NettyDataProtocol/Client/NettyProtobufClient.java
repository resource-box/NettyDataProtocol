package com.hooniegit.NettyDataProtocol.Client;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty Client Class :: Send Google Protobuf Data
 * @param <T>
 */
public class NettyProtobufClient<T extends GeneratedMessageV3> {

    private final String HOST;
    private final int PORT;
    private final int CHANNEL_COUNT;

    private final List<Channel> channels = new ArrayList<>();
    private final NioEventLoopGroup group = new NioEventLoopGroup();

    private final AtomicInteger INDEX = new AtomicInteger(0);
    private boolean IS_INITIALIZED = false;

    public NettyProtobufClient(String HOST, int PORT, int CHANNEL_COUNT) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.CHANNEL_COUNT = CHANNEL_COUNT;
    }

    /**
     * Initialize Channels
     * @throws Exception
     */
    public void initialize() throws Exception {
        if (this.IS_INITIALIZED) return;

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });

            for (int i = 0; i < CHANNEL_COUNT; i++) {
                Channel channel = bootstrap.connect(HOST, PORT).sync().channel();
                channels.add(channel);
            }

            IS_INITIALIZED = true;
        } catch (Exception e) {
            this.IS_INITIALIZED = false;
            throw new Exception("Failed to initialize Netty client: " + e);
        }

    }

    /**
     * Send Google Protobuf Data
     * @param data
     * @throws Exception
     */
    public void send(T data) throws Exception {
        if (!this.IS_INITIALIZED) initialize();

        int index = this.INDEX.getAndUpdate(i -> (i + 1) % CHANNEL_COUNT);
        Channel channel = this.channels.get(index);

        byte[] bytes = data.toByteArray();
        ByteBuf buf = Unpooled.buffer(4 + bytes.length);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);

        try {
            channel.writeAndFlush(buf.retainedDuplicate());
        } catch (Exception e) {
            this.IS_INITIALIZED = false;
            throw new Exception("Failed to send data: " + e);
        }
    }

    /**
     * Shutdown Netty Client
     */
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
