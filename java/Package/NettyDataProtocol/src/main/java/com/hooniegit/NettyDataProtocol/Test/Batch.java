package com.hooniegit.NettyDataProtocol.Test;

import java.io.Serializable;
import java.util.List;

/**
 * 특정 시점(batchTs)에 수집된 데이터 항목들의 묶음을 나타냅니다.
 * @param <T> 데이터 항목의 타입
 */
public class Batch<T> implements Serializable {

    public long batchTs;
    public List<T> items;

    public Batch() {}

    public Batch(long batchTs, List<T> items) {
        this.batchTs = batchTs;
        this.items = items;
    }

    /**
     * 배치에 포함된 데이터 항목들의 수를 반환합니다.
     * @return
     */
    public int size() { return items == null ? 0 : items.size(); }

}