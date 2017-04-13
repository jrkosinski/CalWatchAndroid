using System;

namespace CalWatch.Api.Utilities
{
    public static class JsonUtil
    {
        public static T JsonToObject<T>(string json)
        {
            return Newtonsoft.Json.JsonConvert.DeserializeObject<T>(json);
        }
    }
}
