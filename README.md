# NettyDataProtocol
'NettyDataProtocol' 은 Netty 패키지를 전신으로 하는 TCP 프로토콜 기반의 데이터 전송용 패키지입니다.<br>
모든 라이센스는 Apache License 2.0을 따릅니다.<br>

### About
- 해당 패키지는 Java Application 간의 상호 통신, 또는 Java to .NET Application 통신을 위해 설계되었습니다.
    - Java Application 간의 상호 통신은 List<T> 객체 기반입니다.
    - Java to .NET Application 통신은 Google Protobuf 객체 기반입니다.<br><br>
- 해당 패키지는 다중 스레드에서의 초고반복 데이터 전송 상황을 고려(병목 방지, 성능 고려)해 설계되었습니다.
    - 연결 초기화 시에 OS에 바인딩된 기존 연결을 닫고 신규 연결을 생성합니다.
    - 데이터 전송 과정에서 자동으로 연결 상태를 점검 및 복원합니다.
    - 전송 실패한 데이터는 복원 및 재시도하지 않고 즉시 제거합니다.

### How to use
1. Client Application 전송 예시
```java
@Configuration
public class ClientService {

    /**
     * NettyClientMananger 객체는 리스트 형태의 데이터를 전송합니다.
     */
    private final NettyClientManager<TagData<String>> MANAGER;
    private final int CLIENT_COUNT = 5;
    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 9999;

    public ClientService() {
        this.MANAGER = new NettyClientManager<>(this.CLIENT_COUNT, this.SERVER_HOST, this.SERVER_PORT);
        try {
            /**
             * initialize() 메서드를 통해 초기 서버와의 연결을 시도합니다.
             */
            this.MANAGER.initialize();
        } catch (Exception e) {
            // add logging logics here..
        }
    }

    /**
     * 클라이언트 매니저를 스프링 빈으로 등록합니다.
     * @return NettyClientManager<TagData<String>> 객체
     */
    @Bean
    public NettyClientManager<TagData<String>> nettyClientManager() {
        return this.MANAGER;
    }
    
    /**
     * 데이터 전송 예제 :: 샘플 데이터를 서버로 전송합니다.
     */
    @PostConstruct
    private void test() {
        while (true) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                List<TagData<String>> spls = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    spls.add(new TagData<>(i, "Data " + i, timestamp));
                }

                /**
                 * <중요>
                 *     NettyClientManager 객체는 send() 호출 시점에 서버와의 연결 상태를 점검 및 갱신합니다.
                 *     따라서 연결 상태를 점검 및 갱신하는 별도의 로직을 필요로 하지 않습니다.
                 * </중요>
                 */
                this.MANAGER.send(spls);
            } catch (Exception e) {
                // add logging logics here..
            }
        }
    }

}
```

2. Server Application 수신 예시
```java
@Service
public class TestServer {

    private ObjectServer<TagData<String>> nettyServer;

    @PostConstruct
    public void start() throws Exception {
        int port = 9999;
        int bossThreads = 1;
        int workerThreads = Runtime.getRuntime().availableProcessors();

        nettyServer = new ObjectServer<>(
                port,
                bossThreads,
                workerThreads,
                TestHandler::new
        );

        nettyServer.start();
        System.out.println("Netty TCP Server Started on Port " + port);
    }

    @PreDestroy
    public void stop() throws Exception {
        if (nettyServer != null) {
            nettyServer.stop();
            System.out.println("Netty TCP Server Stopped.");
        }
    }

}

```
```java
public class TestHandler extends DefaultHandler<TagData<String>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<TagData<String>> msg) {
        System.out.println("data received");
        for (TagData<String> data : msg) {
            System.out.println("Received data: " + data.getTimestamp());
        }
    }
}

```

### Version (Newest)
- v3.0.0
  - 2025-08-18 release
