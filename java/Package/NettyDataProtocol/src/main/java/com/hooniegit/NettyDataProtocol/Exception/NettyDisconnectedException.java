package com.hooniegit.NettyDataProtocol.Exception;

/**
 * 클라이언트 연결 해제 상태의 예외 클래스입니다.
 */
public class NettyDisconnectedException extends IllegalStateException {

    public final Integer INDEX;

    public NettyDisconnectedException(String message, Integer INDEX) {
        super(message);
        this.INDEX = INDEX;
    }

}
