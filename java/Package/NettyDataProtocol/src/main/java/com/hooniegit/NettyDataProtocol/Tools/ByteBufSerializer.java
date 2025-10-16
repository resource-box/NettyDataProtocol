package com.hooniegit.NettyDataProtocol.Tools;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

/**
 * 데이터 직렬화 및 역직렬화 클래스입니다.
 * @Kryo 데이터 직렬화 및 역직렬화를 수행합니다. v4.0.0 이후 버전은 사용하지 않습니다.
 * @Pool2 객체 풀을 구성하고 객체를 재사용합니다.
 */
public class ByteBufSerializer {

    private static final ObjectPool<Kryo> kryoPool;

    /**
     * 객체 풀을 구성 및 초기화합니다.
     */
    static {
        GenericObjectPoolConfig<Kryo> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(10); // Kryo 인스턴스 수는 조정할 수 있습니다.

        kryoPool = new GenericObjectPool<>(new BasePooledObjectFactory<Kryo>() {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                // ** 클래스 로더를 구성하여 ClassNotFoundException을 방지합니다. **
                kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
                // ** 참조 추적을 비활성화하여 성능을 향상시킵니다. (필요에 따라 조정 가능) **
                kryo.setReferences(false);
                return kryo;
            }

            @Override
            public PooledObject<Kryo> wrap(Kryo kryo) {
                return new DefaultPooledObject<>(kryo);
            }
        }, config);
    }

    /**
     * 데이터를 직렬화하여 바이트버퍼 객체에 입력합니다.
     * @param object 직렬화 대상 객체
     * @param out 바이트버퍼 객체
     * @param <T>
     * @throws Exception 직렬화 과정 내 오류
     */
    public static <T> void serialize(T object, ByteBuf out) throws Exception {
        Kryo kryo = null;
        try {
            kryo = kryoPool.borrowObject();
            Output output = new Output(new ByteBufOutputStream(out));
            kryo.writeClassAndObject(output, object);
//            output.close();
            output.flush();
        } finally {
            if (kryo != null) {
                kryo.reset();
                kryoPool.returnObject(kryo);
            }
        }
    }

    /**
     * 바이트버퍼 객체에서 데이터를 읽고 역직렬화합니다.
     * @param in 바이트버퍼 객체
     * @return 역직렬화 객체
     * @param <T>
     * @throws Exception 역직렬화 과정 내 오류
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(ByteBuf in) throws Exception {
        Kryo kryo = null;
        Input input = null;
        try {
            kryo = kryoPool.borrowObject();
            input = new Input(new ByteBufInputStream(in));
            return (T) kryo.readClassAndObject(input);
        } finally {
            if (input != null) input.close();
            if (kryo != null) {
                kryo.reset();
                kryoPool.returnObject(kryo);
            }
        }
    }
}

