package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class KryoObjectListEncoder<T> extends MessageToByteEncoder<List<T>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, List<T> msg, ByteBuf out) throws Exception {
        // 여기서 out은 "payload 버퍼" (LengthFieldPrepender가 앞단에서 길이 헤더를 붙임)
        ByteBufSerializer.serialize(msg, out);
        // 별도 임시 payload 버퍼 만들지 않아도 됨
    }
}
