package com.hooniegit.NettyDataProtocol.Exception;

import java.io.IOException;

/**
 * 서버 시작 실패 상태의 예외 클래스입니다.
 */
public class NettyTransportFailedException extends IOException {

    public final Integer INDEX;

    public NettyTransportFailedException(String message, Integer INDEX) {
        super(message);
        this.INDEX = INDEX;
    }

}
