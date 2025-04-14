package com.hooniegit.NettyDataProtocol.Tools;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @param <T>
 */
public class Encoder<T> extends MessageToByteEncoder<List<T>> {
    /**
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, List<T> msg, ByteBuf out) throws Exception {
        ByteBuf payload = ctx.alloc().buffer();
        try {
            ByteBufSerializer.serialize(msg, payload);
            out.writeInt(payload.readableBytes());
            out.writeBytes(payload);
        } finally {
            payload.release();
        }
    }
}
