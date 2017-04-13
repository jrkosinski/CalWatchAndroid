using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Used for auth token expired in requests where user identity is needed. 
    /// </summary>
    [Serializable]
    public class AuthExpiredException : Exception
    {
        public AuthExpiredException() : base() { }
        public AuthExpiredException(string message) : base(message) { }
    }
}
