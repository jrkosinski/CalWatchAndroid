using System;
using System.Linq;

namespace CalWatch.Api.Utilities
{
    public static class AuthTokenUtil
    {
        public static string GenerateNewAuthToken()
        {
            return Convert.ToBase64String(Guid.NewGuid().ToByteArray());
        }

        public static string GetAuthTokenFromRequestHeaders(System.Net.Http.Headers.HttpRequestHeaders headers)
        {
            if (headers != null)
            {
                var header = headers.FirstOrDefault(h => h.Key == "AuthToken");

                if (header.Value != null)
                    return header.Value.FirstOrDefault();
            }

            return null;
        }
    }
}
