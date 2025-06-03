using Google.Protobuf;

namespace TcpServerLib
{
    public interface Handler<T> where T : IMessage<T>
    {
        void OnMessageReceived(T message);
    }
}