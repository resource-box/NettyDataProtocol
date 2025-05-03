using Com.Hooniegit.Protobuf;
using TcpServerLib;

namespace NettyServer
{

    /// <summary>
    /// Handler<T> 인터페이스를 구현하는 커스텀 이벤트 핸들러를 정의합니다.
    /// </summary>
    public class CustomHandler : Handler<TagGroup>
    {
        /// <summary>
        /// 인터페이스의 추상 메서드입니다. 반드시 구현해야 합니다.
        /// </summary>
        /// <param name="message"></param>
        public void OnMessageReceived(TagGroup message)
        {
            // 수신한 데이터로 수행할 작업을 정의합니다.
            Console.WriteLine($"SIZE: {message.Tags.Count}, Timestamp: {message.Timestamp}");
        }

    }
}
