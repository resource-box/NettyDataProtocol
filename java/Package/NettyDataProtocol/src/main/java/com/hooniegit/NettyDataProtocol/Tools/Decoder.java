package com.hooniegit.NettyDataProtocol.Tools;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 데이터 디코더 클래스입니다.
 */
public class Decoder extends ByteToMessageDecoder {

    /**
     * 바이트버퍼 객체에서 데이터를 읽고 리스트 객체로 변환합니다.
     * @param ctx 채널 핸들러 콘텍스트 (기본값)
     * @param in 바이트버퍼 객체
     * @param out 리스트 객체
     * @throws Exception 변환 과정 내 오류
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 수신 데이터는 최소 8바이트 (정수형 길이 + 데이터) 이상이어야 합니다.
        if (in.readableBytes() < 4) return;

        // 페이로드 길이를 확인하고, 길이가 부족한 경우 데이터를 버퍼링합니다.
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
            frame.release(); // ** IMPORTANT **
        }
    }

}