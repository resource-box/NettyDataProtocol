package com.hooniegit.NettyDataProtocol.Config;

import com.hooniegit.NettyDataProtocol.Client.NettyClient;
import com.hooniegit.NettyDataProtocol.Client.NettyClientManager;
import com.hooniegit.NettyDataProtocol.test.Sample;
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

    private final NettyClientManager<Sample> manager;

    public NettyClientServiceConfig() throws Exception {
        this.manager = new NettyClientManager<>(5, "localhost", 9999);
        this.manager.initialize();
        System.out.println("Initialization Suceeded");
    }

    @Bean
    public NettyClientManager<Sample> nettyClientManager() {
        return this.manager;
    }

    @Scheduled(cron = "0 * * * * *")
    private void initialize() throws Exception {
        System.out.println("Will Run Initialize Scheduled Task");
        this.manager.initialize();
    }

    @PostConstruct
    private void test() throws Exception {
        System.out.println("Will Sent Date to Server");

        List<Sample> spls = new ArrayList<>();
        // Test :: Send Datas
        for (int i = 0; i < 1000; i++) {
            spls.add(new Sample(i, "Sample Data " + i));
        }

        NettyClient<Sample> cli = this.manager.getNextClient();
        cli.send(spls);

    }

}

