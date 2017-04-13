using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Used for bad input parameters sent in. 
    /// </summary>
    [Serializable]
    public class ValidationFailedException : Exception
    {
        public ValidationFailedException() : base() { }
        public ValidationFailedException(string message) : base(message) { }
    }
}
