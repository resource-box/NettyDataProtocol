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
 * ByteBufSerializer Class for Netty Data Protocol (Based On Kryo)
 */
public class ByteBufSerializer {
    private static final ObjectPool<Kryo> kryoPool;

    static {
        GenericObjectPoolConfig<Kryo> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(10);

        kryoPool = new GenericObjectPool<>(new BasePooledObjectFactory<Kryo>() {
            @Override
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
                return kryo;
            }

            @Override
            public PooledObject<Kryo> wrap(Kryo kryo) {
                return new DefaultPooledObject<>(kryo);
            }
        }, config);
    }

    /**
     * Serialization :: Serialize The Object to ByteBuf
     * @param object
     * @param out
     * @param <T>
     * @throws Exception
     */
    public static <T> void serialize(T object, ByteBuf out) throws Exception {
        Kryo kryo = null;
        try {
            kryo = kryoPool.borrowObject();
            Output output = new Output(new ByteBufOutputStream(out));
            kryo.writeClassAndObject(output, object);
            output.close();
        } finally {
            if (kryo != null) kryoPool.returnObject(kryo);
        }
    }

    /**
     * De-Serialization :: De-Serialize The ByteBuf to Object
     * @param in
     * @return
     * @param <T>
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(ByteBuf in) throws Exception {
        Kryo kryo = null;
        try {
            kryo = kryoPool.borrowObject();
            Input input = new Input(new ByteBufInputStream(in));
            return (T) kryo.readClassAndObject(input);
        } finally {
            if (kryo != null) kryoPool.returnObject(kryo);
        }
    }

}

