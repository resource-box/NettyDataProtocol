package com.hooniegit.NettyDataProtocol.Data;

import io.netty.util.Recycler;
import lombok.Getter;
import lombok.Setter;

/**
 * 풀링 기반의 객체 재사용을 위한 예제 클래스입니다.
 */
public class SampleRecycleObject {

    @Getter @Setter
    private int id;
    @Getter @Setter
    private String value;

    // 재활용 객체 정의
    private static final Recycler<SampleRecycleObject> RECYCLER = new Recycler<SampleRecycleObject>() {
        @Override
        protected SampleRecycleObject newObject(Handle<SampleRecycleObject> handle) {
            return new SampleRecycleObject(handle);
        }
    };
    private final Recycler.Handle<SampleRecycleObject> HANDLE;

    // 프라이빗 생성자를 사용해 외부 인스턴스 생성을 방지합니다.
    private SampleRecycleObject(Recycler.Handle<SampleRecycleObject> HANDLE) {
        this.HANDLE = HANDLE;
    }

    /**
     * 재활용 풀에서 객체를 가져오거나 새로 생성해 반환합니다.
     * @return 재사용 객체
     */
    public static SampleRecycleObject create() {
        return RECYCLER.get();
    }

    /**
     * 객체를 재활용합니다
     */
    public void recycle() {
        this.id = 0;
        this.value = null;
        RECYCLER.recycle(this, this.HANDLE);
    }

    public void set(int id, String value) {
        this.id = id;
        this.value = value;
    }

}

