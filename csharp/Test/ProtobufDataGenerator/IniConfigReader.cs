using System;
using System.Collections.Generic;
using System.IO;

namespace ProtobufDataGenerator
{
    public class IniConfigReader
    {
        private readonly Dictionary<string, Dictionary<string, string>> _data = new Dictionary<string, Dictionary<string, string>>();

        public IniConfigReader(string filePath)
        {
            if (!File.Exists(filePath))
                throw new FileNotFoundException("INI file not found", filePath);

            string[] lines = File.ReadAllLines(filePath);
            Parse(lines);
        }

        private void Parse(string[] lines)
        {
            string currentSection = "";

            foreach (string rawLine in lines)
            {
                string line = rawLine.Trim();

                if (string.IsNullOrEmpty(line) || line.StartsWith(";") || line.StartsWith("#"))
                    continue;

                if (line.StartsWith("[") && line.EndsWith("]"))
                {
                    currentSection = line.Substring(1, line.Length - 2);
                    if (!_data.ContainsKey(currentSection))
                        _data[currentSection] = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase);
                }
                else if (line.Contains("="))
                {
                    string[] parts = line.Split(new[] { '=' }, 2);
                    if (parts.Length == 2)
                    {
                        string key = parts[0].Trim();
                        string value = parts[1].Trim();

                        if (!_data.ContainsKey(currentSection))
                            _data[currentSection] = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase);

                        _data[currentSection][key] = value;
                    }
                }
            }
        }

        public string Get(string section, string key, string defaultValue)
        {
            Dictionary<string, string> sec;
            string val;

            if (_data.TryGetValue(section, out sec) && sec.TryGetValue(key, out val))
            {
                return val;
            }

            return defaultValue;
        }

        public string Get(string section, string key)
        {
            return Get(section, key, null);
        }
    }
}
