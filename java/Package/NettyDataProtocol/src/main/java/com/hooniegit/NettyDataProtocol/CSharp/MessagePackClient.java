package com.hooniegit.NettyDataProtocol.CSharp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hooniegit.NettyDataProtocol.Test.TcpKryoClient;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.msgpack.jackson.dataformat.MessagePackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MessagePackClient {

    private String host;
    private int port;

    private final EventLoopGroup group;

    private ObjectMapper mapper = new MessagePackMapper();
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean isConnected = false;

    // Logger
    private static final Logger log = LoggerFactory.getLogger(MessagePackClient.class);

    // status
    private final AtomicLong reconnectCount = new AtomicLong(0);
    private final AtomicLong lastConnectedAtMs = new AtomicLong(0);

    public MessagePackClient(String host, int port, int ioThreads) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup(ioThreads);
    }

    public void connect() throws IOException {

    }

    public void doConnect(long delayMs) {
        this.group.schedule(() -> {
            try {
                this.socket = new Socket(host, port);
                this.dos = new DataOutputStream(socket.getOutputStream());
                this.isConnected = true;
                lastConnectedAtMs.set(System.currentTimeMillis());
                log.info("[CLIENT] connected");
            } catch (IOException e) {
                long rc = reconnectCount.incrementAndGet();
                log.warn("[CLIENT] connect failed rc={}, cause={}", rc, e.getMessage()); // 추후 로깅 방식 변경
                doConnect(Math.min(5000, 200 + rc * 50)); // 단순 백오프
            }

        }, delayMs, TimeUnit.MILLISECONDS);
    }

    public Map<String, Object> send(Map<String, Object> data) throws IOException {
        if (!isConnected) {
            throw new IllegalStateException("Not connected to server");
        }
        byte[] payload = mapper.writeValueAsBytes(data);
        this.dos.writeInt(payload.length);
        this.dos.write(payload);
        this.dos.flush();

        int responseLength = dis.readInt();
        byte[] responseBuffer = new byte[responseLength];
        dis.readFully(responseBuffer);

        return mapper.readValue(
                responseBuffer, new TypeReference<>() {
                });
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
        isConnected = false;
    }
}
