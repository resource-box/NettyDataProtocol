package com.hooniegit.NettyDataProtocol.Data;

/**
 * 스레드 단위의 객체 재사용을 위한 기본 클래스입니다.
 * 해당 클래스를 상속하고 기본 속성 구현을 수행해야 합니다.
 */
public class ThreadLocalObject {

    // 스레드 로컬 객체 정의
    private static final ThreadLocal<ThreadLocalObject> THREAD_LOCAL = ThreadLocal.withInitial(ThreadLocalObject::new);

    // 프라이빗 생성자를 사용해 외부 인스턴스 생성을 방지합니다.
    private ThreadLocalObject() { }

    /**
     * 재활용 풀에서 객체를 가져오거나 새로 생성해 반환합니다.
     * @return 재사용 객체
     */
    public static ThreadLocalObject getInstance() {
        return THREAD_LOCAL.get();
    }

}
