package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 *
 */
public class Decoder extends ByteToMessageDecoder {
    /**
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) return;

        in.markReaderIndex();
        int payloadLen = in.readInt();

        if (in.readableBytes() < payloadLen) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf frame = in.readBytes(payloadLen);
        try {
            Object result = ByteBufSerializer.deserialize(frame);
            if (result != null) {
                out.add(result);
            }
        } finally {
            frame.release(); // 해제하지 않으면 heap/dir memory 누적됨
        }
    }
}