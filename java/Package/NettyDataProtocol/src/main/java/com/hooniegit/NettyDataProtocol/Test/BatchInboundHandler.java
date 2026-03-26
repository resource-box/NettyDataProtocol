package com.hooniegit.NettyDataProtocol.Test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP 클라이언트 : 수신된 데이터를 검증 및 처리하는 핸들러입니다.
 * 운영 시 해당 클래스를 상속받는 커스텀 핸들러를 정의해 사용합니다.
 */
public class BatchInboundHandler extends SimpleChannelInboundHandler<Object> {

    // Logger
    protected static final Logger log = LoggerFactory.getLogger(BatchInboundHandler.class);

    /**
     * 데이터 수신 시점에 호출되는 메서드입니다.
     * 수신된 메세지의 데이터 타입을 검증한 후, 실제 메세지 처리를 담당하는 task() 메서드를 호출합니다.
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // 수신된 메세지의 데이터 타입 검증
        if (msg instanceof Batch batch) {
            try {
                task(msg);
            } catch (Exception e) {
                log.warn("Failed to run task(msg): ", e);
            }
        } else {
             log.warn("Unknown message type: {}", msg.getClass());
        }
    }

    /**
     * 실제 메세지 처리를 담당하는 메서드입니다.
     * 커스텀 핸들러 구성 시 해당 메서드를 오버라이드하여 구체적인 작업을 구현합니다.
     * @param msg
     */
    protected void task(Object msg) { }

}
