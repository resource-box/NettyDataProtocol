package com.hooniegit.NettyDataProtocol.Test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ByteBuf로부터 데이터를 읽어 Kryo로 역직렬화하여 객체로 디코딩합니다.
 */
public final class KryoDecoder extends ByteToMessageDecoder {
    private final KryoPool pool;

    public KryoDecoder(KryoPool pool) {
        this.pool = pool;
    }

    /**
     * ByteBuf로부터 데이터를 읽어 Kryo로 역직렬화하여 객체로 디코딩합니다.
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes();
        if (readable <= 0) return;

        byte[] arr = new byte[readable];
        in.readBytes(arr);

        KryoHolder h = pool.borrow();
        boolean ok = false;
        try {
            // KryoHolder.activateObject() 메서드 또는 해당 위치에서 Kryo 객체의 클래스로더를 현재 스레드의 TCCL로 보정합니다.
            // h.kryo.setClassLoader(Thread.currentThread().getContextClassLoader());

            // Input에 바이트 배열을 설정합니다. Input 객체는 내부적으로 byte[]를 참조하여 데이터를 읽습니다.
            h.input.setBuffer(arr, 0, readable);

            Object obj = h.kryo.readClassAndObject(h.input);
            out.add(obj);

            ok = true;
        } finally {
            if (ok) pool.release(h); // 역직렬화 성공 시 객체 반납
            else pool.invalidate(h); // 역직렬화 실패 시 객체 폐기
        }
    }
}