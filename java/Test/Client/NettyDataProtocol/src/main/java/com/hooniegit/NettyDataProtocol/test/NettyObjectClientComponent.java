package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Client.NettyObjectClient; // Package Import

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NettyObjectClientComponent {

    // NettyClient Configuration
    private static final int CHANNEL_COUNT = 64;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    // NettyClient Instance
    private final NettyObjectClient<List<String>> nettyClient = new NettyObjectClient<>(HOST, PORT, CHANNEL_COUNT);

    @PostConstruct
    private void task() throws Exception {
        // Initialize NettyClient

        this.nettyClient.initialize();

        List<String> strs = new ArrayList<>();

        // Test :: Send Datas
        for (int i = 0; i < 1000; i++) {
            // Send Data
            strs.add("Sample" + i);
        }

        this.nettyClient.send(strs);
    }

    @PreDestroy
    private void shutdown() {
        // Shutdown NettyClient
        this.nettyClient.shutdown();
    }

}

