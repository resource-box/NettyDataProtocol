package com.hooniegit.NettyDataProtocol.Config;

import com.hooniegit.NettyDataProtocol.Client.NettyClient;
import com.hooniegit.NettyDataProtocol.Client.NettyClientManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class NettyClientServiceConfig {

    private final NettyClientManager<Sample> MANAGER;

    public NettyClientServiceConfig() {
        this.MANAGER = new NettyClientManager<>(5, "localhost", 9999);
        this.MANAGER.initialize();
        System.out.println("Initialization Suceeded");
    }

    /**
     * 클라이언트 매니저를 스프링 빈으로 등록합니다.
     * @return
     */
    @Bean
    public NettyClientManager<Sample> nettyClientManager() {
        return this.MANAGER;
    }

    /**
     * 클라이언트를 주기적으로 일괄 초기화합니다.
     */
    @Scheduled(cron = "0 * * * * *")
    private void initialize() {
        System.out.println("Will Run Initialize Scheduled Task");
        this.MANAGER.initialize();
    }

    /**
     * 데이터 전송 예제 :: 샘플 데이터를 서버로 전송합니다.
     */
    @PostConstruct
    private void test() {
        List<Sample> spls = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            spls.add(new Sample(i, "Sample Data " + i));
        }

        NettyClient<Sample> cli = this.MANAGER.getNextClient();
        cli.send(spls);
    }

}

