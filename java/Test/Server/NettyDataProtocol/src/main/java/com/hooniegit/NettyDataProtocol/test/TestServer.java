package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Server.ObjectServer;
import com.hooniegit.SourceData.Interface.TagData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class TestServer {

    private ObjectServer<TagData<String>> nettyServer;

    @PostConstruct
    public void start() throws Exception {
        int port = 9999;
        int bossThreads = 1;
        int workerThreads = Runtime.getRuntime().availableProcessors();

        nettyServer = new ObjectServer<>(
                port,
                bossThreads,
                workerThreads,
                TestHandler::new
        );

        nettyServer.start();
        System.out.println("Netty TCP Server Started on Port " + port);
    }

    @PreDestroy
    public void stop() throws Exception {
        if (nettyServer != null) {
            nettyServer.stop();
            System.out.println("Netty TCP Server Stopped.");
        }
    }

}
