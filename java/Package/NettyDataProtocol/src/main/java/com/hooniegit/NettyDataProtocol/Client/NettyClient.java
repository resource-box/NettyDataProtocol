package com.hooniegit.NettyDataProtocol.Client;

import com.hooniegit.NettyDataProtocol.Exception.NettyConnectionFailedException;
import com.hooniegit.NettyDataProtocol.Exception.NettyDisconnectedException;
import com.hooniegit.NettyDataProtocol.Exception.NettyUnInitializedException;
import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;
import com.hooniegit.NettyDataProtocol.Tools.KryoObjectListEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;

import java.util.List;

/**
 * 통신 클라이언트 클래스입니다. 서버와의 연결을 관리하고 데이터를 전송합니다.
 * @param <T>
 */
public class NettyClient<T> {

    private final String HOST;
    private final int PORT;
    @Getter
    private volatile Channel CHANNEL;
    private final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    public NettyClient(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    /**
     * 채널을 초기화하고 서버로 연결을 시도합니다.
     * @throws NettyConnectionFailedException 연결 실패 오류
     */
    public void initialize() {
        if (this.CHANNEL != null) {
            this.CHANNEL.close();
        }
        try {
            Channel NEW = new Bootstrap()
                    .group(this.GROUP)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    // NEW
                                    new LengthFieldPrepender(4),
                                    new KryoObjectListEncoder<T>(),
                                    // OLD
//                                    new Encoder<T>(),
//                                    new Decoder(),
                                    new ChannelInboundHandlerAdapter()
                            );
                        }
                    }).connect(HOST, PORT).sync().channel();
            this.CHANNEL = NEW;
        } catch (Exception e) {
            throw new NettyConnectionFailedException(e.toString());
        }
    }

    /**
     * 리스트 객체를 서버로 전송합니다.
     * @param data 전송 데이터
     * @throws NettyUnInitializedException 초기화 비활성 오류
     * @throws NettyDisconnectedException 연결 해제 오류
     */
    public void send(T data) {
        this.CHANNEL.writeAndFlush(data);
    }

    /**
     * 채널과 그룹을 종료합니다.
     */
    public void shutdown() {
        if (this.CHANNEL.isOpen()) {
            this.CHANNEL.close();
        }
        this.GROUP.shutdownGracefully();
    }

}
