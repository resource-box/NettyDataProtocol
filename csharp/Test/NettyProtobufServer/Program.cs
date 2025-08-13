using System;
using Com.Hooniegit.Protobuf;
using NettyServer;
using TcpServerLib;

namespace TcpServerApp
{
    class Program
    {
        static void Main(string[] args)
        {
            // 서버 구성을 정의합니다.
            int port = 8090;
            var handler = new CustomHandler();
            var parser = TagGroup.Parser; // Google Protobuf 기반 자동 생성 클래스의 파서

            // 서버 인스턴스를 선언합니다.
            var server = new Server<TagGroup>(port, handler, parser);

            // 서버를 시작합니다.
            server.Start();

            // 테스트용 :: 커맨드라인이 입력되면 서비스를 종료합니다.
            Console.WriteLine("서버가 시작되었습니다. 종료하려면 Enter 키를 누르세요.");
            Console.ReadLine();

            // 서버를 종료합니다.
            server.Stop();
            Console.WriteLine("서버가 종료되었습니다.");
        }
    }
}
