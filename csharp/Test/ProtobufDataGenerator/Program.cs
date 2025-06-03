using System;
using System.Net.Sockets;
using Google.Protobuf;
using System.Threading;
using ProtobufDataGenerator;
using Com.Hooniegit.Protobuf;
using System.Collections.Generic;
using System.Linq;
using System.IO;

namespace TagGroupSender
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8;

            try
            {
                var config = new IniConfigReader("config/target.ini");

                string host = config.Get("server", "host", "127.0.0.1");
                int port = int.Parse(config.Get("server", "port", "8000"));

                int range = int.Parse(config.Get("generate", "range", "30000"));
                int size = int.Parse(config.Get("generate", "size", "2000"));
                int sleep = int.Parse(config.Get("generate", "sleep", "1000"));

                string location = config.Get("log", "location", "logs/sample.log");
                List<int> ids = config.Get("log", "ids", "500, 1000, 1500, 2000")
                    .Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries)
                    .Select(id => int.Parse(id.Trim()))
                    .ToList();

                Console.WriteLine($"[Client] Connecting to {host}:{port}");

                using (TcpClient client = new TcpClient())
                {
                    client.Connect(host, port);
                    using (NetworkStream stream = client.GetStream())
                    {
                        Console.WriteLine("[Client] Connected. Press any key to stop...");

                        int offset = 0;

                        while (!Console.KeyAvailable)
                        {
                            int startId = offset + 1;
                            TagGroup tagGroup = GenerateDummyTagGroup(startId, size);

                            byte[] payload = tagGroup.ToByteArray();

                            byte[] lengthPrefix = BitConverter.GetBytes(payload.Length);
                            if (BitConverter.IsLittleEndian)
                                Array.Reverse(lengthPrefix);

                            stream.Write(lengthPrefix, 0, lengthPrefix.Length);
                            stream.Write(payload, 0, payload.Length);

                            Console.WriteLine($"[Client] Sent TagGroup (IDs {startId} ~ {startId + size - 1}, {payload.Length} bytes)");

                            foreach (int id in ids)
                            {
                                var tag = tagGroup.Tags.FirstOrDefault(t => t.Id == id);
                                if (tag != null)
                                    //Console.WriteLine($"{tag.Id} : {tag.DoubleValue}");
                                    WriteLogs(location, $"{tag.Id} : {tag.DoubleValue}");
                            }

                            offset += size;
                            if (offset > range)
                            {
                                offset = 0;
                            }

                            Thread.Sleep(sleep);
                        }

                        Console.ReadKey();
                        Console.WriteLine("[Client] Transmission stopped by user.");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[Error] {ex.Message}");
            }
        }

        static TagGroup GenerateDummyTagGroup(int startId, int size)
        {
            TagGroup group = new TagGroup();
            group.Timestamp = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ");

            Random rand = new Random();

            for (int i = 0; i < size; i++)
            {
                TagData tag = new TagData
                {
                    Id = startId + i,
                    DoubleValue = rand.NextDouble() * 1000
                };
                group.Tags.Add(tag);
            }

            return group;
        }

        static void WriteLogs(string logFilePath, string message)
        {
            Directory.CreateDirectory(Path.GetDirectoryName(logFilePath));

            using (StreamWriter writer = new StreamWriter(logFilePath, append: true))
            {
                //writer.WriteLine($"[{DateTime.Now:yyyy-MM-dd HH:mm:ss}] Application started.");
                writer.WriteLine(message);
            }
        }
    }
}
