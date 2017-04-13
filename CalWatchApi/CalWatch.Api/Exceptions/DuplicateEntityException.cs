using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Used for attempts to create duplicate record (e.g., user with existing username)
    /// </summary>
    [Serializable]
    public class DuplicateEntityException : Exception
    {
        public DuplicateEntityException() : base() { }
        public DuplicateEntityException(string message) : base(message) { }
    }
}
