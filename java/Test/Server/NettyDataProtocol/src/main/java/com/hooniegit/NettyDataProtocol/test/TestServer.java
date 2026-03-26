package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Test.BatchInboundHandler;
import com.hooniegit.NettyDataProtocol.Test.TcpKryoServer;
import io.netty.buffer.PooledByteBufAllocator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public class TestServer {

    private TcpKryoServer server;

    @PostConstruct
    public void start() throws Exception {
        int port = 18000;
        int maxFrameBytes = 192000000;
        int workerThreads = 30;
        Supplier<BatchInboundHandler> handlerSupplier = TestHandler::new;

        this.server = new TcpKryoServer(port, maxFrameBytes, workerThreads, handlerSupplier);
        this.server.start();
    }

    @Scheduled(fixedRate = 5000)
    private void check() {
        var metric = PooledByteBufAllocator.DEFAULT.metric();
        System.out.printf("Active heap: %d, active direct: %d%n",
                metric.usedHeapMemory(), metric.usedDirectMemory());    }

    @PreDestroy
    public void stop() throws Exception {
        if (this.server != null) {
            this.server.close();
        }
    }

}
