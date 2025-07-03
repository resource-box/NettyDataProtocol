package com.hooniegit.NettyDataProtocol.Client;

import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;

/**
 * Netty Client Class :: Send Java Object Data
 * @param <T>
 */
public class NettyClient<T> {

    public final int INDEX;

    private final String HOST;
    private final int PORT;

    private Channel CHANNEL;
    private final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    public boolean IS_INITIALIZED = false;

    public NettyClient(int INDEX, String HOST, int PORT) {
        this.INDEX = INDEX;
        this.HOST = HOST;
        this.PORT = PORT;
    }

    /**
     * Initialize Channel
     * @throws Exception
     */
    public void initialize() throws Exception {
        try {
            Channel NEW = new Bootstrap()
                    .group(this.GROUP)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new Encoder<List<T>>(),
                                    new Decoder(),
                                    new ChannelInboundHandlerAdapter()
                            );
                        }
                    }).connect(HOST, PORT).sync().channel();

            this.CHANNEL = NEW;
            this.IS_INITIALIZED = true;
        } catch (Exception e) {
            this.IS_INITIALIZED = false;
            throw new Exception("Failed to initialize Netty client: " + e.getMessage());
        }
    }

    /**
     * Send Java Object Data
     * @param data
     * @throws Exception
     */
    public void send(List<T> data) throws Exception {
        if (!this.IS_INITIALIZED) {
            throw new Exception("Netty Client is Not Initialized");
        }
        try {
            this.CHANNEL.writeAndFlush(data);
        } catch (Exception e) {
            this.IS_INITIALIZED = false;
            throw new Exception("Failed to send data: " + e);
        }
    }

    /**
     * Shutdown Netty Client
     */
    public void shutdown() {
        if (this.CHANNEL.isOpen()) {
            this.CHANNEL.close();
        }
        this.GROUP.shutdownGracefully();
    }

}
