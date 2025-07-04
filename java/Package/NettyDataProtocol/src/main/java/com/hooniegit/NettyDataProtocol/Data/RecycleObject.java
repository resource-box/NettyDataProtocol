package com.hooniegit.NettyDataProtocol.Data;

import io.netty.util.Recycler;

/**
 * 풀링 기반의 객체 재사용을 위한 기본 클래스입니다.
 * 해당 클래스를 상속하고 기본 속성 구현 및 recycle() 메서드를 오버라이드해야 합니다.
 */
public class RecycleObject {

    // 재활용 객체 정의
    protected static final Recycler<RecycleObject> RECYCLER = new Recycler<RecycleObject>() {
        @Override
        protected RecycleObject newObject(Handle<RecycleObject> handle) {
            return new RecycleObject(handle);
        }
    };
    protected final Recycler.Handle<RecycleObject> HANDLE;

    // 프라이빗 생성자를 사용해 외부 인스턴스 생성을 방지합니다.
    protected RecycleObject(Recycler.Handle<RecycleObject> HANDLE) {
        this.HANDLE = HANDLE;
    }

    /**
     * 재활용 풀에서 객체를 가져오거나 새로 생성해 반환합니다.
     * @return 재사용 객체
     */
    public static RecycleObject create() {
        return RECYCLER.get();
    }

    /**
     * 객체를 재활용합니다. 속성값을 초기화하도록 상속 클래스에서 오버라이드 해야 합니다.
     */
    public void recycle() {
        RECYCLER.recycle(this, this.HANDLE);
    }

}
