using Google.Protobuf;
using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace TcpServerLib
{
    public class Server<T> where T : IMessage<T>
    {
        private readonly int _port;
        private readonly Handler<T> _handler;
        private readonly MessageParser<T> _parser;
        private TcpListener _listener;

        public Server(int port, Handler<T> handler, MessageParser<T> parser)
        {
            _port = port;
            _handler = handler;
            _parser = parser;
        }

        public void Start()
        {
            _listener = new TcpListener(IPAddress.Any, _port);
            _listener.Start();
            Console.WriteLine($"[Server] Started on port {_port}");
            Task.Run(() => AcceptLoop());
        }

        private async Task AcceptLoop()
        {
            while (true)
            {
                var client = await _listener.AcceptTcpClientAsync();
                _ = Task.Run(() => HandleClientAsync(client));
            }
        }

        private async Task HandleClientAsync(TcpClient client)
        {
            using (client)
            using (var stream = client.GetStream())
            {
                try
                {
                    while (true)
                    {
                        byte[] lengthBuffer = new byte[4];
                        int lenRead = await stream.ReadAsync(lengthBuffer, 0, 4);
                        if (lenRead == 0) break;

                        int dataLength = BitConverter.ToInt32(lengthBuffer.Reverse().ToArray(), 0);
                        if (dataLength <= 0 || dataLength > 10_000_000) break;

                        byte[] dataBuffer = new byte[dataLength];
                        int readBytes = 0;
                        while (readBytes < dataLength)
                        {
                            int current = await stream.ReadAsync(dataBuffer, readBytes, dataLength - readBytes);
                            if (current == 0) return;
                            readBytes += current;
                        }

                        var message = _parser.ParseFrom(dataBuffer);
                        _handler.OnMessageReceived(message);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[Server] Client handler error: {ex.Message}");
                }
            }
        }

        public void Stop()
        {
            _listener?.Stop();
            Console.WriteLine("[Server] Stopped.");
        }
    }
}
