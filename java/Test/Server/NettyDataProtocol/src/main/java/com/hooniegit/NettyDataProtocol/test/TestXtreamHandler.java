package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.SourceData.Interface.TagData;
import com.hooniegit.Xtream.Tools.Event;
import com.hooniegit.Xtream.Tools.Handler;

import java.util.List;

public class TestXtreamHandler extends Handler<List<TagData<Double>>> {

    @Override
    protected void process(Event<List<TagData<Double>>> event) {
//        List<TagData<Double>> d = event.getData();
//        try {
//            System.out.println(d.size() + " : " + d.get(0).getTimestamp());
//        } finally {
//            d = null;
//        }
    }

}
