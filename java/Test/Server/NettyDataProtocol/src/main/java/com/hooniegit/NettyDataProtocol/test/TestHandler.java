package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Test.Batch;
import com.hooniegit.NettyDataProtocol.Test.BatchInboundHandler;
import com.hooniegit.SourceData.Interface.TagData;

public class TestHandler extends BatchInboundHandler {

    @Override
    protected void task(Object msg) {
        System.out.println(msg);
        try {
            Batch<TagData<Double>> batch = (Batch<TagData<Double>>) msg;
            String message = "[Handler] Received batch with timestamp: " + batch.batchTs + " and size: " + batch.size();
            System.out.println(message);
            System.out.println(batch.items.get(0).getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
