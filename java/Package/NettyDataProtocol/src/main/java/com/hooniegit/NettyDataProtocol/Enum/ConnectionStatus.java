package com.hooniegit.NettyDataProtocol.Enum;

import lombok.Getter;

@Getter
public enum ConnectionStatus {

    CONNECTED(1, "CONNECTED"),
    DISCONNECTED(0, "DISCONNECTED"),
    MANAGING(-1, "MANAGING");

    private final int code;
    private final String description;

    ConnectionStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

}