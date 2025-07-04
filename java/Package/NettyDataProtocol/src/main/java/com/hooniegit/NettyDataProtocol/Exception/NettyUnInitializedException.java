package com.hooniegit.NettyDataProtocol.Exception;

/**
 * 클라이언트 초기화 비활성 상태의 예외 클래스입니다.
 */
public class NettyUnInitializedException extends IllegalStateException {

    public final Integer INDEX;

    public NettyUnInitializedException(String message, Integer INDEX) {
        super(message);
        this.INDEX = INDEX;
    }

}
