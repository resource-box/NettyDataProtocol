package com.hooniegit.NettyDataProtocol.Netty;

import com.hooniegit.NettyDataProtocol.Test.Batch;
import com.hooniegit.NettyDataProtocol.Test.TcpKryoClient;
import com.hooniegit.SourceData.Interface.TagData;
import io.netty.channel.ChannelFuture;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@EnableScheduling
public class NettyClientServiceConfig {

    private final Random random = new Random();
    private final TcpKryoClient client = new TcpKryoClient("localhost", 18000, 192000000, 30);

    @Scheduled(cron = "0 * * * * *")
    private void schedule() { }

    @PostConstruct
    private void test() {

        try {
            this.client.connect();
            Thread.sleep(1000);
            System.out.println(this.client.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                List<TagData<Double>> items = new ArrayList<>();
                for (int i = 0; i < 50000; i++) {
                    items.add(new TagData<>(i, this.random.nextDouble(), timestamp));
                }

                Batch<TagData<Double>> batch = new Batch<>(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), items);

                ChannelFuture result = this.client.send(batch);
                this.client.flush();
                Thread.sleep(50);

                String r = result.toString();
                System.out.println(r);
                System.out.println("Sent batch with timestamp: " + batch.batchTs + " and size: " + batch.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

