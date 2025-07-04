package com.hooniegit.NettyDataProtocol.Config;

import com.hooniegit.NettyDataProtocol.Client.NettyProtobufClient;
import com.hooniegit.NettyDataProtocol.Client.NettyProtobufClientManager;
import com.hooniegit.NettyDataProtocol.Protobuf.Message.TagGroup;
import com.hooniegit.NettyDataProtocol.Protobuf.Message.TagData;
//import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class NettyProtobufClientComponent {

    private final NettyProtobufClientManager<TagGroup> MANAGER;

    public NettyProtobufClientComponent() {
        this.MANAGER = new NettyProtobufClientManager<>(5, "localhost", 9999);
        this.MANAGER.initialize();
        System.out.println("Initialization Succeeded");
    }

    /**
     * 클라이언트 매니저를 스프링 빈으로 등록합니다.
     * @return
     */
    @Bean
    public NettyProtobufClientManager<TagGroup> nettyProtobufClientManager() {
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
//    @PostConstruct
    public void startClients() {
        for (int i = 0; i < 2000; i++) {
            List<TagData> tags = new ArrayList<>();
            for (int j = 0; j < 4000; j++) {
                TagData t = TagData.newBuilder()
                        .setId(j)
                        .setDoubleValue(100.123456789)
                        .build();
                tags.add(t);
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            TagGroup tagGroup = TagGroup.newBuilder()
                    .setTimestamp(timestamp)
                    .addAllTags(tags)
                    .build();

            NettyProtobufClient<TagGroup> cli = this.MANAGER.getNextClient();
            cli.send(tagGroup);
        }
    }

}

