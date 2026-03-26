package com.hooniegit.NettyDataProtocol.Test;

import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 객체를 Kryo로 직렬화하여 ByteBuf로 인코딩합니다.
 */
public final class KryoEncoder extends MessageToByteEncoder<Object> {
    private final KryoPool pool;

    public KryoEncoder(KryoPool pool) {
        this.pool = pool;
    }

    /**
     * 객체를 Kryo로 직렬화하여 ByteBuf로 인코딩합니다.
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        KryoHolder h = pool.borrow();
        boolean ok = false;

        try {
            // KryoHolder.activateObject() 메서드 또는 해당 위치에서 Kryo 객체의 클래스로더를 현재 스레드의 TCCL로 보정합니다.
            // h.kryo.setClassLoader(Thread.currentThread().getContextClassLoader());

            // Output 포인터 초기화
            // Kryo v4.0.0 Output : reset() → setPosition(0)
            Output output = h.output;
            output.setPosition(0);

            h.kryo.writeClassAndObject(output, msg);
            output.flush();

            // Output에서 직렬화된 데이터를 ByteBuf로 복사합니다.
            byte[] buf = output.getBuffer();
            int len = output.position();
            out.writeBytes(buf, 0, len);

            ok = true;
        } finally {
            if (ok) pool.release(h); // 직렬화 성공 시 객체 반납
            else pool.invalidate(h); // 직렬화 실패 시 객체 폐기
        }
    }
}
