using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Used for auth token expired in requests where user identity is needed. 
    /// </summary>
    [Serializable]
    public class AuthTokenInvalidException : Exception
    {
        public AuthTokenInvalidException() : base() { }
        public AuthTokenInvalidException(string message) : base(message) { }
    }
}
