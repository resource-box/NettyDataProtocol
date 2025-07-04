package com.hooniegit.NettyDataProtocol.Client;

import com.google.protobuf.GeneratedMessageV3;
import com.hooniegit.NettyDataProtocol.Exception.NettyConnectionFailedException;
import com.hooniegit.NettyDataProtocol.Exception.NettyDisconnectedException;
import com.hooniegit.NettyDataProtocol.Exception.NettyUnInitializedException;
import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.List;

/**
 * 통신 클라이언트 클래스입니다. 프로토버퍼 객체를 다루며, 복수의 클라이언트를 관리 및 반환합니다.
 * @param <T>
 */
public class NettyProtobufClient<T extends GeneratedMessageV3> {

    // 생성자 속성
    public final int INDEX;
    private final String HOST;
    private final int PORT;
    // 클래스 내부 속성
    private Channel CHANNEL;
    private final NioEventLoopGroup GROUP = new NioEventLoopGroup();

    // 상태 속성
    public boolean IS_INITIALIZED = false;

    public NettyProtobufClient(int INDEX, String HOST, int PORT) {
        this.INDEX = INDEX;
        this.HOST = HOST;
        this.PORT = PORT;
    }

    /**
     * 채널을 초기화하고 서버로 연결을 시도합니다.
     * @throws NettyConnectionFailedException 연결 실패 오류
     */
    public void initialize() {
        try {
            // 채널을 별도로 신규 정의해 비정상적인 할당을 방지합니다.
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
            throw new NettyConnectionFailedException(e.toString(), INDEX);
        }
    }

    /**
     * 리스트 객체를 서버로 전송합니다.
     * @param data 전송 데이터
     * @throws NettyUnInitializedException 초기화 비활성 오류
     * @throws NettyDisconnectedException 연결 해제 오류
     */
    public void send(T data) {
        if (!this.IS_INITIALIZED) throw new NettyUnInitializedException("Netty Client is Not Initialized", INDEX);

        try {
            byte[] bytes = data.toByteArray();
            ByteBuf buf = Unpooled.buffer(4 + bytes.length);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            this.CHANNEL.writeAndFlush(buf.retainedDuplicate());
        } catch (Exception e) {
            this.IS_INITIALIZED = false;
            throw new NettyDisconnectedException(e.toString(), this.INDEX);
        }
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
