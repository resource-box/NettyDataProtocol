package com.hooniegit.NettyDataProtocol.Netty;

/**
 * 샘플 데이터 클래스
 * @param id 샘플의 고유 ID
 * @param value 샘플의 측정값
 * @param timestamp 샘플이 생성된 시점의 타임스탬프 (ISO 8601 형식)
 */
public record Sample(int id, double value, String timestamp) { }
