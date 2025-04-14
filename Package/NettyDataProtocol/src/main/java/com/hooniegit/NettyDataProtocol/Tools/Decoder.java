package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * Decoder Class for Netty Data Protocol
 * @author hooniegit
 */
public class Decoder extends ByteToMessageDecoder {

    /**
     * Decode Method to Decode The ByteBuf
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Return If Necessary
        if (in.readableBytes() < 8) return;

        // Mark Reader Index
        in.markReaderIndex();

        // Check the length of the payload
        // Reset Reader Index & Return If Necessary
        int payloadLen = in.readInt();
        if (in.readableBytes() < payloadLen) {
            in.resetReaderIndex();
            return;
        }

        // Read The Payload
        ByteBuf frame = in.readBytes(payloadLen);
        try {
            // De-Serialize & Add To Out
            Object result = ByteBufSerializer.deserialize(frame);
            if (result != null) {
                out.add(result);
            }
        } finally {
            // Release The Frame (** IMPORTANT **)
            frame.release();
        }
    }

}