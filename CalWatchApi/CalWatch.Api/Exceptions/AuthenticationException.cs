using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Used for errors authenticating (e.g. wrong username, wrong passwd)
    /// </summary>
    [Serializable]
    public class AuthenticationException : Exception
    {
        public AuthenticationException() : base() { }
        public AuthenticationException(string message) : base(message) { }
    }
}
