package com.hooniegit.NettyDataProtocol.Exception;

public class NettyConnectionFailedException extends IllegalStateException {

    public NettyConnectionFailedException(String message) {
        super(message);
    }

}
