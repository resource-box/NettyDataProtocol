package com.hooniegit.NettyDataProtocol.Netty;

import com.hooniegit.NettyDataProtocol.Client.NettyClientManager;
import com.hooniegit.SourceData.Interface.TagData;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@EnableScheduling
public class NettyClientServiceConfig {

    private final NettyClientManager<List<TagData<Double>>> MANAGER;
    private final Random RANDOM = new Random();

    public NettyClientServiceConfig() {
        this.MANAGER = new NettyClientManager<>(20, "localhost", 9999);
        try {
            this.MANAGER.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 클라이언트 매니저를 스프링 빈으로 등록합니다.
     * @return
     */
    @Bean
    public NettyClientManager<List<TagData<Double>>> nettyClientManager() {
        return this.MANAGER;
    }

    /**
     * 클라이언트를 주기적으로 일괄 초기화합니다.
     */
    @Scheduled(cron = "0 * * * * *")
    private void schedule() { }

    /**
     * 데이터 전송 예제 :: 샘플 데이터를 서버로 전송합니다.
     */
    @PostConstruct
    private void test() {
        while (true) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                List<TagData<Double>> spls = new ArrayList<>();
                for (int i = 0; i < 20000; i++) {
                    spls.add(new TagData<>(i, this.RANDOM.nextDouble(), timestamp));
                }

                this.MANAGER.send(spls);
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

