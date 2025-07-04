package com.hooniegit.NettyDataProtocol.Tools;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 데이터 인코더 클래스입니다.
 * @param <T>
 */
public class Encoder<T> extends MessageToByteEncoder<List<T>> {

    /**
     * 데이터를 직렬화하고 바이트버퍼 객체에 입력합니다.
     * @param ctx 채널 핸들러 콘텍스트 (기본값)
     * @param msg 직렬화 대상 객체
     * @param out 바이트버퍼 객체
     * @throws Exception 변환 과정 내 오류
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, List<T> msg, ByteBuf out) throws Exception {
        // 전송 데이터의 페이로드 길이 정보를 입력합니다.
        ByteBuf payload = ctx.alloc().buffer();
        try {
            ByteBufSerializer.serialize(msg, payload);
            out.writeInt(payload.readableBytes());
            out.writeBytes(payload);
        } finally {
            payload.release(); // ** IMPORTANT **
        }
    }

}
