package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Server.ObjectServer;
import com.hooniegit.SourceData.Interface.TagData;
import com.hooniegit.Xtream.Tools.StreamManager;
import io.netty.buffer.PooledByteBufAllocator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServer {

    private ObjectServer<List<TagData<Double>>> nettyServer;

    private final StreamManager<List<TagData<Double>>> MANAGER;

    public TestServer(StreamManager<List<TagData<Double>>> MANAGER) {
        this.MANAGER = MANAGER;
    }

    @PostConstruct
    public void start() throws Exception {
        int port = 9999;
        int bossThreads = 1;
        int workerThreads = Runtime.getRuntime().availableProcessors();

        nettyServer = new ObjectServer<>(
                port,
                bossThreads,
                workerThreads,
//                TestHandler::new
                () -> new TestHandler(MANAGER)
        );


        nettyServer.start();
        System.out.println("Netty TCP Server Started on Port " + port);
    }

    @Scheduled(fixedRate = 5000)
    private void check() {
        var metric = PooledByteBufAllocator.DEFAULT.metric();
        System.out.printf("Active heap: %d, active direct: %d%n",
                metric.usedHeapMemory(), metric.usedDirectMemory());    }

    @PreDestroy
    public void stop() throws Exception {
        if (nettyServer != null) {
            nettyServer.stop();
            System.out.println("Netty TCP Server Stopped.");
        }
    }

}
