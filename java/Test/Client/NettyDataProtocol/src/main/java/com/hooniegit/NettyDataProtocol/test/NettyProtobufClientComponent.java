package com.hooniegit.NettyDataProtocol.test;

import com.hooniegit.NettyDataProtocol.Protobuf.Message.TagGroup;
import com.hooniegit.NettyDataProtocol.Protobuf.Message.TagData;
import com.hooniegit.NettyDataProtocol.Client.NettyProtobufClient; // Package Import

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NettyProtobufClientComponent {

    private static final int CHANNEL_COUNT = 32;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9000;

    private final NettyProtobufClient<TagGroup> nettyClient = new NettyProtobufClient<>(HOST, PORT, CHANNEL_COUNT);

//    @PostConstruct
    public void startClients() throws Exception {
        // Initialize NettyClient
        this.nettyClient.initialize();

        // Test :: Repeatable Task
        for (int i = 0; i < 2000; i++) {
            // Generate TagGroup Instance
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

            // Send Data
            this.nettyClient.send(tagGroup);
        }

        // Shutdown NettyClient
        this.nettyClient.shutdown();
    }

}

