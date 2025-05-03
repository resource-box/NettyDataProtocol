using Com.Hooniegit.Protobuf;
using NettyServer;
using TcpServerLib;

// 서버 구성을 정의합니다.
int port = 9000;
var handler = new CustomHandler();
var parser = TagGroup.Parser; // Google Protobuf 기반의 자동 생성된 클래스 객체입니다.

// 서버 인스턴스를 선언합니다.
var server = new Server<TagGroup>(port, handler, parser);

// 서버를 시작하면 즉시 데이터 수신을 시작합니다.
// 클라이언트 연결이 초기화되어도 서버는 정상 동작합니다.
server.Start();

// 테스트용 :: 커맨드라인이 입력되면 서비스를 종료합니다.
Console.WriteLine();
Console.ReadLine();
server.Stop();
