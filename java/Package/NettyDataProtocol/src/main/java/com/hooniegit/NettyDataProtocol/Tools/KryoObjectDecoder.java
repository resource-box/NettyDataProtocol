package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public final class KryoObjectDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 여기 도달하는 ByteBuf는 이미 "정상 프레임 (길이 필드 제거됨)"
        // 안전하게 바로 역직렬화
        Object obj = ByteBufSerializer.deserialize(in);
        if (obj != null) out.add(obj);
        // ByteToMessageDecoder는 in의 release를 자체 관리 (프레임 소모됨)
    }
}

