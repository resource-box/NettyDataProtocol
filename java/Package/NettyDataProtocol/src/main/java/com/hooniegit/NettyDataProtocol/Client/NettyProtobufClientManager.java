package com.hooniegit.NettyDataProtocol.Client;

import com.google.protobuf.GeneratedMessageV3;
import com.hooniegit.NettyDataProtocol.Enum.ConnectionStatus;
import com.hooniegit.NettyDataProtocol.Exception.NettyConnectionFailedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 클라이언트 관리자 클래스입니다. 복수의 클라이언트 그룹을 관리 및 반환합니다.
 * @param <T>
 */
public class NettyProtobufClientManager<T extends GeneratedMessageV3> {

    private final List<NettyProtobufClient<T>> CLIENTS = new ArrayList<>();
    private final AtomicInteger INDEX = new AtomicInteger(0);
    private ConnectionStatus STATUS = ConnectionStatus.DISCONNECTED;

    public NettyProtobufClientManager(int clientCount, String HOST, int PORT) {
        for (int i = 0; i < clientCount; i++) {
            CLIENTS.add(new NettyProtobufClient<T>(i, HOST, PORT));
        }
    }

    /**
     * 모든 클라이언트를 초기화합니다.
     */
    public void initialize() {
        for (NettyProtobufClient<T> client : this.CLIENTS) {
            try {
                client.initialize();
            } catch (NettyConnectionFailedException e) {
                throw new NettyConnectionFailedException(e.toString());
            }
        }
    }

    /**
     * 리스트 객체를 TCP 서버로 전송합니다.
     * @param message
     * @return 전송 성공 여부
     */
    public boolean send(T message) {
        if (this.STATUS.getCode() == -1) return false; // STATUS Check

        // DISCONNECTED 상태일 경우 NettyClient 초기화 시도
        if (this.STATUS.getCode() == 0) {
            try {
                update();
            } catch (NettyConnectionFailedException e) {
                throw new NettyConnectionFailedException(e.toString());
            }
            return false;
        }

        // CONNECTED 상태일 경우 데이터 전송 시도, 실패 시 NettyClient 초기화 시도
        NettyProtobufClient<T> client = getNextClient();
        if (!client.CHANNEL.isActive()) {
            this.STATUS = ConnectionStatus.DISCONNECTED;
            update();
            return false;
        }

        client.send(message);
        return true;
    }

    /**
     * 클라이언트 업데이트를 수행합니다.
     */
    public void update() {
        if (this.STATUS.getCode() == -1) return; // STATUS Check

        // NettyClient 일괄 초기화
        this.STATUS = ConnectionStatus.MANAGING; // STATUS 전환 :: Initializing
        try {
            for (NettyProtobufClient<T> client : this.CLIENTS) {
                client.initialize();
            }
            this.STATUS = ConnectionStatus.CONNECTED; // STATUS 전환 :: Initialization Succeed
        } catch (NettyConnectionFailedException ex) {
            this.STATUS = ConnectionStatus.DISCONNECTED; // STATUS 전환 :: Initialization Failed
            throw new NettyConnectionFailedException(ex.toString());
        }

    }

    /**
     * 클라이언트를 낱개로 순서대로 반환합니다.
     * @return NettyClient 객체
     */
    public NettyProtobufClient<T> getNextClient() {
        // 아톰 인티저를 사용하여 순차적으로 채널을 선택합니다.
        int index = this.INDEX.getAndUpdate(i -> (i + 1) % this.CLIENTS.size());
        return this.CLIENTS.get(index);
    }

}
