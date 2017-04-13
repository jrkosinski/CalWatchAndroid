using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.IO;
using System.Threading.Tasks;

using Newtonsoft.Json;

namespace CalWatch.Api.Utilities
{
    public class JsonContent : HttpContent
    {
        private readonly MemoryStream memStream = new MemoryStream();

        public JsonContent(object value)
        {
            Headers.ContentType = new MediaTypeHeaderValue("application/json");
            var jw = new JsonTextWriter(new StreamWriter(memStream));
            jw.Formatting = Formatting.Indented;
            var serializer = new JsonSerializer();
            serializer.Serialize(jw, value);
            jw.Flush();
            memStream.Position = 0;
        }

        protected override Task SerializeToStreamAsync(Stream stream, TransportContext context)
        {
            return memStream.CopyToAsync(stream);
        }

        protected override bool TryComputeLength(out long length)
        {
            length = memStream.Length;
            return true;
        }
    }
}
