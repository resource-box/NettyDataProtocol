package com.hooniegit.NettyDataProtocol.Exception;

/**
 * 클라이언트 연결 실패 상태의 예외 클래스입니다.
 */
public class NettyConnectionFailedException extends IllegalStateException {

    public NettyConnectionFailedException(String message) {
        super(message);}

}
