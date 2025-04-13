package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Tools.Decoder;
import com.hooniegit.NettyDataProtocol.Tools.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class MultiChannelClientTest {

    private static final int CHANNEL_COUNT = 64;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private final List<Channel> channels = new ArrayList<>();
    private final NioEventLoopGroup group = new NioEventLoopGroup();

    @PostConstruct
    private void task() {
            try {
                Thread.sleep(3000); // 서버 대기 시간
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new Encoder<Sample>(),
                                    new Decoder(),
                                    new ChannelInboundHandlerAdapter()
                            );
                        }
                    });

            try {
                // ✅ 채널 사전 연결
                for (int i = 0; i < CHANNEL_COUNT; i++) {
                    Channel channel = bootstrap.connect(HOST, PORT).sync().channel();
                    channels.add(channel);
                }

                System.out.println("✅ 채널 연결 완료. 전송 시작.");

                // ✅ 연결 재사용하며 데이터 반복 전송
                while (true) {
                    for (Channel channel : channels) {
                        List<Sample> dataList = new ArrayList<>();
                        for (int j = 0; j < 3000; j++) {
                            dataList.add(new Sample(j, "name-" + j));
                        }

                        channel.writeAndFlush(dataList); // 재사용
                        Thread.sleep(20);
                        dataList.clear();
                    }

                    Thread.sleep(80); // 타이밍 조정
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @PreDestroy
    public void shutdown() {
        for (Channel channel : channels) {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        }
        group.shutdownGracefully();
        System.out.println("🛑 클라이언트 채널 종료");
    }
}

