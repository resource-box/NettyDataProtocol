package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Client.NettyObjectClient; // Package Import

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class NettyObjectClientService {

    // NettyClient Configuration
    private static final int CHANNEL_COUNT = 64;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    // NettyClient Instance
    private final NettyObjectClient<Sample> nettyClient = new NettyObjectClient<>(HOST, PORT, CHANNEL_COUNT);

    @PostConstruct
    private void task() throws InterruptedException {
        // Initialize NettyClient
        this.nettyClient.initialize();

        // Test :: Send Datas
        for (int i = 0; i < 1000; i++) {
            // Send Data
            this.nettyClient.send(new Sample(i, "Sample" + i));
        }
    }

    @PreDestroy
    private void shutdown() {
        // Shutdown NettyClient
        this.nettyClient.shutdown();
    }

}

