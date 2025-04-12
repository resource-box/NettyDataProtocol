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
import org.springframework.stereotype.Service;

@Service
public class MultiChannelClientTest {

    @PostConstruct
    private void task() {

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) { e.printStackTrace(); }

            int channelCount = 64;
            String host = "localhost";
            int port = 9999;

            NioEventLoopGroup group = new NioEventLoopGroup();

            for (int i = 0; i < channelCount; i++) {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new Encoder<Sample>(),
                                        new Decoder(),
                                        new ChannelInboundHandlerAdapter());
                            }
                        });

                try {

                    ChannelFuture future = bootstrap.connect(host, port).sync();

                    List<Sample> dataList = new ArrayList<>();
                    for (int j = 0; j < 3000; j++) {
                        dataList.add(new Sample(j, "name-" + j));
                    }

                    future.channel().writeAndFlush(dataList);
                    System.out.println("Data Transmitted.");

                } catch (Exception e) { e.printStackTrace();}
            }
        }).start();


    }
}
