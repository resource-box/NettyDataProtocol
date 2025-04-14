package com.hooniegit.NettyDataProtocol.Tools;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encoder Class for Netty Data Protocol
 * @param <T>
 */
public class Encoder<T> extends MessageToByteEncoder<List<T>> {

    /**
     * Encode Method to Encode The List<T> to ByteBuf
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, List<T> msg, ByteBuf out) throws Exception {
        // Create A Payload ByteBuf
        ByteBuf payload = ctx.alloc().buffer();
        try {
            // Serialize & Write
            ByteBufSerializer.serialize(msg, payload);
            out.writeInt(payload.readableBytes());
            out.writeBytes(payload);
        } finally {
            // Release The Payload (** IMPORTANT **)
            payload.release();
        }
    }

}
