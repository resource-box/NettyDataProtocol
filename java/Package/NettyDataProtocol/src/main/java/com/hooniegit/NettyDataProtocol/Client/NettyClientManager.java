package com.hooniegit.NettyDataProtocol.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyClientManager<T> {

    private final List<NettyClient<T>> CLIENTS;
    private final AtomicInteger INDEX = new AtomicInteger(0);

    public NettyClientManager(int clientCount, String HOST, int PORT) {
        this.CLIENTS = new ArrayList<>(clientCount);

        for (int i = 0; i < clientCount; i++) {
            CLIENTS.add(new NettyClient<T>(i, HOST, PORT));
        }
    }

    public void initialize() throws Exception {
        for (NettyClient<T> client : this.CLIENTS) {
            client.initialize();
        }
    }

    public void initialize(int INDEX) throws Exception {
        this.CLIENTS.get(INDEX).initialize();
    }

    public NettyClient<T> getNextClient() {
        int index = this.INDEX.getAndUpdate(i -> (i + 1) % this.CLIENTS.size());
        return this.CLIENTS.get(index);
    }

}
