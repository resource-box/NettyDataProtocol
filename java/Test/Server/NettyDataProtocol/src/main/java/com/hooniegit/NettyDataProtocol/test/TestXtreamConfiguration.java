package com.hooniegit.NettyDataProtocol.test;

import java.util.List;

import com.hooniegit.SourceData.Interface.TagData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hooniegit.Xtream.Tools.Handler;
import com.hooniegit.Xtream.Tools.StreamAutoConfiguration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(StreamAutoConfiguration.class)
public class TestXtreamConfiguration {

    @Bean
    public List<Handler<List<TagData<Double>>>> handlers() {
        return List.of(new TestXtreamHandler());
    }

    @Bean
    public StreamAutoConfiguration<List<TagData<Double>>> streamAutoConfiguration() {
        return new StreamAutoConfiguration<>();
    }

}
