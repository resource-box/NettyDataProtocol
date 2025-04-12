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
        // Return if Received Data is Too Small
        // Minimum : payloadLen(int) + listSize(int)
        if (in.readableBytes() < 8) return;

        // Mark Reader Index
        in.markReaderIndex();

        // Check Payload Length
        int payloadLen = in.readInt();

        // Reset Reader Index & Return if Received Data is Not Complete
        if (in.readableBytes() < payloadLen) {
            // System.out.println("payload length " + payloadLen + " not enough. Current length is " + in.readableBytes());
            in.resetReaderIndex();
            return;
        }

        // Read Exact Payload Length & De-Serialize
        ByteBuf frame = in.readBytes(payloadLen);
        Object result = ByteBufSerializer.deserialize(frame);

        // Add Result
        if (result != null) {
            out.add(result);
        }
    }
}