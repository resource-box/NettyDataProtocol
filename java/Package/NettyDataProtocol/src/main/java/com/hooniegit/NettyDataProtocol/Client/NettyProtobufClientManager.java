package com.hooniegit.NettyDataProtocol.Client;

import com.google.protobuf.GeneratedMessageV3;
import com.hooniegit.NettyDataProtocol.Exception.NettyConnectionFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 클라이언트 관리자 클래스입니다. 복수의 클라이언트 그룹을 관리 및 반환합니다.
 * @param <T>
 */
public class NettyProtobufClientManager<T extends GeneratedMessageV3> {

    // 클래스 내부 속성
    private final List<NettyProtobufClient<T>> CLIENTS = new ArrayList<>();
    private final AtomicInteger INDEX = new AtomicInteger(0);

    /**
     * [생성자] 입력한 수만큼 클라이언트를 생성합니다.
     * @param clientCount 클라이언트 수
     * @param HOST 대상 주소
     * @param PORT 대상 포트
     */
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
            // 일괄 초기화 로직에서는 INDEX 값을 지정하지 않습니다.
            } catch (NettyConnectionFailedException e) {
                throw new NettyConnectionFailedException(e.toString(), null);
            }
        }
    }

    /**
     * 입력받은 인덱스 위치의 클라이언트를 초기화합니다.
     * @param INDEX 클라이언트 위치 지정
     */
    public void initialize(int INDEX) {
        this.CLIENTS.get(INDEX).initialize();
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
